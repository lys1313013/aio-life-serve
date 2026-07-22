CREATE TABLE IF NOT EXISTS `notification_channel_config` (
  `id` BIGINT NOT NULL COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'AIO Life 用户 ID',
  `channel` VARCHAR(32) NOT NULL COMMENT '通知渠道：FEISHU',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用',
  `app_id` VARCHAR(128) NOT NULL COMMENT '用户自己的飞书应用 App ID',
  `app_secret_ciphertext` TEXT NOT NULL COMMENT '加密的飞书应用 App Secret',
  `receiver_open_id` VARCHAR(128) DEFAULT NULL COMMENT '接收用户在该应用下的 open_id',
  `receiver_name` VARCHAR(128) DEFAULT NULL COMMENT '接收用户名称',
  `create_user` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_user` BIGINT NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_channel` (`user_id`, `channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知渠道配置';

CREATE TABLE IF NOT EXISTS `notification_preference` (
  `id` BIGINT NOT NULL COMMENT 'ID',
  `user_id` BIGINT NOT NULL,
  `biz_type` VARCHAR(64) NOT NULL,
  `channel` VARCHAR(32) NOT NULL COMMENT '通知渠道：FEISHU',
  `enabled` TINYINT NOT NULL DEFAULT 1,
  `create_user` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_user` BIGINT NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_biz_channel` (`user_id`, `biz_type`, `channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知偏好';

CREATE TABLE IF NOT EXISTS `notification_delivery` (
  `id` BIGINT NOT NULL COMMENT 'ID',
  `dedup_key` VARCHAR(128) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `biz_type` VARCHAR(64) NOT NULL,
  `channel` VARCHAR(32) NOT NULL,
  `status` VARCHAR(16) NOT NULL COMMENT 'PENDING/SUCCESS/RETRY/FAILED',
  `payload_ciphertext` MEDIUMTEXT NOT NULL COMMENT '重试所需的加密消息载荷',
  `retry_count` INT NOT NULL DEFAULT 0,
  `next_retry_time` DATETIME DEFAULT NULL,
  `provider_code` VARCHAR(64) DEFAULT NULL,
  `error_message` VARCHAR(512) DEFAULT NULL,
  `create_user` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_user` BIGINT NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dedup_user_channel` (`dedup_key`, `user_id`, `channel`),
  KEY `idx_retry` (`status`, `next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知投递记录';
