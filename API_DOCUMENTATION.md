# Rent Truth BD — API Documentation 📑

This document describes the REST API routing structures, request payloads, response models, and WebSocket channels configured for the production deployment of Rent Truth BD.

---

## 🔐 Authentication & Session Headers

Every secure transaction requires a bearer JWT (JSON Web Token) supplied inside the header:

```http
Authorization: Bearer <your_rotated_access_token>
```

---

## 🛣️ core REST API routes

### 1. User & Identity Service

#### `POST /api/v1/auth/register`
Creates a brand new tenant, landlord, or broker identity keys record:
- **Request Payload**:
```json
{
  "username": "Ratul Ahmed",
  "email": "ratul@school.edu",
  "phone": "01722334455",
  "role": "TENANT"
}
```
- **Response (201 Created)**:
```json
{
  "status": "success",
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "user": {
    "username": "Ratul Ahmed",
    "email": "ratul@school.edu",
    "walletBalance": 800,
    "role": "TENANT"
  }
}
```

---

### 2. Listings & Auditing Service

#### `GET /api/v1/listings`
Fetches verified property lists based on area or budget:
- **Filters**: `area` (Dhanmondi, Uttara), `rent_max`, `role_type`.
- **Response (200 OK)**:
```json
[
  {
    "id": 1,
    "title": "Verified Dhanmondi Bachelor Room",
    "area": "Dhanmondi",
    "rentAmount": 8500,
    "availabilityState": "Vacant",
    "isVerifiedOwner": true,
    "complaintCount": 0
  }
]
```

---

### 3. Subscription & Billing Desk

#### `POST /api/v1/billing/manual-payment`
Submits transactional screenshot metadata for manual administrator verification:
- **Request Payload**:
```json
{
  "senderNumber": "01711223344",
  "transactionId": "TXN99BKA10",
  "amount": 1500,
  "paymentMethod": "bKash",
  "purpose": "Upgrade_Owner"
}
```
- **Response (202 Accepted)**:
```json
{
  "status": "pending_manual_audit",
  "message": "Payment TXN99BKA10 logged. Plan will elevate once approved by Admin Desk."
}
```

---

## ⚡ Real-Time WebSocket Channel (`/sync/`)
Provides bidirectional WebSocket tunnels for real-time rent synchronization (`RentSync` protocol).

### 1. Client Heartbeat (Every 30 seconds):
- **Client Sending**:
```json
{
  "event": "heartbeat",
  "token": "bearer_jwt_key"
}
```
- **Server Response**:
```json
{
  "event": "heartbeat_ack",
  "timestamp": "2026-05-28T08:48:00Z"
}
```

### 2. Live Booking and Agreement Broadcast:
When an owner accepts any tenant booking, a real-time event propagates matching clients:
```json
{
  "event": "booking_state_changed",
  "listingId": 14,
  "status": "Booked",
  "occupiedBy": "Ratul Ahmed",
  "verificationHash": "sha256_checksum_logs"
}
```
