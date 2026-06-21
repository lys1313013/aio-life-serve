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


CREATE TABLE IF NOT EXISTS `chat_message` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role` varchar(50) NOT NULL COMMENT '角色: user-用户, assistant-助手',
    `content` text COMMENT '消息内容',
    `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
    `create_user` bigint(20) NOT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint(20) NOT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话消息表';


CREATE TABLE IF NOT EXISTS `chat_session` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
    `create_user` bigint(20) NOT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint(20) NOT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话会话表';

ALTER TABLE `chat_message` ADD COLUMN `conversation_id` bigint(20) DEFAULT NULL COMMENT '会话ID' AFTER `user_id`;
ALTER TABLE `chat_message` ADD INDEX `idx_conversation_id` (`conversation_id`);
