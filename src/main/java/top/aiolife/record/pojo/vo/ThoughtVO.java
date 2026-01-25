package top.aiolife.record.pojo.vo;

import top.aiolife.record.pojo.entity.RelaEventEntity;
import lombok.Data;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-16 18:31
 */
@Data
public class ThoughtVO {
    private Long id;
    private Long userId;
    private String content;
    private String createTime;

    private List<RelaEventEntity> events;
}
