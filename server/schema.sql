-- RENT TRUTH BD SQL SCHEMA SPECIFICATION (SUPABASE / POSTGRESQL)
-- Production deployment instructions for Bangladesh real-estate matching database

BEGIN;

-- 1. Create Enums for Users & System States
CREATE TYPE user_role AS ENUM ('TENANT', 'OWNER', 'BROKER', 'ADMIN');
CREATE TYPE doc_type AS ENUM ('NID', 'SMART_NID', 'BIRTH_CERTIFICATE', 'PASSPORT', 'DRIVING_LICENSE', 'STUDENT_ID', 'UTILITY_BILL');
CREATE TYPE doc_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'RESUBMIT');
CREATE TYPE listing_category AS ENUM ('FAMILY', 'BACHELOR', 'SUBLET', 'OFFICE', 'HOSTEL', 'MESS');

-- Disable triggers or constraints during bulk sync imports if needed
SET CONSTRAINTS ALL DEFERRED;

-- 2. User Profiles Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role DEFAULT 'TENANT',
    subscription_type VARCHAR(50) DEFAULT 'Free', -- Free, Student Premium, Owner Platinum, Broker Pro
    wallet_balance INTEGER DEFAULT 1000,
    is_verified BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Rent Listings Table (Syncs with RentListing Room entity)
CREATE TABLE IF NOT EXISTS rent_listings (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    rent_amount INTEGER NOT NULL,
    service_charge INTEGER DEFAULT 0,
    bedrooms INTEGER DEFAULT 1,
    bathrooms INTEGER DEFAULT 1,
    category VARCHAR(50) NOT NULL, -- Family, Sublet, Bachelor, Hostel, Mess, Office
    description TEXT,
    photos TEXT, -- Comma separated URL elements
    owner_id VARCHAR(100) NOT NULL,
    owner_phone VARCHAR(50) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    added_at VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION DEFAULT 23.8103,
    longitude DOUBLE PRECISION DEFAULT 90.4125,
    has_gas BOOLEAN DEFAULT FALSE,
    has_water BOOLEAN DEFAULT FALSE,
    has_lift BOOLEAN DEFAULT FALSE,
    is_security_verified BOOLEAN DEFAULT FALSE,
    verification_badge VARCHAR(50) DEFAULT 'Unverified',
    local_last_modified BIGINT DEFAULT 0
);

-- 4. Negotiations / Rent Offers (Syncs with Negotiation Room entity)
CREATE TABLE IF NOT EXISTS negotiations (
    id VARCHAR(100) PRIMARY KEY,
    listing_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    tenant_name VARCHAR(100) NOT NULL,
    tenant_phone VARCHAR(50) NOT NULL,
    original_rent INTEGER NOT NULL,
    offered_rent INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending', -- Pending, Accepted, Declined, Countered, Completed
    counter_offer_amount INTEGER DEFAULT 0,
    is_escrow_deposited BOOLEAN DEFAULT FALSE,
    agreed_at VARCHAR(100)
);

-- 5. Roommate Requests Table (Syncs with RoommateRequest Room entity)
CREATE TABLE IF NOT EXISTS roommate_requests (
    id VARCHAR(100) PRIMARY KEY,
    requester_name VARCHAR(100) NOT NULL,
    requester_phone VARCHAR(50) NOT NULL,
    religion VARCHAR(50) DEFAULT 'Any',
    profession VARCHAR(100) DEFAULT 'Student',
    rent_contribution INTEGER NOT NULL,
    area_preference VARCHAR(255) NOT NULL,
    description TEXT,
    posted_at VARCHAR(100) NOT NULL,
    isActive BOOLEAN DEFAULT TRUE
);

-- 6. Tenant Reviews (Syncs with TenantReview Room entity)
CREATE TABLE IF NOT EXISTS tenant_reviews (
    id VARCHAR(100) PRIMARY KEY,
    tenant_nid VARCHAR(100) NOT NULL,
    tenant_name VARCHAR(100) NOT NULL,
    review_text TEXT,
    rating INTEGER DEFAULT 5,
    reporting_owner_name VARCHAR(100) NOT NULL,
    date_posted VARCHAR(100) NOT NULL,
    isApproved BOOLEAN DEFAULT FALSE
);

-- 7. Escrow Deposits Table (Syncs with EscrowDeposit Room entity)
CREATE TABLE IF NOT EXISTS escrow_deposits (
    id VARCHAR(100) PRIMARY KEY,
    negotiation_id VARCHAR(100) NOT NULL,
    amount INTEGER NOT NULL,
    tenant_name VARCHAR(100) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'Locked', -- Locked, Released, RefundRequested, Refunded, Disputed
    bKash_trx_id VARCHAR(100),
    locked_date VARCHAR(100) NOT NULL,
    released_date VARCHAR(100)
);

-- 8. Manual Payments Table (Syncs with ManualPaymentSubmission Room entity)
CREATE TABLE IF NOT EXISTS manual_payments (
    id VARCHAR(100) PRIMARY KEY,
    sender_name VARCHAR(100) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- bKash, Nagad, Rocket
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount INTEGER NOT NULL,
    submission_date VARCHAR(100) NOT NULL,
    target_listing_id VARCHAR(100) DEFAULT '',
    purpose VARCHAR(100) DEFAULT 'Verification Deposit',
    status VARCHAR(50) DEFAULT 'Pending' -- Pending, Approved, Rejected
);

-- 9. Real-time Security Sync Audit Logs
CREATE TABLE IF NOT EXISTS sync_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(100) NOT NULL,
    action_type VARCHAR(50) NOT NULL, -- INSERT, UPDATE, DELETE, CONFLICT_MERGE
    table_name VARCHAR(100) NOT NULL,
    record_id VARCHAR(100) NOT NULL,
    performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    payload JSONB
);

-- 10. PostgreSQL Performance Optimization Indexes (Fast search by NID, phone, and coordinates)
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_listings_rent ON rent_listings(rent_amount);
CREATE INDEX IF NOT EXISTS idx_listings_loc ON rent_listings(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_reviews_nid ON tenant_reviews(tenant_nid);
CREATE INDEX IF NOT EXISTS idx_payments_trx ON manual_payments(transaction_id);

COMMIT;
