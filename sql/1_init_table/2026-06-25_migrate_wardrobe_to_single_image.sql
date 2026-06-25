-- 衣柜衣物表：将 photo_urls 改为 file_id（单张图片）
ALTER TABLE `wardrobe_item` DROP COLUMN IF EXISTS `photo_urls`;
ALTER TABLE `wardrobe_item` ADD COLUMN `file_id` VARCHAR(50) COMMENT '图片文件ID' AFTER `price`;
