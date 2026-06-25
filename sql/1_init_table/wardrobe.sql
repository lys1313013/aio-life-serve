
-- 衣柜分类表
CREATE TABLE IF NOT EXISTS `wardrobe_category` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(50) COMMENT '图标',
    `parent_id` BIGINT COMMENT '父分类ID',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `category_type` TINYINT DEFAULT 0 COMMENT '0=系统预设 1=用户自定义',
    `user_id` BIGINT COMMENT '用户ID(用户自定义分类时)',
    `create_user` BIGINT COMMENT '创建人',
    `create_time` DATETIME COMMENT '创建时间',
    `update_user` BIGINT COMMENT '更新人',
    `update_time` DATETIME COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='衣柜分类表';

-- 衣柜衣物表
CREATE TABLE IF NOT EXISTS `wardrobe_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '衣物名称',
    `category_id` BIGINT COMMENT '分类ID',
    `color` VARCHAR(50) COMMENT '颜色',
    `brand` VARCHAR(100) COMMENT '品牌',
    `season` VARCHAR(20) COMMENT '适用季节:春,夏,秋,冬',
    `purchase_date` DATE COMMENT '购买日期',
    `price` DECIMAL(10,2) COMMENT '价格',
    `file_id` VARCHAR(50) COMMENT '图片文件ID',
    `size` VARCHAR(20) COMMENT '尺码',
    `memo` VARCHAR(500) COMMENT '备注',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_user` BIGINT COMMENT '创建人',
    `create_time` DATETIME COMMENT '创建时间',
    `update_user` BIGINT COMMENT '更新人',
    `update_time` DATETIME COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='衣柜衣物表';

-- 插入系统预设分类
INSERT INTO `wardrobe_category` (`id`, `name`, `icon`, `parent_id`, `sort`, `category_type`) VALUES
(1, '上衣', 'shirt', NULL, 1, 0),
(2, '下装', 'pants', NULL, 2, 0),
(3, '鞋子', 'shoe', NULL, 3, 0),
(4, '配饰', 'accessory', NULL, 4, 0),
(5, '外套', 'jacket', NULL, 5, 0);

-- 插入二级分类
INSERT INTO `wardrobe_category` (`id`, `name`, `icon`, `parent_id`, `sort`, `category_type`) VALUES
(101, 'T恤', 'shirt', 1, 1, 0),
(102, '衬衫', 'shirt', 1, 2, 0),
(103, '卫衣', 'shirt', 1, 3, 0),
(201, '牛仔裤', 'pants', 2, 1, 0),
(202, '休闲裤', 'pants', 2, 2, 0),
(203, '裙子', 'skirt', 2, 3, 0),
(301, '运动鞋', 'shoe', 3, 1, 0),
(302, '皮鞋', 'shoe', 3, 2, 0),
(303, '拖鞋', 'shoe', 3, 3, 0),
(401, '帽子', 'hat', 4, 1, 0),
(402, '围巾', 'scarf', 4, 2, 0),
(403, '手表', 'watch', 4, 3, 0),
(501, '夹克', 'jacket', 5, 1, 0),
(502, '大衣', 'coat', 5, 2, 0),
(503, '羽绒服', 'down', 5, 3, 0);

-- 添加衣柜菜单（在物品中心分组下）
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `redirect`, `meta`, `roles`, `sort`, `status`, `is_deleted`)
VALUES
(1702, 1700, 'Wardrobe', '/wardrobe', 'wardrobe/index', NULL,
 JSON_OBJECT('icon','ant-design:book-outlined','title','衣柜'),
 NULL, 1, 1, 0);
