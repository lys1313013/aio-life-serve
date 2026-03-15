CREATE TABLE `llm_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `model_name` varchar(100) NOT NULL COMMENT '模型名称',
  `api_key` varchar(255) NOT NULL COMMENT 'API密钥（加密存储）',
  `base_url` varchar(255) NOT NULL COMMENT '基础URL',
  `is_default` int(1) DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大模型密钥表';
