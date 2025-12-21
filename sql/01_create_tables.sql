-- ============================================================================
-- Blood Bank Management System - Oracle Database Setup
-- Run this script as bloodbank user or SYSDBA
-- ============================================================================

-- Drop existing tables (in reverse order of dependencies)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE transfusions CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE donations CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE blood_requests CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE blood_stock CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE recipients CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE donors CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE users CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- Drop sequences
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE donors_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE recipients_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE blood_stock_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE donations_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE transfusions_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE blood_requests_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE users_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- ============================================================================
-- USERS TABLE
-- ============================================================================
CREATE TABLE users (
    id              NUMBER(19) PRIMARY KEY,
    username        VARCHAR2(50) NOT NULL UNIQUE,
    password_hash   VARCHAR2(255) NOT NULL,
    full_name       VARCHAR2(100) NOT NULL,
    email           VARCHAR2(100),
    role            VARCHAR2(20) DEFAULT 'STAFF' NOT NULL,
    is_active       NUMBER(1) DEFAULT 1 NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login      TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'STAFF', 'VIEWER'))
);

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER users_bi_trg
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT users_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER users_bu_trg
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- DONORS TABLE
-- ============================================================================
CREATE TABLE donors (
    id                  NUMBER(19) PRIMARY KEY,
    full_name           VARCHAR2(100) NOT NULL,
    blood_group         VARCHAR2(5) NOT NULL,
    phone               VARCHAR2(20),
    email               VARCHAR2(100),
    address             VARCHAR2(500),
    date_of_birth       DATE,
    gender              VARCHAR2(10),
    weight_kg           NUMBER(5,2),
    last_donation_date  DATE,
    total_donations     NUMBER(10) DEFAULT 0,
    is_eligible         NUMBER(1) DEFAULT 1,
    medical_notes       VARCHAR2(1000),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_blood_group CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

CREATE SEQUENCE donors_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER donors_bi_trg
BEFORE INSERT ON donors
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT donors_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER donors_bu_trg
BEFORE UPDATE ON donors
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- RECIPIENTS TABLE
-- ============================================================================
CREATE TABLE recipients (
    id                  NUMBER(19) PRIMARY KEY,
    full_name           VARCHAR2(100) NOT NULL,
    blood_group         VARCHAR2(5) NOT NULL,
    phone               VARCHAR2(20),
    email               VARCHAR2(100),
    address             VARCHAR2(500),
    date_of_birth       DATE,
    gender              VARCHAR2(10),
    hospital_name       VARCHAR2(200),
    doctor_name         VARCHAR2(100),
    medical_condition   VARCHAR2(500),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_recip_blood_group CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    CONSTRAINT chk_recip_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

CREATE SEQUENCE recipients_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER recipients_bi_trg
BEFORE INSERT ON recipients
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT recipients_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER recipients_bu_trg
BEFORE UPDATE ON recipients
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- BLOOD STOCK TABLE
-- ============================================================================
CREATE TABLE blood_stock (
    id              NUMBER(19) PRIMARY KEY,
    blood_group     VARCHAR2(5) NOT NULL,
    component_type  VARCHAR2(30) DEFAULT 'WHOLE_BLOOD' NOT NULL,
    units_available NUMBER(10) DEFAULT 0 NOT NULL,
    unit_volume_ml  NUMBER(10) DEFAULT 450,
    collection_date DATE,
    expiry_date     DATE,
    storage_location VARCHAR2(100),
    status          VARCHAR2(20) DEFAULT 'AVAILABLE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stock_blood_group CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    CONSTRAINT chk_component_type CHECK (component_type IN ('WHOLE_BLOOD', 'PLASMA', 'PLATELETS', 'RBC', 'WBC')),
    CONSTRAINT chk_stock_status CHECK (status IN ('AVAILABLE', 'RESERVED', 'EXPIRED', 'DISCARDED'))
);

CREATE SEQUENCE blood_stock_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER blood_stock_bi_trg
BEFORE INSERT ON blood_stock
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT blood_stock_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER blood_stock_bu_trg
BEFORE UPDATE ON blood_stock
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- DONATIONS TABLE
-- ============================================================================
CREATE TABLE donations (
    id                  NUMBER(19) PRIMARY KEY,
    donor_id            NUMBER(19) NOT NULL,
    donation_date       DATE DEFAULT SYSDATE NOT NULL,
    blood_group         VARCHAR2(5) NOT NULL,
    component_type      VARCHAR2(30) DEFAULT 'WHOLE_BLOOD',
    volume_ml           NUMBER(10) DEFAULT 450,
    hemoglobin_level    NUMBER(5,2),
    blood_pressure      VARCHAR2(20),
    pulse_rate          NUMBER(5),
    temperature_c       NUMBER(4,1),
    status              VARCHAR2(20) DEFAULT 'COMPLETED',
    notes               VARCHAR2(1000),
    collected_by        VARCHAR2(100),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donation_donor FOREIGN KEY (donor_id) REFERENCES donors(id),
    CONSTRAINT chk_donation_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'REJECTED'))
);

CREATE SEQUENCE donations_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER donations_bi_trg
BEFORE INSERT ON donations
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT donations_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- BLOOD REQUESTS TABLE
-- ============================================================================
CREATE TABLE blood_requests (
    id                  NUMBER(19) PRIMARY KEY,
    recipient_id        NUMBER(19) NOT NULL,
    blood_group         VARCHAR2(5) NOT NULL,
    component_type      VARCHAR2(30) DEFAULT 'WHOLE_BLOOD',
    units_requested     NUMBER(10) NOT NULL,
    units_fulfilled     NUMBER(10) DEFAULT 0,
    priority            VARCHAR2(20) DEFAULT 'NORMAL',
    request_date        DATE DEFAULT SYSDATE,
    required_by_date    DATE,
    status              VARCHAR2(20) DEFAULT 'PENDING',
    hospital_name       VARCHAR2(200),
    doctor_name         VARCHAR2(100),
    notes               VARCHAR2(1000),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_recipient FOREIGN KEY (recipient_id) REFERENCES recipients(id),
    CONSTRAINT chk_request_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'EMERGENCY')),
    CONSTRAINT chk_request_status CHECK (status IN ('PENDING', 'APPROVED', 'PARTIALLY_FULFILLED', 'FULFILLED', 'CANCELLED'))
);

CREATE SEQUENCE blood_requests_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER blood_requests_bi_trg
BEFORE INSERT ON blood_requests
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT blood_requests_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER blood_requests_bu_trg
BEFORE UPDATE ON blood_requests
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- TRANSFUSIONS TABLE
-- ============================================================================
CREATE TABLE transfusions (
    id                  NUMBER(19) PRIMARY KEY,
    recipient_id        NUMBER(19) NOT NULL,
    blood_stock_id      NUMBER(19),
    blood_request_id    NUMBER(19),
    blood_group         VARCHAR2(5) NOT NULL,
    component_type      VARCHAR2(30) DEFAULT 'WHOLE_BLOOD',
    units_transfused    NUMBER(10) NOT NULL,
    transfusion_date    DATE DEFAULT SYSDATE NOT NULL,
    administered_by     VARCHAR2(100),
    notes               VARCHAR2(1000),
    reaction_observed   NUMBER(1) DEFAULT 0,
    reaction_details    VARCHAR2(500),
    status              VARCHAR2(20) DEFAULT 'COMPLETED',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfusion_recipient FOREIGN KEY (recipient_id) REFERENCES recipients(id),
    CONSTRAINT fk_transfusion_stock FOREIGN KEY (blood_stock_id) REFERENCES blood_stock(id),
    CONSTRAINT fk_transfusion_request FOREIGN KEY (blood_request_id) REFERENCES blood_requests(id),
    CONSTRAINT chk_transfusion_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE SEQUENCE transfusions_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER transfusions_bi_trg
BEFORE INSERT ON transfusions
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT transfusions_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_at := CURRENT_TIMESTAMP;
END;
/

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================
CREATE INDEX idx_donors_blood_group ON donors(blood_group);
CREATE INDEX idx_donors_phone ON donors(phone);
CREATE INDEX idx_donors_eligible ON donors(is_eligible);

CREATE INDEX idx_recipients_blood_group ON recipients(blood_group);

CREATE INDEX idx_blood_stock_group ON blood_stock(blood_group);
CREATE INDEX idx_blood_stock_status ON blood_stock(status);
CREATE INDEX idx_blood_stock_expiry ON blood_stock(expiry_date);

CREATE INDEX idx_donations_donor ON donations(donor_id);
CREATE INDEX idx_donations_date ON donations(donation_date);

CREATE INDEX idx_requests_recipient ON blood_requests(recipient_id);
CREATE INDEX idx_requests_status ON blood_requests(status);
CREATE INDEX idx_requests_priority ON blood_requests(priority);

CREATE INDEX idx_transfusions_recipient ON transfusions(recipient_id);
CREATE INDEX idx_transfusions_date ON transfusions(transfusion_date);

COMMIT;

-- Verify tables created
SELECT table_name FROM user_tables ORDER BY table_name;
