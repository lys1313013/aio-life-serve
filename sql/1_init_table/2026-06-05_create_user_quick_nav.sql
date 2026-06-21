-- 用户首页快捷导航布局表
-- 创建时间: 2026-06-05
-- 更新功能简介:
-- 1) 新增 user_quick_nav 表，存储用户首页快捷导航的自定义布局
-- 2) 用户未保存任何项时为空表，前端展示空态
-- 3) 一项一行；title/icon/color/url 渲染时 JOIN sys_menu 实时取，不在表中冗余

CREATE TABLE IF NOT EXISTS `user_quick_nav` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `menu_id` BIGINT NOT NULL COMMENT '关联 sys_menu.id',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号（越小越前）',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用（1启用 / 0隐藏）',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除(0-否,1-是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_menu` (`user_id`, `menu_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户首页快捷导航布局';
