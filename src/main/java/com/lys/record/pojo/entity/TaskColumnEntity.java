package com.lys.record.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务列实体类
 *
 * @author Lys
 * @date 2025/04/10 22:44
 */
@Data
public class TaskColumnEntity {

    /**
     * 列ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 列标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
