import express, { Request, Response, NextFunction } from 'express';
import { createServer } from 'http';
import { WebSocketServer, WebSocket } from 'ws';
import cors from 'cors';
import rateLimit from 'express-rate-limit';
import { AuthService } from './auth/auth.service';
import { PaymentService } from './payments/bkash.service';

const app = express();
const httpServer = createServer(app);
const wss = new WebSocketServer({ server: httpServer, path: '/sync' });

const PORT = process.env.PORT || 5000;

// Parsers & Security Middlewares
app.use(cors({ origin: '*' }));
app.use(express.json({ limit: '10mb' }));

// Global Rate Limiter to guard against DDoS or brute-force sync attacks
const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100, // limit each IP to 100 requests per windowMs
    message: { error: 'Too many requests from this IP. Please try again after 15 minutes.' },
    standardHeaders: true,
    legacyHeaders: false
});
app.use('/api/', apiLimiter);

// 1. Health Status Endpoint
app.get('/api/health', (req: Request, res: Response) => {
    res.status(200).json({
        status: 'UP',
        timestamp: new Date().toISOString(),
        service: 'Rent Truth BD REST and Sync Gateway',
        environment: process.env.NODE_ENV || 'development'
    });
});

// 2. JWT Authentication Endpoint Group
app.post('/api/auth/register', async (req: Request, res: Response) => {
    try {
        const { username, email, phone, password, role } = req.body;
        // Mock DB storage & validation
        const user = { username, email, phone, role: role || 'TENANT' };
        const tokens = AuthService.generateTokens(user);
        res.status(201).json({ user, ...tokens });
    } catch (err: any) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/auth/login', async (req: Request, res: Response) => {
    try {
        const { email, password } = req.body;
        const mockUser = { username: 'Riyad Ahmed', email, phone: '01712345678', role: 'TENANT' };
        const tokens = AuthService.generateTokens(mockUser);
        res.status(200).json({ user: mockUser, ...tokens });
    } catch (err: any) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/auth/refresh', async (req: Request, res: Response) => {
    const { refreshToken } = req.body;
    if (!refreshToken) return res.status(400).json({ error: 'Refresh token is required' });
    try {
        const decoded = AuthService.verifyRefreshToken(refreshToken);
        const tokens = AuthService.generateTokens(decoded);
        res.status(200).json({ ...tokens });
    } catch (err) {
        res.status(401).json({ error: 'Invalid or expired refresh token' });
    }
});

// 3. Payment Gateway Integration Support (bKash & Nagad APIs)
app.post('/api/payments/bkash/create', AuthService.authenticateJWT, async (req: Request, res: Response) => {
    try {
        const { amount, purpose, listingId } = req.body;
        const result = await PaymentService.createBkashPayment(amount, purpose, listingId);
        res.status(200).json(result);
    } catch (err: any) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/payments/bkash/callback', async (req: Request, res: Response) => {
    try {
        const { paymentID, status, trxID } = req.query;
        const result = await PaymentService.verifyTransaction(paymentID as string, trxID as string, status as string);
        res.status(200).json(result);
    } catch (err: any) {
        res.status(500).json({ error: err.message });
    }
});

// Active Web Sockets pool for real-time notifications and synchronized ledger push
const connectedClients = new Set<WebSocket>();

// 4. WebSocket Coordination Hub for Local Room DB Offline Sync Writes
wss.on('connection', (ws: WebSocket, req) => {
    connectedClients.add(ws);
    console.log(`[WebSocket] Mobile Client linked. Connected pool size: ${connectedClients.size}`);

    ws.on('message', (message: string) => {
        try {
            const data = JSON.parse(message);
            console.log(`[WebSocket] Received Sync Packet:`, data.type);

            // Handle client synchronize mutations
            if (data.type === 'SYNC_REQUEST') {
                const clientLastModified = data.since || 0;
                
                // Diff data logs and push conflict-resilient updates
                ws.send(JSON.stringify({
                    type: 'SYNC_RESPONSE',
                    since: Date.now(),
                    listings: [], // Server push additions modified since clientLastModified
                    escapedConflictKeys: []
                }));
            } else if (data.type === 'SYNC_PUSH') {
                // Client pushing local changes made while offline
                const { listings, negotiations, roommateRequests, manualPayments } = data.payload;
                console.log(`[WebSocket] Synchronizing outbound client payload queue, items: ${listings?.length || 0}`);

                // Broadcast state to all other connected instances to trigger live UI feed refresh
                connectedClients.forEach(client => {
                    if (client !== ws && client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({
                            type: 'LIVE_FEED_REFRESH',
                            payload: { listings, negotiations, roommateRequests }
                        }));
                    }
                });

                ws.send(JSON.stringify({
                    status: 'ACK',
                    type: 'SYNC_PUSH_COMPLETE',
                    syncedAt: Date.now()
                }));
            }
        } catch (err) {
            console.error('[WebSocket] Parsing anomaly:', err);
        }
    });

    ws.on('close', () => {
        connectedClients.delete(ws);
        console.log(`[WebSocket] Client disconnected. Pool size: ${connectedClients.size}`);
    });
});

// Error handling fallback middleware
app.use((err: any, req: Request, res: Response, next: NextFunction) => {
    console.error(err.stack);
    res.status(500).json({ error: 'Server anomaly. Integrity intact.' });
});

httpServer.listen(PORT, () => {
    console.log(`⚡ Rent Truth BD Server Gateway online on port ${PORT}`);
    console.log(`📡 WebSocket Synchronizer listening at /sync`);
});
