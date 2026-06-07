-- 1. 插入字典类型 (如果不存在)
INSERT IGNORE INTO `user_dict_type` (`user_id`, `dict_name`, `dict_type`, `status`, `create_user`, `update_user`)
VALUES (0, '支付方式', 'pay_type', '0', 0, 0);

-- 2. 插入字典数据
INSERT IGNORE INTO `user_dict_data` (`user_id`, `dict_type`, `dict_sort`, `dict_label`, `dict_value`, `status`, `create_user`, `update_user`)
VALUES 
(0, 'pay_type', 1, '支付宝', '1', '0', 0, 0),
(0, 'pay_type', 2, '微信', '2', '0', 0, 0),
(0, 'pay_type', 3, '现金', '3', '0', 0, 0),
(0, 'pay_type', 4, '银行卡', '4', '0', 0, 0),
(0, 'pay_type', 5, '广发信用卡', '5', '0', 0, 0),
(0, 'pay_type', 6, '招商信号卡', '6', '0', 0, 0),
(0, 'pay_type', 7, '京东', '7', '0', 0, 0);

-- 3. 迁移 expense 表的数据
-- 我们将 pay_type_id 更新为 user_dict_data 表中对应的 id
UPDATE expense e
JOIN user_dict_data udd ON e.pay_type_id = udd.dict_value AND udd.dict_type = 'pay_type' AND udd.user_id = 0
SET e.pay_type_id = udd.id
WHERE e.pay_type_id IN (1, 2, 3, 4, 5, 6, 7);

-- 4. 修改 expense 表的 pay_type_id 类型为 varchar(32) 或者 bigint
ALTER TABLE `expense` MODIFY COLUMN `pay_type_id` VARCHAR(32) COMMENT '支付方式ID(关联user_dict_data)';
