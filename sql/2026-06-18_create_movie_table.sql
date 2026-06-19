CREATE TABLE `movie` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `title` varchar(255) NOT NULL COMMENT '影视名称',
  `type` tinyint(4) NOT NULL COMMENT '类型：1-电影，2-剧集，3-动漫，4-纪录片，5-其他',
  `director` varchar(100) DEFAULT NULL COMMENT '导演/演员',
  `url` varchar(500) DEFAULT NULL COMMENT '链接（主要针对豆瓣等外部链接）',
  `cover_img` varchar(500) DEFAULT NULL COMMENT '封面图片链接',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0-想看，1-在看，2-看过，3-搁置',
  `total_progress` int(11) DEFAULT '0' COMMENT '总进度（总集数或时长）',
  `current_progress` int(11) DEFAULT '0' COMMENT '当前进度（当前集数或观看时长）',
  `start_time` datetime DEFAULT NULL COMMENT '开始观看时间',
  `finish_time` datetime DEFAULT NULL COMMENT '看完时间',
  `remark` varchar(1000) DEFAULT NULL COMMENT '短评/备注',
  `user_id` bigint(20) NOT NULL COMMENT '归属用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影视记录表';

INSERT IGNORE INTO `sys_menu` (`id`,`parent_id`,`name`,`path`,`component`,`redirect`,`meta`,`roles`,`sort`,`status`,`is_deleted`)
VALUES
(1508, 1500, 'Movie', '/my-hub/movie', 'my-hub/movie/index', NULL,
 JSON_OBJECT('icon','mdi:movie-open-play-outline','title','观影记录','backTop',false),
 NULL, 7, 1, 0);
