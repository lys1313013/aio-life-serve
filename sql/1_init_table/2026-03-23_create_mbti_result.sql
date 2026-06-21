-- MBTI测试结果表
-- 创建时间: 2026-03-23
-- 描述: 存储用户MBTI人格测试结果

CREATE TABLE IF NOT EXISTS `mbti_result` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `test_id` VARCHAR(255) DEFAULT NULL COMMENT '测试ID',
    `mbti_type` VARCHAR(10) DEFAULT NULL COMMENT 'MBTI类型',
    `raw_result` TEXT DEFAULT NULL COMMENT '原始结果数据(JSON)',
    `results_page` VARCHAR(500) DEFAULT NULL COMMENT '官方结果页面URL',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除(0-否,1-是)',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_test_id` (`test_id`),
    INDEX `idx_mbti_type` (`mbti_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MBTI测试结果表';
