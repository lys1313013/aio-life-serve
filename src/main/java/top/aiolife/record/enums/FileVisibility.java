package top.aiolife.record.enums;

import lombok.Getter;

/**
 * 文件可见性。
 */
@Getter
public enum FileVisibility {

    PRIVATE(0),
    PUBLIC(1);

    private final int value;

    FileVisibility(int value) {
        this.value = value;
    }
}
