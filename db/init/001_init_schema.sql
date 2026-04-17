-- =========================================================
-- Feellog MVP 초기 스키마 (단순화 버전)
-- =========================================================

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS expense_situation_tag;
DROP TABLE IF EXISTS expense_emotion;
DROP TABLE IF EXISTS expense;
DROP TABLE IF EXISTS situation_tag;
DROP TABLE IF EXISTS emotion;
DROP TABLE IF EXISTS emotion_group;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS category_group;
DROP TABLE IF EXISTS users;

SET
FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- USERS (소셜 로그인 통합)
-- =========================================================
CREATE TABLE users
(
    user_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider         VARCHAR(20)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    nickname         VARCHAR(100) NOT NULL,
    birth_date       DATE,
    gender           VARCHAR(20),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at    DATETIME,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    deleted_at       DATETIME,
    CONSTRAINT uq_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT uq_email UNIQUE (email)
);

-- =========================================================
-- CATEGORY GROUP
-- =========================================================
CREATE TABLE category_group
(
    category_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL UNIQUE,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL
);

-- =========================================================
-- CATEGORY
-- =========================================================
CREATE TABLE category
(
    category_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_group_id BIGINT       NOT NULL,
    name              VARCHAR(100) NOT NULL UNIQUE,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    CONSTRAINT fk_category_group
        FOREIGN KEY (category_group_id)
            REFERENCES category_group (category_group_id)
);

-- =========================================================
-- EMOTION GROUP
-- =========================================================
CREATE TABLE emotion_group
(
    emotion_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL
);

-- =========================================================
-- EMOTION (이모지 제거, 이름만 사용)
-- =========================================================
CREATE TABLE emotion
(
    emotion_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    emotion_group_id BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    CONSTRAINT fk_emotion_group
        FOREIGN KEY (emotion_group_id)
            REFERENCES emotion_group (emotion_group_id)
);

-- =========================================================
-- SITUATION TAG
-- =========================================================
CREATE TABLE situation_tag
(
    situation_tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL
);

-- =========================================================
-- EXPENSE (핵심 테이블)
-- =========================================================
CREATE TABLE expense
(
    expense_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT         NOT NULL,
    category_id   BIGINT         NOT NULL,
    amount        DECIMAL(15, 2) NOT NULL,
    expense_date  DATE           NOT NULL,
    expense_time  TIME,
    merchant_name VARCHAR(150),
    memo          TEXT,
    is_deleted    BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at    DATETIME       NOT NULL,
    updated_at    DATETIME       NOT NULL,
    deleted_at    DATETIME,
    CONSTRAINT fk_expense_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),
    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id)
            REFERENCES category (category_id)
);

-- =========================================================
-- EXPENSE EMOTION (N:M)
-- =========================================================
CREATE TABLE expense_emotion
(
    expense_emotion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id         BIGINT   NOT NULL,
    emotion_id         BIGINT   NOT NULL,
    created_at         DATETIME NOT NULL,
    CONSTRAINT uq_expense_emotion UNIQUE (expense_id, emotion_id),
    CONSTRAINT fk_expense_emotion_expense
        FOREIGN KEY (expense_id)
            REFERENCES expense (expense_id),
    CONSTRAINT fk_expense_emotion_emotion
        FOREIGN KEY (emotion_id)
            REFERENCES emotion (emotion_id)
);

-- =========================================================
-- EXPENSE SITUATION TAG (N:M)
-- =========================================================
CREATE TABLE expense_situation_tag
(
    expense_situation_tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id               BIGINT   NOT NULL,
    situation_tag_id         BIGINT   NOT NULL,
    created_at               DATETIME NOT NULL,
    CONSTRAINT uq_expense_situation UNIQUE (expense_id, situation_tag_id),
    CONSTRAINT fk_expense_situation_expense
        FOREIGN KEY (expense_id)
            REFERENCES expense (expense_id),
    CONSTRAINT fk_expense_situation_tag
        FOREIGN KEY (situation_tag_id)
            REFERENCES situation_tag (situation_tag_id)
);