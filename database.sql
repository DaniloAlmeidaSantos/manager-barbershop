-- =============================================================================
-- Manager Barbershop — DDL
-- Database: MySQL 8+
-- Encoding: utf8mb4
-- =============================================================================

CREATE DATABASE IF NOT EXISTS barber
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE barber;

-- =============================================================================
-- TB_SYSTEM_USERS
-- Stores authentication credentials for all system users (barbers).
-- Passwords must be hashed with Argon2 before insertion.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_SYSTEM_USERS (
    SYS_ID       INT          NOT NULL AUTO_INCREMENT,
    SYS_ROLE     VARCHAR(20)  NOT NULL,
    SYS_USERNAME VARCHAR(100) NOT NULL,
    SYS_PASSWORD VARCHAR(255) NOT NULL,

    CONSTRAINT PK_SYSTEM_USERS PRIMARY KEY (SYS_ID),
    CONSTRAINT UQ_SYS_USERNAME UNIQUE (SYS_USERNAME)
);

-- =============================================================================
-- TB_BARBER_LOCATION
-- Physical locations (units) of the barbershop.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_BARBER_LOCATION (
    LOCATION_ID           INT          NOT NULL AUTO_INCREMENT,
    LOCATION_COMPANY_NAME VARCHAR(150) NOT NULL,
    LOCATION_NAME         VARCHAR(150) NOT NULL,
    LOCATION_POSTAL_CODE  VARCHAR(10)  NOT NULL,
    LOCATION_COMPLEMENT   VARCHAR(100)     NULL,
    LOCATION_NUMBER       VARCHAR(10)  NOT NULL,
    LOCATION_STATE        CHAR(1)      NOT NULL,
    LOCATION_CITY         VARCHAR(100) NOT NULL,

    CONSTRAINT PK_BARBER_LOCATION PRIMARY KEY (LOCATION_ID)
);

-- =============================================================================
-- TB_CUSTOMERS
-- Registered customers. Passwords are hashed with Argon2.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_CUSTOMERS (
    CUSTOMER_ID       INT          NOT NULL AUTO_INCREMENT,
    CUSTOMER_NAME     VARCHAR(150) NOT NULL,
    CUSTOMER_PHONE    VARCHAR(20)  NOT NULL,
    CUSTOMER_PASSWORD VARCHAR(255) NOT NULL,
    CUSTOMER_EMAIL    VARCHAR(150) NOT NULL,

    CONSTRAINT PK_CUSTOMERS PRIMARY KEY (CUSTOMER_ID)
);

-- =============================================================================
-- TB_SERVICES
-- Catalog of services offered by the barbershop (e.g. haircut, beard trim).
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_SERVICES (
    SERVICE_ID        INT          NOT NULL AUTO_INCREMENT,
    SERVICE_TYPE_NAME VARCHAR(100) NOT NULL,

    CONSTRAINT PK_SERVICES PRIMARY KEY (SERVICE_ID)
);

-- =============================================================================
-- TB_BARBERS
-- Barber profiles. Each barber is linked to a system user (SYS_USER_ID)
-- for JWT authentication and to a physical location (BARBER_LOCATION_ID).
--
-- BARBER_ROLE values: ADMIN | EMPLOYEE
--   ADMIN    — can register new barbers and manage locations/config.
--   EMPLOYEE — can manage appointments only.
--
-- Bootstrap rule: the first barber registered is automatically ADMIN.
-- Requires SERVER_PRINCIPLE_IP to be set in TB_CONFIG beforehand.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_BARBERS (
    BARBER_ID          INT         NOT NULL AUTO_INCREMENT,
    BARBER_NAME        VARCHAR(150) NOT NULL,
    BARBER_LOCATION_ID INT              NULL,
    BARBER_ROLE        VARCHAR(10)  NOT NULL,
    SYS_USER_ID        INT              NULL,

    CONSTRAINT PK_BARBERS          PRIMARY KEY (BARBER_ID),
    CONSTRAINT FK_BARBER_LOCATION  FOREIGN KEY (BARBER_LOCATION_ID) REFERENCES TB_BARBER_LOCATION (LOCATION_ID),
    CONSTRAINT FK_BARBER_SYS_USER  FOREIGN KEY (SYS_USER_ID)        REFERENCES TB_SYSTEM_USERS    (SYS_ID),
    CONSTRAINT UQ_BARBER_SYS_USER  UNIQUE (SYS_USER_ID)
);

-- =============================================================================
-- TB_VALUES_SHEET
-- Maps which services each barber offers and their individual prices.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_VALUES_SHEET (
    SHEET_ID            INT            NOT NULL AUTO_INCREMENT,
    BARBER_ID           INT            NOT NULL,
    SERVICE_ID          INT            NOT NULL,
    SHEET_SERVICE_VALUE DOUBLE         NOT NULL,

    CONSTRAINT PK_VALUES_SHEET      PRIMARY KEY (SHEET_ID),
    CONSTRAINT FK_SHEET_BARBER      FOREIGN KEY (BARBER_ID)  REFERENCES TB_BARBERS  (BARBER_ID),
    CONSTRAINT FK_SHEET_SERVICE     FOREIGN KEY (SERVICE_ID) REFERENCES TB_SERVICES (SERVICE_ID)
);

-- =============================================================================
-- TB_SCHEDULE
-- Appointment records.
--
-- SCHEDULE_STATUS values:
--   A — Active   (confirmed)
--   P — Pending  (awaiting confirmation)
--   C — Cancelled
--   F — Finished
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_SCHEDULE (
    SCHEDULE_ID          INT         NOT NULL AUTO_INCREMENT,
    BARBER_ID            INT         NOT NULL,
    CUSTOMER_ID          INT         NOT NULL,
    SCHEDULE_START_TIME  DATETIME    NOT NULL,
    SCHEDULE_FINISH_TIME DATETIME    NOT NULL,
    SCHEDULE_STATUS      VARCHAR(1)  NOT NULL,
    SCHEDULE_SERVICES    VARCHAR(255)    NULL,

    CONSTRAINT PK_SCHEDULE          PRIMARY KEY (SCHEDULE_ID),
    CONSTRAINT FK_SCHEDULE_BARBER   FOREIGN KEY (BARBER_ID)   REFERENCES TB_BARBERS   (BARBER_ID),
    CONSTRAINT FK_SCHEDULE_CUSTOMER FOREIGN KEY (CUSTOMER_ID) REFERENCES TB_CUSTOMERS (CUSTOMER_ID)
);

-- =============================================================================
-- TB_CONFIG
-- Flexible key-value configuration store.
-- Scope resolution priority (highest to lowest):
--   1. BARBER_ID + LOCATION_ID both set  → barber-specific config
--   2. BARBER_ID null, LOCATION_ID set   → location-level config
--   3. BARBER_ID null, LOCATION_ID null  → global system config
--
-- Global config keys:
--   SERVER-PRINCIPLE-IP — IP allowed to perform the one-time ADMIN bootstrap.
--                         Must be inserted before the first barber registration.
-- =============================================================================
CREATE TABLE IF NOT EXISTS TB_CONFIG (
    CONFIG_ID    INT          NOT NULL AUTO_INCREMENT,
    CONFIG_NAME  VARCHAR(100) NOT NULL,
    CONFIG_VALUE VARCHAR(255) NOT NULL,
    LOCATION_ID  INT              NULL,
    BARBER_ID    INT              NULL,

    CONSTRAINT PK_CONFIG          PRIMARY KEY (CONFIG_ID),
    CONSTRAINT FK_CONFIG_LOCATION FOREIGN KEY (LOCATION_ID) REFERENCES TB_BARBER_LOCATION (LOCATION_ID),
    CONSTRAINT FK_CONFIG_BARBER   FOREIGN KEY (BARBER_ID)   REFERENCES TB_BARBERS         (BARBER_ID)
);

-- =============================================================================
-- BOOTSTRAP SETUP (run once before the first barber registration)
-- Replace 'SERVER_IP' with the actual IP of the machine that will register
-- the first ADMIN barber.
-- =============================================================================
INSERT INTO TB_CONFIG (CONFIG_NAME, CONFIG_VALUE, BARBER_ID, LOCATION_ID)
VALUES ('SERVER-PRINCIPLE-IP', 'SERVER-IP', NULL, NULL);
