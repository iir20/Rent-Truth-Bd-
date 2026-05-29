import jwt from 'jsonwebtoken';
import { Request, Response, NextFunction } from 'express';

const ACCESS_SECRET = process.env.JWT_ACCESS_SECRET || 'renttruth_access_secret_token_key_2026_bd';
const REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'renttruth_refresh_secret_token_key_2026_bd';

export interface TokenPayload {
    username: string;
    email: string;
    phone: string;
    role: string;
}

export class AuthService {
    /**
     * Generates secure Access token and long-lived Rotation Refresh token pair
     */
    static generateTokens(payload: TokenPayload) {
        const cleanPayload = {
            username: payload.username,
            email: payload.email,
            phone: payload.phone,
            role: payload.role
        };

        const accessToken = jwt.sign(cleanPayload, ACCESS_SECRET, { expiresIn: '15m' });
        const refreshToken = jwt.sign(cleanPayload, REFRESH_SECRET, { expiresIn: '7d' });

        return {
            accessToken,
            refreshToken,
            tokenType: 'Bearer',
            expiresIn: 900 // 15 minutes
        };
    }

    /**
     * Verifies the authenticity and expiration status of a rotation refresh token
     */
    static verifyRefreshToken(token: string): TokenPayload {
        return jwt.verify(token, REFRESH_SECRET) as TokenPayload;
    }

    /**
     * Express middleware to enforce valid access tokens on REST and sync streams
     */
    static authenticateJWT(req: Request, res: Response, next: NextFunction) {
        const authHeader = req.headers.authorization;

        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Access token validation failed. Bearer credentials required.' });
        }

        const token = authHeader.split(' ')[1];

        try {
            const decoded = jwt.verify(token, ACCESS_SECRET) as TokenPayload;
            (req as any).user = decoded;
            next();
        } catch (err) {
            return res.status(403).json({ error: 'Expired or tampered token. Operational block.' });
        }
    }

    /**
     * Express middleware factory to restrict routes to specified user roles (e.g. OWNER, ADMIN, BROKER)
     */
    static restrictToRoles(...allowedRoles: string[]) {
        return (req: Request, res: Response, next: NextFunction) => {
            const user = (req as any).user as TokenPayload;
            if (!user || !allowedRoles.includes(user.role)) {
                return res.status(403).json({ error: 'Role access authorization mismatch. Access denied.' });
            }
            next();
        };
    }
}
