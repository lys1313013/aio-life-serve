-- =====================================================
-- 用户反馈模块
-- =====================================================

-- 反馈主表
CREATE TABLE `feedback` (
  `id` BIGINT NOT NULL COMMENT '反馈ID（雪花）',
  `user_id` BIGINT NOT NULL COMMENT '反馈人ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容（Markdown）',
  `feedback_type` VARCHAR(32) NOT NULL COMMENT '类型：BUG/SUGGESTION/QUESTION/OTHER',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/RESOLVED/CLOSED/REJECTED',
  `priority` VARCHAR(16) DEFAULT 'MEDIUM' COMMENT '优先级：LOW/MEDIUM/HIGH（预留）',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_create_time` (`user_id`, `create_time`),
  KEY `idx_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户反馈表';

-- 反馈评论表
CREATE TABLE `feedback_comment` (
  `id` BIGINT NOT NULL COMMENT '评论ID（雪花）',
  `feedback_id` BIGINT NOT NULL COMMENT '所属反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '评论人ID',
  `role_type` VARCHAR(16) NOT NULL COMMENT '角色：USER/ADMIN',
  `content` TEXT NOT NULL COMMENT '评论内容（Markdown）',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_id_create_time` (`feedback_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈评论表';

-- =====================================================
-- 系统配置（通用 KV）
-- =====================================================

CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花）',
  `config_key` VARCHAR(128) NOT NULL COMMENT '配置键（唯一）',
  `config_value` TEXT COMMENT '配置值（JSON 或纯文本）',
  `config_type` VARCHAR(32) DEFAULT 'STRING' COMMENT '类型：STRING/JSON/BOOLEAN/NUMBER（前端渲染用）',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '配置说明',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 初始化：反馈通知接收人配置（默认空列表，需管理员在后台勾选）
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `config_type`, `description`, `is_deleted`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES (1, 'feedback.notify_admin_ids', '[]', 'JSON', '接收用户反馈通知的管理员账号 ID 列表', 0, NOW(), NOW(), 1, 1);
