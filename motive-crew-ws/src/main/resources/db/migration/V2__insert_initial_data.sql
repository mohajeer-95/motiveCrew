-- Initial Data for Motive Crew
-- Insert team members and initial data

-- ============================================
-- INSERT TEAM MEMBERS
-- ============================================

-- Admins
INSERT INTO users (name, email, phone, password_hash, role, position, is_active, joined_date, created_at, updated_at) VALUES
('Mohammed Hajeer', 'mohammed.hajeer@company.com', '0790000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'React Native Developer', TRUE, '2024-01-01', NOW(), NOW()),
('Ashraf Matar', 'ashraf.matar@company.com', '0790000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'Senior Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW());

-- Members
INSERT INTO users (name, email, phone, password_hash, role, position, is_active, joined_date, created_at, updated_at) VALUES
('Firas Kamal', 'firas.kamal@company.com', '0790000003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Software Development Manager', TRUE, '2024-01-01', NOW(), NOW()),
('Ayat Hamdan', 'ayat.hamdan@company.com', '0790000004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Technical Team Leader', TRUE, '2024-01-01', NOW(), NOW()),
('Ahmad Shlool', 'ahmad.shlool@company.com', '0790000005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Technical Team Leader', TRUE, '2024-01-01', NOW(), NOW()),
('Munis Alawneh', 'munis.alawneh@company.com', '0790000006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW()),
('Hamzeh Radaideh', 'hamzeh.radaideh@company.com', '0790000007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW()),
('Mohanned Sadiq', 'mohanned.sadiq@company.com', '0790000008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Senior Devops/ Support Engineer', TRUE, '2024-01-01', NOW(), NOW()),
('Hisham Almasri', 'hisham.almasri@company.com', '0790000009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Devops/Support Engineer', TRUE, '2024-01-01', NOW(), NOW()),
('Hisham Alzuraiqi', 'hisham.alzuraiqi@company.com', '0790000010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Senior Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW()),
('Dana Sawalha', 'dana.sawalha@company.com', '0790000011', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW()),
('Mesan Qawasmeh', 'mesan.qawasmeh@company.com', '0790000012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Angular Developer', TRUE, '2024-01-01', NOW(), NOW()),
('Ahmad Juhaini', 'ahmad.juhaini@company.com', '0790000013', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Senior Developer(JAVA)', TRUE, '2024-01-01', NOW(), NOW()),
('Khaled Taamneh', 'khaled.taamneh@company.com', '0790000014', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Front End Developer', TRUE, '2024-01-01', NOW(), NOW()),
('Reneh Madanat', 'reneh.madanat@company.com', '0790000015', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Junior JAVA Developer', TRUE, '2024-01-01', NOW(), NOW()),
('Remah Al-Ramahi', 'remah.alramahi@company.com', '0790000016', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Project Manager', TRUE, '2024-01-01', NOW(), NOW()),
('Alaa AlTuhl', 'alaa.altuhl@company.com', '0790000017', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Business Analyst', TRUE, '2024-01-01', NOW(), NOW()),
('Farah Almasri', 'farah.almasri@company.com', '0790000018', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Project Coordinator', TRUE, '2024-01-01', NOW(), NOW()),
('Nawal Zahran', 'nawal.zahran@company.com', '0790000019', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Quality Assurance Officer', TRUE, '2024-01-01', NOW(), NOW()),
('Ibrahim Mansour', 'ibrahim.mansour@company.com', '0790000020', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Senior Quality Assurance Officer', TRUE, '2024-01-01', NOW(), NOW()),
('Mohammad Majdoub', 'mohammad.majdoub@company.com', '0790000021', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MEMBER', 'Wireless Software - Business Unit Manager', TRUE, '2024-01-01', NOW(), NOW());

-- ============================================
-- CREATE USER PREFERENCES FOR ALL USERS
-- ============================================
INSERT INTO user_preferences (user_id, notifications_enabled, dark_mode, language, auto_login, default_month, created_at, updated_at)
SELECT id, TRUE, FALSE, 'en', TRUE, 'CURRENT', NOW(), NOW()
FROM users;

-- ============================================
-- CREATE CURRENT MONTH COLLECTION
-- ============================================
INSERT INTO monthly_collections (year, month, target_amount, is_locked, created_at, updated_at)
VALUES (YEAR(NOW()), MONTH(NOW()), 5.00, FALSE, NOW(), NOW());

-- Note: Password hash above is for "password123" (BCrypt)
-- You can change it or use the signup endpoint to create users with proper passwords

