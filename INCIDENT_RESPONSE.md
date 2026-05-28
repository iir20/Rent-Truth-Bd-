# Rent Truth BD — Emergency Incident Response Plan 🚨

This document details the step-by-step instructions to follow in case of a system security breach, data leak, NID verification fraud anomaly, or severe service degradation.

## 🏁 Critical Incident Levels

### 🔴 Level 1: Extreme Severities (NID Database Corrupt / Account Hijack)
- **Indicators**: High volumes of automated lockouts, fake NID documents successfully overriding OCR validators, or severe unauthorized DB edits.
- **Responsibility**: System Lead Security Engineer and Administrative Moderators.

### 🟡 Level 2: Moderate Anomaly (DDoS / Isolated Broker Fraud)
- **Indicators**: Sudden spikes in manual payment transaction retry loops, websocket congestion over 2000 concurrent backlog events.
- **Responsibility**: DevOps and Platform Infrastructure Team.

---

## 🛠️ Step-by-Step Response Protocol

### Step 1: Secure & Contain the Breach
Immediately block any automated scraper engines or target IP regions routing malicious payloads.
```bash
# Block specific hostile IP subnet via iptables Firewall
sudo iptables -A INPUT -s 190.2.14.0/24 -j DROP
```

### Step 2: Enable Edge Mode Maintenance
Instantly redirect all endpoints to maintenance pages to audit live DB schemas and preserve session states:
```bash
sudo mv /etc/nginx/sites-enabled/rent_prod /etc/nginx/sites-enabled/rent_prod.bak
sudo ln -sf /etc/nginx/sites-available/maintenance /etc/nginx/sites-enabled/rent_prod
sudo systemctl reload nginx
```

### Step 3: Revoke Compromised Device Sessions
Utilize the Admin Command Panel's "Session Anomaly Detection" tools to trigger a platform-wide token rotation. Live database tables are updated to immediately invalidate expired tokens and enforce physical MFA validation.

### Step 4: Run OCR Pipeline Scans
Scan for duplicate submitted document fingerprints to catch and ban copy-paste brokers attempting listing hijackings:
```bash
# List document scan duplications matching existing government registries
node bin/audit_docs.js --checkForDuplicates
```

### Step 5: Post-Incident Forensic Logging
Record details of the intrusion in `/var/log/renttruth/incident_telemetry.log`. Gather telemetry, including:
1. Origin IP addresses and geolocation routing info.
2. Target REST API endpoint names and query payloads.
3. Total number of impacted rental accounts or listings.
4. Total duration of down-time before service was completely restored.
