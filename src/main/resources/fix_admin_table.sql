-- Fix existing admin table to match the new Admin entity structure
-- Run this SQL script in your merco_db database

-- First, let's see the current structure
-- DESCRIBE admin;

-- Option 1: If your admin table has different column names, rename them
-- Uncomment and modify these as needed:

-- ALTER TABLE admin CHANGE COLUMN admin_id id BIGINT AUTO_INCREMENT;
-- ALTER TABLE admin CHANGE COLUMN admin_email email VARCHAR(255);
-- ALTER TABLE admin CHANGE COLUMN admin_password password VARCHAR(255);

-- Option 2: Add missing columns if they don't exist
-- Add first_name column if it doesn't exist
-- ALTER TABLE admin ADD COLUMN first_name VARCHAR(255) NOT NULL DEFAULT 'Admin';

-- Add last_name column if it doesn't exist  
-- ALTER TABLE admin ADD COLUMN last_name VARCHAR(255) NOT NULL DEFAULT 'User';

-- Add role column if it doesn't exist
-- ALTER TABLE admin ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'ADMIN';

-- Add is_active column if it doesn't exist
-- ALTER TABLE admin ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;

-- Add last_login column if it doesn't exist
-- ALTER TABLE admin ADD COLUMN last_login DATETIME(6) NULL;

-- Option 3: Fix created_date column issues
-- Update any invalid datetime values in created_date
UPDATE admin SET created_date = NOW() WHERE created_date = '0000-00-00 00:00:00' OR created_date IS NULL;

-- If created_date column doesn't exist, add it:
-- ALTER TABLE admin ADD COLUMN created_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

-- Option 4: If your table structure is completely different, create a backup and recreate:
-- CREATE TABLE admin_backup AS SELECT * FROM admin;
-- DROP TABLE admin;
-- 
-- CREATE TABLE admin (
--   id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   first_name VARCHAR(255) NOT NULL,
--   last_name VARCHAR(255) NOT NULL,
--   email VARCHAR(255) NOT NULL UNIQUE,
--   password VARCHAR(255) NOT NULL,
--   role VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
--   created_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
--   last_login DATETIME(6) NULL,
--   is_active BOOLEAN NOT NULL DEFAULT true
-- );
-- 
-- -- Insert your existing admin data back:
-- INSERT INTO admin (first_name, last_name, email, password, created_date, is_active)
-- SELECT 'Admin', 'User', email, password, NOW(), true FROM admin_backup;