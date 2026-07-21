-- 衣柜衣物表：统一单张图片字段长度
ALTER TABLE `wardrobe_item` MODIFY COLUMN `file_id` VARCHAR(50) COMMENT '图片文件ID' AFTER `price`;
