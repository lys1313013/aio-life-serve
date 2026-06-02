-- 关系图谱菜单
-- 创建时间: 2026-06-01

INSERT IGNORE INTO `sys_menu` (`id`,`parent_id`,`name`,`path`,`component`,`redirect`,`meta`,`roles`,`sort`,`status`,`is_deleted`)
VALUES
-- 关系图谱
(2300, 0, 'Relationship', '/relationship', 'BasicLayout', NULL,
 JSON_OBJECT('icon','ant-design:team-outlined','title','关系图谱','order',8,'keepAlive',true),
 NULL, 8, 1, 0),
(2301, 2300, 'RelationshipGraph', '/relationship/graph', 'relationship/index', NULL,
 JSON_OBJECT('icon','ant-design:team-outlined','title','关系图谱','backTop',false),
 NULL, 0, 1, 0);
