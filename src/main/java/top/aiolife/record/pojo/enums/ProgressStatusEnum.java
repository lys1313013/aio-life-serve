package top.aiolife.record.pojo.enums;

/**
 * 进度状态枚举
 *
 * @author Lys
 * @date 2026/06/19
 */
public enum ProgressStatusEnum {

    /**
     * 未开始
     */
    NOT_STARTED(0, "未开始"),

    /**
     * 进行中
     */
    IN_PROGRESS(1, "进行中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 搁置
     */
    ON_HOLD(3, "搁置");

    private final Integer code;

    private final String desc;

    ProgressStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
