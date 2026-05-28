# Rent Truth BD — Verified Rental Ecosystem for Bangladesh 🛡️🇧🇩

Rent Truth BD is a production-ready, highly secure, scalable, and decentralized digital property and tenant operating workspace designed for Bangladesh's real estate ecosystem. It solves long-standing challenges in the local market, including broker scams, fake home listings, hidden utilities fees, tenant harassment, tax evasion, and lack of verified landlord-tenant registries.

---

## 🏗️ Core Ecosystem Modules

### 1. 🏡 Landlord Digital Operations Workspace
Landlords are equipped with a complete property management console that archives historical tenancy, tracks monthly rent collection schedules, and preserves legal compliance logs:
- **Ecosystem Registry**: Tracks active renter records, historical tenant files, and lease files permanently. Archival files remain secure and accessible even after contract termination.
- **Verification Logs**: Manages ownership certification metadata, holding tax validation IDs, and corporate titles.
- **Complaint Database**: Logs resolved/pending neighborhood feedback to maintain transparent landlord behavior ratings.
- **Broker Delegation Monitor**: Connects listed properties with licensed, monitored broker IDs.

### 2. 👤 Tenant Digital Identity (KYC Shield)
Every citizen renter receives a permanent digital lease passport:
- **Editable Identity Keys**: Manage professional statuses, institution/company tags, student badges, and verified emergency contacts.
- **Multi-Document Vault**: Uploads and tracks state-recognized document uploads (NID, Smart NID, Birth Certificates, Passports, Driving Licenses, Student IDs, and Utility Bills).
- **OCR & Expiry Engine**: Includes version-tracked submissions, document safety scans, and active expiry dates.
- **Trust Scores**: Calculates a dynamic score ranging from 0–100 based on historic payment discipline, noise reports, and caretaking feedback.

### 3. 💼 Broker Commission, Mapping & Control Hub
Restricts broker monopoly and forces commission caps under Government Rental Guidelines:
- **Broker-Owner Partnership Routing**: Verifies listing authorization directly with the landlord.
- **Commission Auto-Splitting Setup**: Allocates residential and commercial deal commissions (e.g., 50/50 owner-broker splits or localized percentages).
- **Commercial Office Rentals Workflow**: Fully supports workspace scaling, corporate lease deeds, and customizable office split logs.
- **Badging & Reputation**: Displays verified licenses and verified scam resolution ratings.

### 4. 💳 Subscription & PAYMENT System (Secure Ledger)
A hybrid billing platform ensuring direct and traceable capital flows in BDT:
- **Role Restrictions**: Subscriptions are restricted to **Owners** and **Brokers** only (Tenants and general users enjoy free core access).
- **Manual Mobile Money Integration**: Supports audited manual transfers using **bKash**, **Nagad**, **Rocket**, and **Direct Bank Transfers** with screenshot uploads and Unique Transaction IDs.
- **Manual Review Flow**: Subscriptions activate automatically only after an administrator reviews the screenshot and transaction ID inside the Admin control desk.
- **Multi-State Lifecycle**: Synchronizes payment states through: `Pending` ➔ `Approved` ➔ `Rejected` ➔ `Expired` ➔ `Refunded`.

### 5. 🤖 Gemini AI Moderate & Advisor Engine
Integrated with state-of-the-art Generative AI for automated scam scanning and personalized, context-aware rental advice:
- **AI Listing Audits**: Flags suspicious rental pricing (under market average), fake coordinates, or recycle internet stock image footprints.
- **Interactive Room Advisor**: Conversational guidance tailored to transport budgets, safety checks, and landlord negotiation tactics.

---

## ⚡ Abstraction Blueprints for Automated Financial Scale
To prepare Rent Truth BD for national scaling, pre-configured architectural contracts are defined for future automated integrations:
- `PaymentGatewayIntegrator`: Direct billing API integrations with **SSLCommerz**, **Shurjopay**, or **bKash PG**.
- `SubscriptionScheduler`: Background workers managing automatic premium plan renewals.
- `AutomatedInvoiceGenerator`: Generates cryptographic PDF billing and digital smart receipts.
- `FinancialLedgerManager`: Double-entry accounting system managing platform commission splits.

---

## 🚀 Rapid Development & Tech Stack
- **Framework**: Jetpack Compose (Modern visual component stack)
- **Language**: Kotlin 
- **Database**: Room (Local SQLite verification store & transaction logs)
- **AI Core**: Gemini AI SDK wrapper (Server-side API keys secure injections)
- **Local Unit Verification**: Robolectric Visual & Screenshot Testing

---

## 📜 Repository Documentation Directories
For specialized operational guidance, refer to these root deployment resources:
1. `DEPLOYMENT.md` — Complete production server hosting steps (Docker, Nginx, PM2, AWS, Atlas).
2. `ENVIRONMENT_SETUP.md` — Local workspace compilation guides and secret key variables.
3. `DATABASE_SCHEMA.md` — SQLite table entities, property schema mappings, and DAO interface.
4. `ARCHITECTURE.md` — Deep MVVM block diagrams, clean architecture modules, and sync states.
5. `API_DOCUMENTATION.md` — Backend endpoints spec sheets, JWT permissions, and websocket contracts.
6. `CHANGELOG.md` — Complete development histories, version rollouts, and upgrade paths.
7. `SECURITY.md` — Encryption keys management, JWT token rotation mechanics, and malware scanning rules.
8. `CONTRIBUTING.md` — Team development standards, PR guidelines, and code quality formats.
9. `CODE_OF_CONDUCT.md` — Professional contributor behavior standards.
10. `LICENSE` — Software distribution terms and compliance keys.
