
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
