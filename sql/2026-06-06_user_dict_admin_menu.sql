INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `redirect`, `meta`, `roles`, `sort`, `status`, `is_deleted`)
VALUES
(1903, 1900, 'UserDictAdmin', '/system/user-dict', 'system/user-dict/index', NULL,
 JSON_OBJECT('icon', 'mdi:book-open-page-variant', 'title', '用户字典管理'),
 'admin', 3, 1, 0);