-- ─────────────────────────────────────────────────────────────────────
--  InclusiveConnect — MySQL Initialization Script
--
--  Executed once by the MySQL container on first startup.
--  The database itself is created by MYSQL_DATABASE env var;
--  this script sets up character sets, the app user, and seed roles.
-- ─────────────────────────────────────────────────────────────────────

-- Ensure the database uses proper character set
ALTER DATABASE `inclusive_connect`
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

USE `inclusive_connect`;

-- ── Seed Roles ────────────────────────────────────────────────────────
-- Spring Boot DataSeeder also inserts these, but running here ensures
-- they exist before the first app startup on a brand-new database.

CREATE TABLE IF NOT EXISTS `roles` (
  `id`        BIGINT       NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `roles` (`role_name`) VALUES ('ROLE_CANDIDATE');
INSERT IGNORE INTO `roles` (`role_name`) VALUES ('ROLE_EMPLOYER');
INSERT IGNORE INTO `roles` (`role_name`) VALUES ('ROLE_ADMIN');
