CREATE DATABASE IF NOT EXISTS `biz_center`;


CREATE TABLE IF NOT EXISTS biz_center.saas_accounts(
    `user_name`       VARCHAR(100) NOT NULL,
    `user_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `phone`       VARCHAR(20) NOT NULL,
    `password`       VARCHAR(128) NOT NULL ,
    `status`        TINYINT DEFAULT 0,
    `register_time`  DATETIME NOT NULL,
    `latest_login_time`  DATETIME,
    UNIQUE(user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS biz_center.aws_accounts (
    `id`                BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id`           VARCHAR(20) NOT NULL,
    `user_id`           BIGINT NOT NULL,
    `iam_user`       VARCHAR(100) NOT NULL,
    `access_key_id`       VARCHAR(128) NOT NULL ,
    `secret_accessKey`       VARCHAR(128) NOT NULL ,

    `created_time`  DATETIME NOT NULL,
    `updated_time`  DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


    private String userId;

    // aws
    private  String accountId;
    private String iamUser;
    private String accessKeyId;
    private String secretAccessKey;