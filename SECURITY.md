# Rent Truth BD — Security Hardening & Compliance 🛡️

Rent Truth BD prioritizes security compliance, data safety, and financial ledger integrity to maintain trust in the Bangladeshi housing ecosystem.

---

## 🛡️ Identity Protection & JWT Token Rotation

1. **Role-Based Authorization (RBAC)**: All routes enforce strict access locks. Tenants are blocked from accessing owner workspaces, landlord registries, or broker splitting functions.
2. **Access Token Lifetimes**: Brief Token Lives (`15 Minutes`) paired with Secure HTTP-Only Refresh Tokens securely cached inside client memory.
3. **Session Revocations**: Users can review their active device logs (containing Geolocation IP references and platform user agents) and trigger immediate global access revocations.

---

## 🗄️ Database & Document Storage Vault

### 1. Protection Against SQL Injections
Our queries utilize strictly prepared statements and high-abstraction ORM schemas:
- Built-in parameterization within SQLite Room interfaces prevents input sanitation attacks in localized data blocks.
- Node.js backend controllers run strict escaping mechanisms alongside validation middleware schemas.

### 2. Encrypted Document Storage (KYC Vault)
Identity documents represent critical private data files. When users upload NIDs, Smart NIDs, or utility bill screenshots:
- Storage assets are encrypted under AES-256 standards before sinking to the cloud.
- Scanned documents are isolated behind private presigned URL tokens checking valid active landlord-tenant relationship permissions.
- In-flight malware scanning placeholders evaluate file signatures to intercept executable inject attacks.

---

## 📞 Security Reporting & Reporting Bugs

Please do **NOT** submit security vulnerabilities or exploits directly into public GitHub issues.

If you identify a security gap or loophole, notify our operations team by sending an encrypted message to:
`security@renttruthbd.com`

Our response guidelines guarantee:
- Initial log acknowledgement within `24 hours`.
- Patched audit rollouts mapped within `72 hours`.
- Clean public verification acknowledgements on release changelogs.
