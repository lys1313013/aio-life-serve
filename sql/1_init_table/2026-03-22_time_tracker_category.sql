
-- 新增 is_enabled 字段，用于控制分类是否启用
ALTER TABLE time_tracker_category ADD COLUMN is_enabled tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用';

alter table time_tracker_category modify name varchar(50) null comment '分类名称';

ALTER TABLE time_tracker_category modify COLUMN color varchar(20) null comment '颜色值(Hex)';

-- 新增 template_id 字段
ALTER TABLE time_tracker_category ADD COLUMN template_id BIGINT NULL COMMENT '模板ID，指向被覆盖的公共分类ID';

update time_record t
set category_id  = (select distinct id from time_tracker_category c where c.code = t.category_id)
where exists(
    select id from time_tracker_category c where c.code = t.category_id
);
alter table time_tracker_category modify code varchar(50) null comment '分类标识(如: rest, work)';
-- 迁移完成后，也可以直接删除字段
-- ALTER TABLE time_tracker_category DROP COLUMN code;

-- 新增 icon 字段，用于存储分类图标
ALTER TABLE time_tracker_category ADD COLUMN icon VARCHAR(100) NULL COMMENT '图标名称(Iconify格式)';


-- device 表结构变更：添加规格字段、结束日期字段，移除订单号字段

-- 添加设备规格字段（位于 name 字段后）
ALTER TABLE `device` ADD COLUMN `spec` varchar(255) DEFAULT NULL COMMENT '设备规格' AFTER `name`;

-- 添加结束日期字段（位于 image 字段后）
ALTER TABLE `device` ADD COLUMN `end_date` varchar(255) DEFAULT NULL COMMENT '结束日期（用于计算日均费用）' AFTER `image`;

-- 移除订单号字段
ALTER TABLE `device` DROP COLUMN `order_number`;
