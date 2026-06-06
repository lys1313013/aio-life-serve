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
    EXP_TYPE("exp_type", "支出类型"),
    GOAL_TYPE("goal_type", "目标类型"),
    TASK_STATUS("task_status", "任务状态"),
    STUDY_STATUS("study_status", "学习状态"),
    MBTI_TYPE("mbti_type", "MBTI类型"),
    CBTI_TYPE("cbti_type", "CBTI类型"),
    WARDROBE_SEASON("wardrobe_season", "衣柜季节"),
    WARDROBE_CATEGORY("wardrobe_category", "衣柜分类");

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
