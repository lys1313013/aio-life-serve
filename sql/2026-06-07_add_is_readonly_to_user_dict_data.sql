-- 修改 user_dict_data 表，增加 is_readonly 字段
ALTER TABLE `user_dict_data` ADD COLUMN `is_readonly` char(1) DEFAULT 'N' COMMENT '是否只读（Y是 N否），当为Y时用户无法修改除状态外的其他属性' AFTER `status`;
