-- 1. 插入字典数据
INSERT IGNORE INTO `user_dict_data` (`user_id`, `dict_type`, `dict_sort`, `dict_label`, `dict_value`, `status`, `create_user`, `update_user`)
VALUES 
(0, 'pay_type', 1, '支付宝', '1', '0', 0, 0),
(0, 'pay_type', 2, '微信', '2', '0', 0, 0),
(0, 'pay_type', 3, '现金', '3', '0', 0, 0),
(0, 'pay_type', 4, '银行卡', '4', '0', 0, 0),
(0, 'pay_type', 5, '广发信用卡', '5', '0', 0, 0),
(0, 'pay_type', 6, '招商信号卡', '6', '0', 0, 0),
(0, 'pay_type', 7, '京东', '7', '0', 0, 0);


-- 4. 修改 expense 表的 pay_type_id 类型为 bigint
ALTER TABLE `expense` MODIFY COLUMN `pay_type_id` bigint COMMENT '支付方式ID(关联user_dict_data)';
