package top.aiolife.record.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/19 21:22
 */
@Getter
@AllArgsConstructor
public enum StudyEnum {

    NOT_START(1, "未开始"),
    IN_PROGRESS(2, "进行中"),
    PAUSED(3, "已暂停"),
    PARTIAL_COMPLETION(4, "部分完成"),
    COMPLETED(5, "已完成");

    private int value;
    private String label;
}
