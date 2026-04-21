-- 纪念日表结构
CREATE TABLE `anniversary_record` (
  `id` BIGINT NOT NULL COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `target_date` DATE NOT NULL COMMENT '目标日期',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：anniversary-纪念日(正数), countdown-倒数日(倒数)',
  `note` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '渐变色class',
  `icon` VARCHAR(20) DEFAULT NULL COMMENT 'Emoji图标',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_date` (`target_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纪念日记录表';