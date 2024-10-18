CREATE DATABASE IF NOT EXISTS `biz_center`;


CREATE TABLE IF NOT EXISTS biz_center.accounts(
    `user_id`       VARCHAR(100) NOT NULL,
    `account_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `balance`       DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `currency`      CHAR(100),
    `status`        VARCHAR(100)  DEFAULT 'active' COMMENT "活跃中=active",
    `created_time`  DATETIME NOT NULL,
    `updated_time`  DATETIME,
    UNIQUE(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS biz_center.payments (
    `id`                BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`           VARCHAR(100) NOT NULL,
    `from_account_id`   BIGINT NOT NULL,
    `to_account_id`     BIGINT NOT NULL,
    `amount`            DECIMAL(10, 2) NOT NULL,
    `currency`          CHAR(100),
    `description`       VARCHAR(100),
    `created_time`  DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;