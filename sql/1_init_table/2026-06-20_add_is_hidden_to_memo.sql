ALTER TABLE `memo` ADD COLUMN `hidden_content` tinyint(1) DEFAULT 0 COMMENT '是否隐藏内容' AFTER `content`;
ALTER TABLE `thought` ADD COLUMN `hidden_content` tinyint(1) DEFAULT 0 COMMENT '是否隐藏内容' AFTER `is_pinned`;
