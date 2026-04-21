package top.aiolife.record.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeTypeEnum {
    REQUIRED(1, "必须"),
    POSITIVE(2, "积极"),
    NEGATIVE(3, "休闲");

    private final Integer code;
    private final String label;

    public static TimeTypeEnum fromCode(Integer code) {
        if (code == null) return REQUIRED;
        for (TimeTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return REQUIRED;
    }
}
