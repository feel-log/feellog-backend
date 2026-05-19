-- =========================================================
-- notification_settings BACKFILL
--
-- 1회성 백필 쿼리. 기존 가입 유저들에게 default settings row 생성.
-- 운영 배포 시 수동 실행 필요 (자동 실행 대상 아님).
--
-- 신규 가입자는 SocialAuthService.ensureNotificationSettings()에서
-- 자동 생성되므로, 이 쿼리는 NotificationSettings 도입 이전에 가입한
-- 기존 유저들만 대상으로 1회 실행하면 됨.
--
-- 멱등성: WHERE NOT IN 가드로 중복 실행해도 안전.
-- =========================================================

INSERT INTO notification_settings (user_id, push_enabled, created_at, updated_at)
SELECT user_id, TRUE, NOW(), NOW()
FROM users
WHERE user_id NOT IN (SELECT user_id FROM notification_settings);
