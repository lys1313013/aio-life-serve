-- 目标管理表
-- 支持年度目标、月度目标、日目标的多层级目标管理

CREATE TABLE `goal` (
  `id` BIGINT NOT NULL COMMENT '目标ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `type` TINYINT DEFAULT NULL COMMENT '目标类型：1=年度目标，2=月度目标，3=日目标',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '目标标题',
  `description` TEXT COMMENT '目标描述',
  `content` TEXT COMMENT '目标详细内容/行动计划',
  `status` TINYINT DEFAULT NULL COMMENT '目标状态：0=待开始，1=进行中，2=已完成，3=已放弃',
  `progress` INT DEFAULT 0 COMMENT '目标进度（0-100）',
  `target_value` INT DEFAULT NULL COMMENT '目标值',
  `current_value` INT DEFAULT 0 COMMENT '当前值',
  `year` INT DEFAULT NULL COMMENT '年份（用于年度目标筛选）',
  `month` INT DEFAULT NULL COMMENT '月份（用于月度目标筛选）',
  `day` INT DEFAULT NULL COMMENT '日期（用于日目标筛选）',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父目标ID',
  `start_date` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_date` DATETIME DEFAULT NULL COMMENT '结束时间',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `tags` VARCHAR(1000) DEFAULT NULL COMMENT '目标标签（JSON格式存储）',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0=未删除，1=已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='目标管理表';
