-- 密码库表
CREATE TABLE `password_vault` (
  `id` BIGINT NOT NULL COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题，如 GitHub',
  `website` VARCHAR(255) COMMENT '网站/应用名',
  `category` VARCHAR(50) DEFAULT '其他' COMMENT '分类：工作/生活/金融/社交/其他',
  `username` TEXT COMMENT '账号（SM4加密存储）',
  `password` TEXT COMMENT '密码（SM4加密存储）',
  `salt` VARCHAR(64) NOT NULL COMMENT 'PBKDF2盐值，每条记录唯一',
  `remark` TEXT COMMENT '备注（SM4加密存储）',
  `favorite` BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码库表';