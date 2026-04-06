-- 添加关注字段到 task_detail 表
ALTER TABLE task_detail
ADD COLUMN is_starred INT(1) NOT NULL DEFAULT 0 COMMENT '是否关注: 0-未关注, 1-已关注';

-- 添加开始时间和结束时间字段
ALTER TABLE task_detail
ADD COLUMN start_time datetime DEFAULT NULL COMMENT '开始时间';

ALTER TABLE task_detail
ADD COLUMN end_time datetime DEFAULT NULL COMMENT '结束时间';
