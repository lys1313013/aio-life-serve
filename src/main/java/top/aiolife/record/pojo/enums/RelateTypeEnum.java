package top.aiolife.record.pojo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时迹关联业务类型枚举
 *
 * @author Lys
 * @date 2026/06/19
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RelateTypeEnum {

    /**
     * 阅读
     */
    READ(1, "阅读"),

    /**
     * 观影
     */
    MOVIE(2, "观影");

    private final Integer value;

    private final String label;

    RelateTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 获取所有枚举值的列表，供前端使用
     *
     * @return 包含 value 和 label 的 Map 列表
     */
    public static List<Map<String, Object>> toList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RelateTypeEnum typeEnum : RelateTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("value", typeEnum.getValue());
            map.put("label", typeEnum.getLabel());
            list.add(map);
        }
        return list;
    }
}
