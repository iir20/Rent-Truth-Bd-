# Rent Truth BD — Database Schema Mapping 🗄️

This document describes the offline SQLite persistence architecture managed through the Room ORM module for the Android client-app and equivalent database diagrams for production structures.

---

## 🗺️ Entity Relationship Layout (Logical Structure)

```
  +--------------------------------+             +-------------------------------+
  |          listings              |             |        tenant_reviews         |
  +--------------------------------+             +-------------------------------+
  | (PK) id (Int, AutoGen)         |<----+       | (PK) id (Int, AutoGen)        |
  | title (String)                 |     +-------| listingId (Int)               |
  | area (String)                  |             | tenantName (String)           |
  | rentAmount (Int)               |             | cleanlinessRating (Float)     |
  | type (String)                  |             | ownerBehaviorRating (Float)   |
  | securityDeposit (Int)          |             | safetyRating (Float)          |
  | isVerifiedOwner (Boolean)      |             | waterElectricityRating (Float)|
  | isTrustedOwner (Boolean)       |             | reviewText (String)           |
  | availabilityState (String)     |             +-------------------------------+
  | complaintCount (Int)           |
  | isScamFlaggedByAI (Boolean)    |             +-------------------------------+
  | isBrokerListing (Boolean)      |             |         negotiations          |
  +--------------------------------+             +-------------------------------+
                                                 | (PK) id (Int, AutoGen)        |
  +--------------------------------+             | listingId (Int)               |
  |        manual_payments         |             | proposedRent (Int)            |
  +--------------------------------+             | status (String)               |
  | (PK) id (Int, AutoGen)         |             +-------------------------------+
  | senderNumber (String)          |
  | transactionId (String)         |             +-------------------------------+
  | amount (Int)                   |             |       escrow_deposits         |
  | purpose (String)               |             +-------------------------------+
  | status (String)                |             | (PK) id (Int, AutoGen)        |
  | userEmail (String)             |             | listingId (Int)               |
  | userName (String)              |             | amount (Int)                  |
  +--------------------------------+             | status (String)               |
                                                 +-------------------------------+
  +--------------------------------+
  |       roommate_requests        |
  +--------------------------------+
  | (PK) id (Int, AutoGen)         |
  | name (String)                  |
  | preferredArea (String)         |
  | budget (Int)                   |
  +--------------------------------+
```

---

## 🗃️ SQLite Database Table Structures

### 1. Table: `listings` (Rental Inventory)
Maintains details regarding verified and unverified properties posted on the platform.

| Column | Type | Nullable | Primary Key / Index | Description |
| :--- | :--- | :---: | :---: | :--- |
| `id` | INTEGER | No | PK (Autogenerate) | Unique identifier for physical property. |
| `title` | TEXT | No | - | Descriptive heading for real estate post. |
| `area` | TEXT | No | Index | Target rental zone eg Dhanmondi, Bashundhara. |
| `rentAmount` | INTEGER | No | Index | Monthly cost in BDT. |
| `type` | TEXT | No | - | Target occupant type: Bachelor, Sublet, Office. |
| `isVerifiedOwner` | INTEGER | No | - | Boolean flag validating NID deed coordinates. |
| `isTrustedOwner` | INTEGER | No | - | Boolean flag indicating higher security compliance. |
| `complaintCount` | INTEGER | No | - | Flagged warnings submitted by registered users. |
| `isScamFlaggedByAI` | INTEGER | No | - | Boolean trigger mapped in Gemini AI audits. |
| `isBrokerListing` | INTEGER | No | - | Boolean identifying whether broker handles deals. |
| `brokerCommission` | INTEGER | No | - | Fee in BDT declared up front. |
| `availabilityState` | TEXT | No | - | Current state: Vacant, Booked, Maintenance. |

### 2. Table: `manual_payments` (Subscription Billing Audits)
Tracks user mobile money screenshot submissions for owner and broker plan unlocks.

| Column | Type | Nullable | Primary Key / Index | Description |
| :--- | :--- | :---: | :---: | :--- |
| `id` | INTEGER | No | PK (Autogenerate) | Payment log identifier. |
| `senderNumber` | TEXT | No | - | bKash/Nagad/Rocket phone number used. |
| `transactionId` | TEXT | No | Unique Index | 10-char reference code to prevent double claims. |
| `amount` | INTEGER | No | - | Transaction charge in BDT. |
| `purpose` | TEXT | No | - | Target Subscription Plan or wallet recharge. |
| `status` | TEXT | No | Index | Lifecycle: Pending, Approved, Rejected, Refunded. |
| `userEmail` | TEXT | No | - | Email ID of user linked to the payment ledger. |

### 3. Table: `negotiations` (Direct Bidding)
Stores direct bargain counters bypassing standard pricing loops.

| Column | Type | Nullable | Primary Key / Index | Description |
| :--- | :--- | :---: | :---: | :--- |
| `id` | INTEGER | No | PK (Autogenerate) | Rent bid transaction ID. |
| `listingId` | INTEGER | No | FK ➔ listings.id | Triggers reference listing. |
| `proposedRent` | INTEGER | No | - | Proposed rent fee counter. |
| `status` | TEXT | No | - | Status state: Pending, Accepted, Rejected. |

---

## 🛠️ DAO Query Highlights (Data Integrity Rules)
All queries implement strict transactional standards in `RentDao.kt`:
1. `insertManualPayment`: Handled with `OnConflictStrategy.REPLACE` but audited using unique database indices to prevent dual transaction ID hacks.
2. `updateListingVerification(id, Boolean)`: Sets verified owners status triggers throughout dynamic list collections simultaneously to bypass race-conditions.
3. `incrementComplaint`: Synthesized directly inside SQLite query level counters to guarantee thread safety during simultaneous high concurrent traffic.
