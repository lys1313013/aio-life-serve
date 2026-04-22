-- CBTI 人格测试相关表
-- 创建时间: 2026-04-18
-- 作者: Ethan
-- 描述: 存储 CBTI 人格类型数据与用户测试历史
-- 更新功能简介:
-- 1) 新增 cbti_personality 表，用于维护人格基础信息（向量、描述、图片对象路径、是否隐藏等）
-- 2) 新增 cbti_result 表，用于记录用户测试结果、维度得分和答案快照
-- 3) 为查询场景补充必要索引（user_id/personality_code/create_time）

CREATE TABLE IF NOT EXISTS `cbti_personality` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `code` VARCHAR(20) NOT NULL COMMENT '人格代码（唯一）',
    `name` VARCHAR(64) NOT NULL COMMENT '人格名称',
    `motto` VARCHAR(255) DEFAULT NULL COMMENT '座右铭',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '主题色（HEX）',
    `vector` JSON DEFAULT NULL COMMENT '人格向量（长度15，数值为-1/0/1/2）',
    `description` TEXT DEFAULT NULL COMMENT '人格描述',
    `strengths` JSON DEFAULT NULL COMMENT '优势（字符串数组）',
    `weaknesses` JSON DEFAULT NULL COMMENT '弱点/注意（字符串数组）',
    `tech_stack` VARCHAR(255) DEFAULT NULL COMMENT '技术栈',
    `spirit` TEXT DEFAULT NULL COMMENT '灵魂格言',
    `image_object` VARCHAR(255) DEFAULT NULL COMMENT 'MinIO对象路径（如 images/cbti/characters/SUDO.png）',
    `is_special` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏人格（0-否，1-是）',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除(0-否,1-是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cbti_personality_code` (`code`),
    INDEX `idx_cbti_personality_special` (`is_special`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CBTI 人格类型表';

CREATE TABLE IF NOT EXISTS `cbti_result` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `personality_code` VARCHAR(20) NOT NULL COMMENT '人格代码',
    `similarity` INT NOT NULL DEFAULT 0 COMMENT '匹配度（0-100）',
    `dimensions` JSON DEFAULT NULL COMMENT '15维度结果(JSON)',
    `answers` JSON DEFAULT NULL COMMENT '答题结果(JSON，题号->选项值)',
    `hidden_answers` JSON DEFAULT NULL COMMENT '彩蛋答题结果(JSON)',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除(0-否,1-是)',
    PRIMARY KEY (`id`),
    INDEX `idx_cbti_result_user_id` (`user_id`),
    INDEX `idx_cbti_result_personality_code` (`personality_code`),
    INDEX `idx_cbti_result_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CBTI 测试历史表';
