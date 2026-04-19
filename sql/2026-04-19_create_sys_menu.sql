-- 系统菜单表（后端菜单模式）
-- 创建时间: 2026-04-19
-- 作者: Ethan
-- 更新功能简介:
-- 1) 新增 sys_menu 表，支持树形菜单、角色可见性、启用状态、排序与逻辑删除
-- 2) 提供默认菜单种子数据，覆盖首页/业务模块/系统管理等基础导航
-- 3) 配合前端 backend 菜单模式，实现“后台配置菜单，前端动态渲染”

CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID（0为根）',
    `name` VARCHAR(64) NOT NULL COMMENT '路由名称',
    `path` VARCHAR(255) NOT NULL COMMENT '路由路径（唯一）',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件标识（BasicLayout/IFrameView 或 views 相对路径，如 system/user/index）',
    `redirect` VARCHAR(255) DEFAULT NULL COMMENT '重定向',
    `meta` JSON DEFAULT NULL COMMENT '路由 meta（title/icon/order/keepAlive/hideInMenu/link等）',
    `roles` VARCHAR(255) DEFAULT NULL COMMENT '可访问角色（逗号分隔，空表示所有）',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1启用，0禁用）',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除(0-否,1-是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_menu_path` (`path`),
    INDEX `idx_sys_menu_parent_id` (`parent_id`),
    INDEX `idx_sys_menu_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- 初始化种子（覆盖式插入由应用层处理；这里仅提供默认菜单）
-- 说明：component 为 views 相对路径（不带 .vue），前端会映射到 /views/**.vue

INSERT IGNORE INTO `sys_menu` (`id`,`parent_id`,`name`,`path`,`component`,`redirect`,`meta`,`roles`,`sort`,`status`,`is_deleted`)
VALUES
-- 主页（置顶）
(1001, 0, 'Analytics', '/analytics', 'dashboard/home/index', NULL,
 JSON_OBJECT('order', -1, 'affixTab', true, 'icon', 'lucide:home', 'title', '主页', 'keepAlive', true, 'maxIdleTime', 60),
 NULL, -1, 1, 0),

-- 仪表盘分组
(1100, 0, 'Dashboard', '/dashboard', 'BasicLayout', NULL,
 JSON_OBJECT('icon','lucide:layout-dashboard','order',0,'title','仪表盘'),
 NULL, 0, 1, 0),
(1101, 1100, 'Workspace', '/workspace', 'dashboard/workspace/index', NULL,
 JSON_OBJECT('icon','carbon:workspace','title','工作台'),
 NULL, 0, 1, 0),

-- 任务中心
(1200, 0, 'TaskCenter', '/task-center', 'BasicLayout', NULL,
 JSON_OBJECT('icon','mdi:clipboard-text-clock-outline','title','任务中心','order',1),
 NULL, 1, 1, 0),
(1201, 1200, 'TaskCenterTodo', '/task-center/todo', 'task-center/todo/index', NULL,
 JSON_OBJECT('icon','mdi:format-list-checks','title','待办'),
 NULL, 0, 1, 0),

-- 时间
(1300, 0, 'TimeManagement', '/time-management', 'BasicLayout', NULL,
 JSON_OBJECT('icon','mdi:clock-outline','title','时间','order',2,'keepAlive',true),
 NULL, 2, 1, 0),
(1301, 1300, 'TimeTracker', '/time-management/time-tracker', 'time-management/time-tracker/index', NULL,
 JSON_OBJECT('icon','mdi:history','title','时迹','backTop',false,'keepAlive',true,'maxIdleTime',60),
 NULL, 0, 1, 0),
(1302, 1300, 'TimeTrackerDashboard', '/time-management/dashboard', 'time-management/dashboard/index', NULL,
 JSON_OBJECT('icon','mdi:view-dashboard-outline','title','看板','backTop',false,'keepAlive',true),
 NULL, 1, 1, 0),
(1303, 1300, 'CategoryConfig', '/time-management/my-categories', 'time-management/time-tracker/category-config/index', NULL,
 JSON_OBJECT('icon','mdi:tag-multiple-outline','title','我的分类','backTop',false),
 NULL, 2, 1, 0),
(1304, 1300, 'TimeTrackerCategoryAdmin', '/time-management/category-admin', 'time-management/time-tracker/admin/index', NULL,
 JSON_OBJECT('icon','mdi:shield-account-outline','title','分类管理（管理员）','backTop',false),
 'admin', 3, 1, 0),

-- 编程看板
(1400, 0, 'Coding', '/coding', 'BasicLayout', NULL,
 JSON_OBJECT('icon','lucide:code-2','title','编程看板','order',3,'keepAlive',true),
 NULL, 3, 1, 0),
(1401, 1400, 'GithubGraph', '/coding/github', 'coding/github/index', NULL,
 JSON_OBJECT('icon','mdi:github','title','Github','backTop',false),
 NULL, 0, 1, 0),
(1402, 1400, 'LeetCode', '/coding/leetcode', 'coding/leetcode/index', NULL,
 JSON_OBJECT('icon','simple-icons:leetcode','title','LeetCode'),
 NULL, 1, 1, 0),

-- 记录
(1500, 0, 'Demos', '/my-hub', 'BasicLayout', NULL,
 JSON_OBJECT('icon','ic:baseline-view-in-ar','title','记录','order',3,'keepAlive',true),
 NULL, 4, 1, 0),
(1501, 1500, 'exercise', '/my-hub/exercise', 'my-hub/exercise/index', NULL,
 JSON_OBJECT('icon','mdi:run-fast','title','运动','backTop',false),
 NULL, 0, 1, 0),
(1502, 1500, 'videoWatch', '/my-hub/videoWatch', 'my-hub/videoWatch/index', NULL,
 JSON_OBJECT('icon','mdi:video-vintage','title','视频观看','backTop',false),
 NULL, 1, 1, 0),
(1503, 1500, 'think', '/my-hub/think', 'my-hub/think/index', NULL,
 JSON_OBJECT('icon','mdi:lightbulb-on-outline','title','闪念','backTop',false),
 NULL, 2, 1, 0),
(1504, 1500, 'memo', '/my-hub/memo', 'my-hub/memo/index', NULL,
 JSON_OBJECT('icon','mdi:note-text-outline','title','笔记','backTop',false),
 NULL, 3, 1, 0),
(1505, 1500, 'performance', '/my-hub/performance', 'my-hub/performance/index', NULL,
 JSON_OBJECT('icon','mdi:chart-line-variant','title','活动','backTop',false),
 NULL, 4, 1, 0),
(1506, 1500, 'milestone', '/my-hub/milestone', 'my-hub/milestone/index', NULL,
 JSON_OBJECT('icon','mdi:flag-variant','title','里程碑','backTop',false),
 NULL, 5, 1, 0),
(1507, 1500, 'anniversary', '/my-hub/anniversary', 'my-hub/anniversary/index', NULL,
 JSON_OBJECT('icon','mdi:calendar-heart','title','纪念日','backTop',false),
 NULL, 6, 1, 0),

-- 财务中心
(1600, 0, 'FinanceManagement', '/finance-management', 'BasicLayout', NULL,
 JSON_OBJECT('icon','mdi:finance','title','财务中心','order',4,'keepAlive',true),
 NULL, 5, 1, 0),
(1601, 1600, 'financeDashboard', '/finance-management/dashboard', 'my-hub/finance-dashboard/index', NULL,
 JSON_OBJECT('icon','mdi:chart-areaspline','title','概览','backTop',false),
 NULL, 0, 1, 0),
(1602, 1600, 'incomeManagement', '/finance-management/income', 'my-hub/income/index', NULL,
 JSON_OBJECT('icon','mdi:cash-plus','title','收入','backTop',false),
 NULL, 1, 1, 0),
(1603, 1600, 'expenseManagement', '/finance-management/expense', 'my-hub/expense/index', NULL,
 JSON_OBJECT('icon','mdi:cash-minus','title','支出','backTop',false),
 NULL, 2, 1, 0),
(1604, 1600, 'alipayImport', '/finance-management/import', 'my-hub/expense/import', NULL,
 JSON_OBJECT('icon','mdi:import','title','账单导入','backTop',false),
 NULL, 3, 1, 0),

-- 物品中心
(1700, 0, 'Goods', '/goods', 'BasicLayout', NULL,
 JSON_OBJECT('icon','lucide:package','title','物品中心','order',5,'keepAlive',true),
 NULL, 6, 1, 0),
(1701, 1700, 'device', '/my-hub/device', 'my-hub/device/index', NULL,
 JSON_OBJECT('icon','mdi:monitor-dashboard','title','设备墙','backTop',false),
 NULL, 0, 1, 0),

-- 配置管理（admin）
(1800, 0, 'ConfigManagement', '/config-management', 'BasicLayout', NULL,
 JSON_OBJECT('icon','mdi:cog-outline','title','配置管理','order',9,'keepAlive',true),
 'admin', 9, 1, 0),
(1801, 1800, 'sysDictType', '/config-management/sysDictType', 'config-management/sysDictType/index', NULL,
 JSON_OBJECT('icon','mdi:book-settings-outline','title','字典类型','backTop',false),
 'admin', 0, 1, 0),
(1802, 1800, 'sysDictData', '/config-management/sysDictData', 'config-management/sysDictData/index', NULL,
 JSON_OBJECT('icon','mdi:database-search-outline','title','字典数据','backTop',false),
 'admin', 1, 1, 0),

-- 系统管理（admin）
(1900, 0, 'System', '/system', 'BasicLayout', NULL,
 JSON_OBJECT('icon','mdi:shield-account-outline','title','系统管理','order',10,'keepAlive',true),
 'admin', 10, 1, 0),
(1901, 1900, 'UserCenter', '/system/user', 'system/user/index', NULL,
 JSON_OBJECT('icon','mdi:account-group-outline','title','用户中心'),
 'admin', 0, 1, 0),
(1902, 1900, 'MenuManagement', '/system/menu', 'system/menu/index', NULL,
 JSON_OBJECT('icon','mdi:menu-open','title','权限菜单'),
 'admin', 1, 1, 0),

-- 消息中心
(2000, 0, 'Message', '/message', 'message/index', NULL,
 JSON_OBJECT('icon','ant-design:message-outlined','title','消息中心','order',10,'hideInMenu',false,'fullPathKey',false),
 NULL, 10, 1, 0),

-- 关于（admin）与个人中心（隐藏）
(2100, 0, 'VbenAbout', '/vben-admin/about', '_core/about/index', NULL,
 JSON_OBJECT('icon','lucide:copyright','title','关于','order',9999),
 'admin', 9999, 1, 0),
(2200, 0, 'Profile', '/profile', '_core/profile/index', NULL,
 JSON_OBJECT('icon','lucide:user','title','个人中心','hideInMenu',true),
 NULL, 9998, 1, 0);
