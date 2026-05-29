-- RENT TRUTH BD MIGRATIONS
-- MIGRATION: 01_INIT_SCHEMA_20260529
-- Create initial schema tables, enums, triggers, and fallback indices

\connect renttruth_db

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO renttruth_user;

RAISE NOTICE 'Initializing database schema migration. Safe mapping is execution of tables setup.';
-- Direct execution of base schema
\i /usr/src/app/schema.sql
RAISE NOTICE 'Migration initialized completely. Base tables created.';
