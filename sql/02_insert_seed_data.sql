-- ============================================================================
-- Blood Bank Management System - Seed Data
-- Run after 01_create_tables.sql
-- ============================================================================

-- ============================================================================
-- DEFAULT ADMIN USER (password: admin123)
-- BCrypt hash of 'admin123'
-- ============================================================================
INSERT INTO users (username, password_hash, full_name, email, role, is_active)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqBuBj.Kq5EzUGqZqZDZqZqZqZqZq', 
        'System Administrator', 'admin@bloodbank.com', 'ADMIN', 1);

INSERT INTO users (username, password_hash, full_name, email, role, is_active)
VALUES ('staff', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqBuBj.Kq5EzUGqZqZDZqZqZqZqZq', 
        'Staff User', 'staff@bloodbank.com', 'STAFF', 1);

-- ============================================================================
-- SAMPLE DONORS
-- ============================================================================
INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Rajesh Kumar', 'O+', '9876543210', 'rajesh.kumar@email.com', 
        '123 Anna Nagar, Chennai', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'MALE', 72.5, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Priya Sharma', 'A+', '9876543211', 'priya.sharma@email.com', 
        '456 T Nagar, Chennai', TO_DATE('1988-08-22', 'YYYY-MM-DD'), 'FEMALE', 58.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Mohammed Ali', 'B+', '9876543212', 'mohammed.ali@email.com', 
        '789 Mylapore, Chennai', TO_DATE('1992-03-10', 'YYYY-MM-DD'), 'MALE', 80.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Lakshmi Narayanan', 'AB+', '9876543213', 'lakshmi.n@email.com', 
        '321 Adyar, Chennai', TO_DATE('1985-11-30', 'YYYY-MM-DD'), 'FEMALE', 62.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Suresh Rajan', 'O-', '9876543214', 'suresh.rajan@email.com', 
        '654 Velachery, Chennai', TO_DATE('1995-01-25', 'YYYY-MM-DD'), 'MALE', 68.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Deepa Venkatesh', 'A-', '9876543215', 'deepa.v@email.com', 
        '987 Guindy, Chennai', TO_DATE('1991-07-18', 'YYYY-MM-DD'), 'FEMALE', 55.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Arun Prakash', 'B-', '9876543216', 'arun.p@email.com', 
        '147 Tambaram, Chennai', TO_DATE('1987-09-05', 'YYYY-MM-DD'), 'MALE', 75.0, 1);

INSERT INTO donors (full_name, blood_group, phone, email, address, date_of_birth, gender, weight_kg, is_eligible)
VALUES ('Kavitha Murthy', 'AB-', '9876543217', 'kavitha.m@email.com', 
        '258 Porur, Chennai', TO_DATE('1993-04-12', 'YYYY-MM-DD'), 'FEMALE', 60.0, 1);

-- ============================================================================
-- SAMPLE RECIPIENTS
-- ============================================================================
INSERT INTO recipients (full_name, blood_group, phone, email, address, date_of_birth, gender, hospital_name, doctor_name, medical_condition)
VALUES ('Ganesh Subramanian', 'A+', '9988776601', 'ganesh.s@email.com', 
        '100 Nungambakkam, Chennai', TO_DATE('1978-02-20', 'YYYY-MM-DD'), 'MALE',
        'Apollo Hospital', 'Dr. Ramesh', 'Scheduled surgery');

INSERT INTO recipients (full_name, blood_group, phone, email, address, date_of_birth, gender, hospital_name, doctor_name, medical_condition)
VALUES ('Meena Krishnan', 'O+', '9988776602', 'meena.k@email.com', 
        '200 Egmore, Chennai', TO_DATE('1982-06-14', 'YYYY-MM-DD'), 'FEMALE',
        'MIOT Hospital', 'Dr. Sunitha', 'Accident trauma');

INSERT INTO recipients (full_name, blood_group, phone, email, address, date_of_birth, gender, hospital_name, doctor_name, medical_condition)
VALUES ('Vijay Kumar', 'B+', '9988776603', 'vijay.k@email.com', 
        '300 Kodambakkam, Chennai', TO_DATE('1975-10-08', 'YYYY-MM-DD'), 'MALE',
        'Fortis Hospital', 'Dr. Karthik', 'Dialysis patient');

INSERT INTO recipients (full_name, blood_group, phone, email, address, date_of_birth, gender, hospital_name, doctor_name, medical_condition)
VALUES ('Saroja Devi', 'AB+', '9988776604', 'saroja.d@email.com', 
        '400 Ashok Nagar, Chennai', TO_DATE('1968-12-25', 'YYYY-MM-DD'), 'FEMALE',
        'SIMS Hospital', 'Dr. Venkat', 'Cancer treatment');

-- ============================================================================
-- BLOOD STOCK INVENTORY
-- ============================================================================
INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('O+', 'WHOLE_BLOOD', 25, SYSDATE - 10, SYSDATE + 32, 'Refrigerator A1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('O-', 'WHOLE_BLOOD', 10, SYSDATE - 5, SYSDATE + 37, 'Refrigerator A1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('A+', 'WHOLE_BLOOD', 20, SYSDATE - 8, SYSDATE + 34, 'Refrigerator A2', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('A-', 'WHOLE_BLOOD', 8, SYSDATE - 12, SYSDATE + 30, 'Refrigerator A2', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('B+', 'WHOLE_BLOOD', 15, SYSDATE - 7, SYSDATE + 35, 'Refrigerator B1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('B-', 'WHOLE_BLOOD', 5, SYSDATE - 15, SYSDATE + 27, 'Refrigerator B1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('AB+', 'WHOLE_BLOOD', 12, SYSDATE - 3, SYSDATE + 39, 'Refrigerator B2', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('AB-', 'WHOLE_BLOOD', 3, SYSDATE - 20, SYSDATE + 22, 'Refrigerator B2', 'AVAILABLE');

-- Plasma stock
INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('O+', 'PLASMA', 30, SYSDATE - 30, SYSDATE + 335, 'Freezer P1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('A+', 'PLASMA', 25, SYSDATE - 25, SYSDATE + 340, 'Freezer P1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('B+', 'PLASMA', 20, SYSDATE - 20, SYSDATE + 345, 'Freezer P2', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('AB+', 'PLASMA', 15, SYSDATE - 15, SYSDATE + 350, 'Freezer P2', 'AVAILABLE');

-- Platelets
INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('O+', 'PLATELETS', 8, SYSDATE - 1, SYSDATE + 4, 'Agitator T1', 'AVAILABLE');

INSERT INTO blood_stock (blood_group, component_type, units_available, collection_date, expiry_date, storage_location, status)
VALUES ('A+', 'PLATELETS', 6, SYSDATE - 2, SYSDATE + 3, 'Agitator T1', 'AVAILABLE');

-- ============================================================================
-- SAMPLE DONATIONS
-- ============================================================================
INSERT INTO donations (donor_id, donation_date, blood_group, component_type, volume_ml, hemoglobin_level, blood_pressure, pulse_rate, temperature_c, status, collected_by)
VALUES (1, SYSDATE - 30, 'O+', 'WHOLE_BLOOD', 450, 14.5, '120/80', 72, 36.6, 'COMPLETED', 'Nurse Revathi');

INSERT INTO donations (donor_id, donation_date, blood_group, component_type, volume_ml, hemoglobin_level, blood_pressure, pulse_rate, temperature_c, status, collected_by)
VALUES (2, SYSDATE - 25, 'A+', 'WHOLE_BLOOD', 450, 13.2, '118/78', 68, 36.5, 'COMPLETED', 'Nurse Revathi');

INSERT INTO donations (donor_id, donation_date, blood_group, component_type, volume_ml, hemoglobin_level, blood_pressure, pulse_rate, temperature_c, status, collected_by)
VALUES (3, SYSDATE - 20, 'B+', 'WHOLE_BLOOD', 450, 15.0, '122/82', 74, 36.7, 'COMPLETED', 'Nurse Kumar');

INSERT INTO donations (donor_id, donation_date, blood_group, component_type, volume_ml, hemoglobin_level, blood_pressure, pulse_rate, temperature_c, status, collected_by)
VALUES (5, SYSDATE - 15, 'O-', 'WHOLE_BLOOD', 450, 14.8, '116/76', 70, 36.4, 'COMPLETED', 'Nurse Kumar');

-- ============================================================================
-- SAMPLE BLOOD REQUESTS
-- ============================================================================
INSERT INTO blood_requests (recipient_id, blood_group, component_type, units_requested, priority, required_by_date, status, hospital_name, doctor_name, notes)
VALUES (1, 'A+', 'WHOLE_BLOOD', 2, 'NORMAL', SYSDATE + 5, 'PENDING', 'Apollo Hospital', 'Dr. Ramesh', 'Pre-surgery requirement');

INSERT INTO blood_requests (recipient_id, blood_group, component_type, units_requested, priority, required_by_date, status, hospital_name, doctor_name, notes)
VALUES (2, 'O+', 'WHOLE_BLOOD', 3, 'EMERGENCY', SYSDATE + 1, 'APPROVED', 'MIOT Hospital', 'Dr. Sunitha', 'Accident victim - urgent');

COMMIT;

-- Verify data
SELECT 'Users: ' || COUNT(*) FROM users;
SELECT 'Donors: ' || COUNT(*) FROM donors;
SELECT 'Recipients: ' || COUNT(*) FROM recipients;
SELECT 'Blood Stock: ' || COUNT(*) FROM blood_stock;
SELECT 'Donations: ' || COUNT(*) FROM donations;
SELECT 'Blood Requests: ' || COUNT(*) FROM blood_requests;
