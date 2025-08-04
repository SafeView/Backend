-- 관리자 사용자 생성 (비밀번호: admin123)
INSERT INTO users (email, password, name, role, address, phone, gender, birthday, created_at, updated_at)
VALUES (
    'admin@safeview.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123 (BCrypt)
    '관리자',
    'ROLE_ADMIN',
    '서울시 강남구 테헤란로 123',
    '010-1234-5678',
    'MALE',
    '1990-01-01',
    NOW(),
    NOW()
);

-- 테스트용 일반 사용자 생성 (비밀번호: user123)
INSERT INTO users (email, password, name, role, address, phone, gender, birthday, created_at, updated_at)
VALUES (
    'user@safeview.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- user123 (BCrypt)
    '일반사용자',
    'ROLE_USER',
    '서울시 서초구 서초대로 456',
    '010-9876-5432',
    'FEMALE',
    '1995-05-15',
    NOW(),
    NOW()
); 

-- 기존 user_roles 테이블의 데이터를 users 테이블의 role 컬럼으로 마이그레이션
-- 이 스크립트는 UserRole 엔티티를 제거한 후 실행되어야 합니다.

-- users 테이블에 role 컬럼이 없다면 추가 (기본값: USER)
-- ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- 기존 user_roles 테이블의 데이터를 users 테이블로 마이그레이션
-- UPDATE users u 
-- SET role = (SELECT ur.role FROM user_roles ur WHERE ur.user_id = u.user_id)
-- WHERE EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.user_id);

-- 마이그레이션 완료 후 user_roles 테이블 삭제
-- DROP TABLE IF EXISTS user_roles; 