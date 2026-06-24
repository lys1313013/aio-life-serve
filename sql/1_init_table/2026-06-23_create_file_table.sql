CREATE TABLE IF NOT EXISTS `sys_file` (
    `id` VARCHAR(32) NOT NULL COMMENT '主键UUID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件原名',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_type` VARCHAR(100) COMMENT '文件MIME类型',
    `hash_value` VARCHAR(128) COMMENT '文件哈希值(如 MD5，用于防重)',
    `biz_type` VARCHAR(50) NOT NULL COMMENT '业务类型(如 movie, device, honor, wardrobe_item)',
    `biz_id` BIGINT COMMENT '业务记录ID(用于反向关联，单附件时也可为空)',
    `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开：0-否，1-是',
    `create_user` BIGINT NOT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_user` BIGINT NOT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_biz` (`biz_type`, `biz_id`),
    KEY `idx_hash` (`hash_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统文件表';

ALTER TABLE `device` DROP COLUMN `image`, ADD COLUMN `file_id` VARCHAR(32) COMMENT '图片文件ID(UUID)';
ALTER TABLE `movie` DROP COLUMN `cover_img`, ADD COLUMN `file_id` VARCHAR(32) COMMENT '封面文件ID(UUID)';
ALTER TABLE `read_record` DROP COLUMN `cover_img`, ADD COLUMN `file_id` VARCHAR(32) COMMENT '封面文件ID(UUID)';
ALTER TABLE `wardrobe_item` DROP COLUMN `photo_urls`, ADD COLUMN `file_id` VARCHAR(32) COMMENT '图片文件ID(UUID)';
ALTER TABLE `honor_record` DROP COLUMN `attachments`, ADD COLUMN `file_id` VARCHAR(32) COMMENT '证书/奖牌图片文件ID(UUID)';
