package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-07 23:19
 */
@Getter
@Setter
@TableName("task_detail")
public class TaskDetailEntity extends BaseEntity {

    private Long taskId;

    private Long userId;

    private String content;

    private Integer isCompleted;
}
