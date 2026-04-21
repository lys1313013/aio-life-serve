-- 荣誉中心表结构
-- 支持用户自主记录个人荣誉（优秀员工、国家奖学金等）

-- 荣誉分类表
CREATE TABLE `honor_category` (
  `id` BIGINT NOT NULL COMMENT '分类ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID（NULL表示系统预设分类）',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '分类颜色',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='荣誉分类表';

-- 系统预设分类
INSERT INTO `honor_category` (`id`, `user_id`, `name`, `icon`, `color`, `sort_order`) VALUES
(1, NULL, '学业成就', '🎓', '#4CAF50', 1),
(2, NULL, '奖学金', '💰', '#FFC107', 2),
(3, NULL, '工作荣誉', '💼', '#2196F3', 3),
(4, NULL, '竞赛获奖', '🏆', '#FF5722', 4),
(5, NULL, '社会实践', '🤝', '#9C27B0', 5),
(6, NULL, '荣誉称号', '⭐', '#FFD700', 6),
(7, NULL, '其他荣誉', '📜', '#607D8B', 7);

-- 荣誉记录表
CREATE TABLE `honor_record` (
  `id` BIGINT NOT NULL COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '荣誉标题',
  `description` TEXT COMMENT '荣誉描述',
  `honor_date` DATE NOT NULL COMMENT '获得日期',
  `issuer` VARCHAR(200) DEFAULT NULL COMMENT '颁发机构/组织',
  `level` VARCHAR(20) DEFAULT NULL COMMENT '荣誉级别：1-校级，2-市级，3-省级，4-国家级，5-国际级',
  `category_id` BIGINT DEFAULT NULL COMMENT '所属分类ID（可为空）',
  `custom_category` VARCHAR(50) DEFAULT NULL COMMENT '自定义分类名称（当不选择预设分类时使用）',
  `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签（JSON格式存储）',
  `attachments` VARCHAR(2000) DEFAULT NULL COMMENT '附件URL列表（JSON格式）',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_public` TINYINT DEFAULT 1 COMMENT '是否公开：0-私密，1-公开',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_honor_date` (`honor_date`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='荣誉记录表';
