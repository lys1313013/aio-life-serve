package top.aiolife.record.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典类型枚举
 *
 * @author Lys
 */
@Getter
@AllArgsConstructor
public enum DictTypeEnum {

    EXERCISE_TYPE("exercise_type", "运动类型"),
    DEVICE_TYPE("device_type", "设备类型");

    private final String value;
    private final String label;

    public static List<Map<String, String>> toList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (DictTypeEnum typeEnum : DictTypeEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("value", typeEnum.getValue());
            map.put("label", typeEnum.getLabel());
            list.add(map);
        }
        return list;
    }
}
