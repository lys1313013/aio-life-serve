package top.aiolife.record.pojo.enums;

/**
 * 目标类型枚举
 *
 * @author Lys
 * @date 2026/03/30
 */
public enum GoalTypeEnum {

    /**
     * 日目标
     */
    DAY(1, "日目标"),

    /**
     * 周目标
     */
    WEEK(2, "周目标"),

    /**
     * 月度目标
     */
    MONTH(3, "月度目标"),

    /**
     * 季度目标
     */
    QUARTER(4, "季度目标"),

    /**
     * 半年目标
     */
    HALF_YEAR(5, "半年目标"),

    /**
     * 年度目标
     */
    YEAR(6, "年度目标"),

    /**
     * 三年目标
     */
    THREE_YEAR(7, "三年目标"),

    /**
     * 五年目标
     */
    FIVE_YEAR(8, "五年目标"),

    /**
     * 十年目标
     */
    TEN_YEAR(9, "十年目标"),

    /**
     * 人生目标
     */
    LIFE(10, "人生目标");

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
