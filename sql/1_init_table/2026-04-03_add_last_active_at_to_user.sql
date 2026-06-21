ALTER TABLE `user`
    ADD COLUMN `last_active_at` timestamp NULL DEFAULT NULL COMMENT '最后活跃时间' AFTER `updated_at`;

