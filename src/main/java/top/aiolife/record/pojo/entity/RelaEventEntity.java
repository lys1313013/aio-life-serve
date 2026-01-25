package top.aiolife.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-16 15:34
 */
@Data
@TableName("rela_event")
public class RelaEventEntity extends BaseEntity{

    private Long thoughtId;

    private String content;
}
