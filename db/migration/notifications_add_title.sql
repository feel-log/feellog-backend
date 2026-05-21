-- =========================================================
-- notifications ADD COLUMN title
--
-- 1회성 스키마 변경. 기존 알림함 레코드에는 title이 없었으므로 nullable.
-- 신규 알림은 ExpenseReminderMessage / DailyReviewMessage enum에서
-- title을 항상 채워 저장하므로 실질적으로 null은 기존 레코드뿐.
--
-- 배포 순서:
--   1) 본 SQL 운영 DB 적용 (ddl-auto: validate라 컬럼 없으면 앱 시작 실패)
--   2) 어플리케이션 배포
--
-- 멱등성: 동일 컬럼 중복 추가 시 에러 발생하므로 1회만 실행.
-- =========================================================

ALTER TABLE notifications
    ADD COLUMN title VARCHAR(255) NULL AFTER user_id;
