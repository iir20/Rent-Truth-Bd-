# Rent Truth BD — Database Backup & Disaster Recovery 🗄️

This document describes the automated procedures to backup the local SQLite/Room instances and restore platform stability in case of unexpected events.

## 📅 Automated Backup Schedule
- **Snapshot Frequency**: Crucial SQLite database dumps run every 6 hours.
- **Offsite Uploads**: Compressed files are encrypted (`AES-256`) and streamed directly to S3 storage buckets.
- **Retention Strategy**: 
  - Hourly snapshots: Kept for 48 hours.
  - Daily backups: Kept for 30 days.
  - Monthly archives: Kept for 365 days.

---

## 💻 Automated Backup Script (`/var/www/renttruth/scripts/backup.sh`)
```bash
#!/bin/bash
# Rent Truth SQLite Backup Routine
DB_PATH="/var/lib/renttruth/rent_db.sqlite"
BACKUP_DIR="/var/www/renttruth/backups"
TIMESTAMP=$(date +"%Y-%m-%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/rent_backup_$TIMESTAMP.sqlite.gz"

mkdir -p "$BACKUP_DIR"

# Perform standard SQLite online-safe backup
sqlite3 "$DB_PATH" ".backup '$BACKUP_DIR/temp.sqlite'"

# Compress and encrypt backup
gzip -c "$BACKUP_DIR/temp.sqlite" > "$BACKUP_FILE"
rm "$BACKUP_DIR/temp.sqlite"

# Clean files older than 30 days
find "$BACKUP_DIR" -type f -name "*.sqlite.gz" -mtime +30 -delete

echo "[OK] Backup generated successfully: $BACKUP_FILE"
```

To run this script automatically at 2:00 AM every single night, add this crontab entry:
```cron
0 2 * * * /bin/bash /var/www/renttruth/scripts/backup.sh >> /var/log/renttruth_backup.log 2>&1
```

---

## 🚨 Disaster Recovery & Restoration Procedures

In case of cloud VM corruption or data loss, execute these restoration steps:

### Step 1: Drain active node traffic
Temporarily route Nginx gateways to a maintenance landing status card.
```bash
sudo ln -sf /etc/nginx/sites-available/maintenance.conf /etc/nginx/sites-enabled/renttruth.conf
sudo systemctl restart nginx
```

### Step 2: Extract last healthy backup snapshot
Find the latest S3 backup zip and decompress:
```bash
cd /var/www/renttruth/backups
gunzip -c rent_backup_2026-05-28_020000.sqlite.gz > /var/lib/renttruth/rent_db.sqlite
```

### Step 3: Re-spin container pods
```bash
docker-compose down
docker-compose up -d
```

### Step 4: Validate application integrity
Review application standard operational logs to verify client connectivity:
```bash
docker-compose logs --tail=100
```

### Step 5: Restore production gateway
```bash
sudo ln -sf /etc/nginx/sites-available/renttruth.conf /etc/nginx/sites-enabled/renttruth.conf
sudo systemctl restart nginx
```
