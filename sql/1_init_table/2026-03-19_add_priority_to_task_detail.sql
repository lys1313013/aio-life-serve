ALTER TABLE `task_detail` ADD COLUMN `priority` int(11) DEFAULT '20' COMMENT '优先级: 1-高, 10-中, 20-低' AFTER `sort`;
