-- 新增 time_type 字段，用于标记分类的时间类型
ALTER TABLE time_tracker_category
ADD COLUMN time_type TINYINT NULL DEFAULT 1
COMMENT '时间类型: 1-必须, 2-积极, 3-休闲';
