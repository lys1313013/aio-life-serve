package top.aiolife.record.pojo.enums;

/**
 * 目标状态枚举
 *
 * @author Lys
 * @date 2026/03/30
 */
public enum GoalStatusEnum {

    /**
     * 待开始
     */
    PENDING(0, "待开始"),

    /**
     * 进行中
     */
    IN_PROGRESS(1, "进行中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 已放弃
     */
    ABANDONED(3, "已放弃");

    private final Integer code;

    private final String desc;

    GoalStatusEnum(Integer code, String desc) {
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
