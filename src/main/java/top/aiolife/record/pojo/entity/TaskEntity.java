package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实体类
 *
 * @author Lys
 * @date 2025/04/10 22:45
 */
@Data
@TableName("task")
public class TaskEntity {
    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 任务明细
     */
    private String detail;

    /**
     * 列ID
     */
    private Integer columnId;

    /**
     * 目标完成时间
     */
    private LocalDateTime dueDate;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer unCompletedCount;
}
