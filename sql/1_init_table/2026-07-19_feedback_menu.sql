-- 用户反馈模块菜单
-- 创建时间: 2026-07-19
-- 作者: Ethan

-- 1. 反馈中心（用户侧，挂在「记录」分组 1500 下）
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `redirect`, `meta`, `roles`, `sort`, `status`, `is_deleted`)
VALUES
(1509, 1500, 'feedbackCenter', '/my-hub/feedback', 'my-hub/feedback/index', NULL,
 JSON_OBJECT('icon', 'mdi:message-alert-outline', 'title', '反馈中心', 'backTop', false),
 NULL, 10, 1, 0);

-- 2. 反馈管理（管理员侧，挂在「系统管理」分组 1900 下）
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `redirect`, `meta`, `roles`, `sort`, `status`, `is_deleted`)
VALUES
(1904, 1900, 'FeedbackAdmin', '/system/feedback', 'system/feedback/index', NULL,
 JSON_OBJECT('icon', 'mdi:message-alert-outline', 'title', '反馈管理'),
 'admin', 3, 1, 0);

-- 3. 系统配置（管理员侧，挂在「系统管理」分组 1900 下）
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `redirect`, `meta`, `roles`, `sort`, `status`, `is_deleted`)
VALUES
(1905, 1900, 'SystemConfig', '/system/config', 'system/config/index', NULL,
 JSON_OBJECT('icon', 'mdi:cog-outline', 'title', '系统配置'),
 'admin', 4, 1, 0);
