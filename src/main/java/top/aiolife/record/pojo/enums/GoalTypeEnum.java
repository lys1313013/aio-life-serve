package top.aiolife.record.pojo.enums;

/**
 * 目标类型枚举
 *
 * @author Lys
 * @date 2026/03/30
 */
public enum GoalTypeEnum {

    /**
     * 年度目标
     */
    YEAR(1, "年度目标"),

    /**
     * 月度目标
     */
    MONTH(2, "月度目标"),

    /**
     * 日目标
     */
    DAY(3, "日目标");

    private final Integer code;

    private final String desc;

    GoalTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取枚举编码
     *
     * @return 枚举对应的编码值
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取枚举描述
     *
     * @return 枚举对应的中文描述
     */
    public String getDesc() {
        return desc;
    }
}
