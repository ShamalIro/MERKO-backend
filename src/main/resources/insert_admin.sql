-- Insert default admin user for testing
-- Run this SQL script in your merco_db database

INSERT INTO admin (first_name, last_name, email, password, role, created_date, is_active) 
VALUES ('Admin', 'User', 'admin@merko.com', 'admin123', 'ADMIN', NOW(), true);

-- You can also add more admin users as needed
-- INSERT INTO admin (first_name, last_name, email, password, role, created_date, is_active) 
-- VALUES ('Super', 'Admin', 'superadmin@merko.com', 'superadmin123', 'ADMIN', NOW(), true);