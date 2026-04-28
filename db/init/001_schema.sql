-- =========================================================
-- Feellog MVP 초기 스키마 (단순화 버전)
-- =========================================================

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS review_result_template;
DROP TABLE IF EXISTS review_choice_option;
DROP TABLE IF EXISTS expense_situation_tag;
DROP TABLE IF EXISTS expense_emotion;
DROP TABLE IF EXISTS expense;
DROP TABLE IF EXISTS situation_tag;
DROP TABLE IF EXISTS emotion;
DROP TABLE IF EXISTS emotion_group;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS category_group;
DROP TABLE IF EXISTS refresh_tokens;
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

-- 👇 바로 여기 추가
-- =========================================================
-- REFRESH TOKENS
-- =========================================================
CREATE TABLE refresh_tokens
(
    refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,

    CONSTRAINT uq_refresh_token_user UNIQUE (user_id),
    CONSTRAINT uq_refresh_token UNIQUE (token),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
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

-- =========================================================
-- REVIEW CHOICE OPTION
-- =========================================================
CREATE TABLE review_choice_option
(
    review_choice_option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type           VARCHAR(50)  NOT NULL,
    option_text             VARCHAR(255) NOT NULL,
    option_value            VARCHAR(100) NOT NULL,
    score                   INT,
    sort_order              INT          NOT NULL DEFAULT 0,
    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              DATETIME     NOT NULL,
    updated_at              DATETIME     NOT NULL
);

-- =========================================================
-- REVIEW RESULT TEMPLATE
-- =========================================================
CREATE TABLE review_result_template
(
    review_result_template_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    emotion_id BIGINT NOT NULL,
    situation_tag_id BIGINT NOT NULL,
    satisfaction_option_id BIGINT NOT NULL,
    next_action_option_id BIGINT NOT NULL,

    title VARCHAR(150) NOT NULL,
    summary_text TEXT NOT NULL,

    feedback_title VARCHAR(150) NOT NULL,
    feedback_text TEXT NOT NULL,

    guide_title VARCHAR(150) NOT NULL,
    guide_item_1 VARCHAR(255) NOT NULL,
    guide_item_2 VARCHAR(255) NOT NULL,
    guide_item_3 VARCHAR(255) NOT NULL,

    priority INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT uq_review_result_template
        UNIQUE (emotion_id, situation_tag_id, satisfaction_option_id, next_action_option_id),

    CONSTRAINT fk_review_template_emotion
        FOREIGN KEY (emotion_id)
            REFERENCES emotion (emotion_id),

    CONSTRAINT fk_review_template_situation
        FOREIGN KEY (situation_tag_id)
            REFERENCES situation_tag (situation_tag_id),

    CONSTRAINT fk_review_template_satisfaction
        FOREIGN KEY (satisfaction_option_id)
            REFERENCES review_choice_option (review_choice_option_id),

    CONSTRAINT fk_review_template_next_action
        FOREIGN KEY (next_action_option_id)
            REFERENCES review_choice_option (review_choice_option_id)
);

-- =========================================================
-- REVIEW
-- =========================================================
CREATE TABLE review
(
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    review_date DATE NOT NULL,

    emotion_id BIGINT NOT NULL,
    situation_tag_id BIGINT NOT NULL,
    satisfaction_option_id BIGINT NOT NULL,
    next_action_option_id BIGINT NOT NULL,

    review_result_template_id BIGINT,

    title VARCHAR(150) NOT NULL,
    summary_text TEXT NOT NULL,

    feedback_title VARCHAR(150) NOT NULL,
    feedback_text TEXT NOT NULL,

    guide_title VARCHAR(150) NOT NULL,
    guide_item_1 VARCHAR(255) NOT NULL,
    guide_item_2 VARCHAR(255) NOT NULL,
    guide_item_3 VARCHAR(255) NOT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT uq_review_user_date
        UNIQUE (user_id, review_date),

    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),

    CONSTRAINT fk_review_emotion
        FOREIGN KEY (emotion_id)
            REFERENCES emotion (emotion_id),

    CONSTRAINT fk_review_situation
        FOREIGN KEY (situation_tag_id)
            REFERENCES situation_tag (situation_tag_id),

    CONSTRAINT fk_review_satisfaction
        FOREIGN KEY (satisfaction_option_id)
            REFERENCES review_choice_option (review_choice_option_id),

    CONSTRAINT fk_review_next_action
        FOREIGN KEY (next_action_option_id)
            REFERENCES review_choice_option (review_choice_option_id),

    CONSTRAINT fk_review_result_template
        FOREIGN KEY (review_result_template_id)
            REFERENCES review_result_template (review_result_template_id)
);