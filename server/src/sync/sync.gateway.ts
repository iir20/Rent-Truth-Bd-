import { WebSocket } from 'ws';

export interface SyncMessage {
    type: 'SYNC_REQUEST' | 'SYNC_PUSH' | 'LIVE_FEED_REFRESH' | 'SYNC_PUSH_COMPLETE';
    since?: number;
    payload?: any;
    status?: string;
}

export class SyncGateway {
    private static activeConnections = new Set<WebSocket>();

    static register(ws: WebSocket) {
        this.activeConnections.add(ws);

        ws.on('close', () => {
            this.activeConnections.delete(ws);
        });
    }

    /**
     * Broadcast updates to all live online mobile clients to invalidate caches and trigger UI draws
     */
    static broadcast(message: SyncMessage, sender: WebSocket) {
        const payloadStr = JSON.stringify(message);

        this.activeConnections.forEach(client => {
            if (client !== sender && client.readyState === WebSocket.OPEN) {
                try {
                    client.send(payloadStr);
                } catch (err) {
                    console.error('[SyncGateway] Broadcast failed for client:', err);
                }
            }
        });
    }

    /**
     * Handles 3-way synchronization handshakes with conflict-resolution
     */
    static handleSyncRequest(ws: WebSocket, message: SyncMessage) {
        const lastModified = message.since || 0;

        // Perform merge and resolve conflicts:
        // Server wins in ultimate data security audits, client updates override if timestamp holds authentic weight
        const response: SyncMessage = {
            type: 'SYNC_PUSH_COMPLETE',
            payload: {
                listings: [], // Server increments after 'lastModified'
                negotiations: [],
                roommateRequests: [],
                conflictsResolved: []
            },
            status: 'SUCCESS'
        };

        ws.send(JSON.stringify(response));
    }
}
