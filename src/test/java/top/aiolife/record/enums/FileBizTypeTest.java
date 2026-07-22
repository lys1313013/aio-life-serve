package top.aiolife.record.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBizTypeTest {

    @Test
    void fromBizType_支持所有已定义业务类型() {
        for (FileBizType type : FileBizType.values()) {
            assertEquals(type, FileBizType.fromBizType(type.getBizType()));
        }
    }

    @Test
    void fromBizType_拒绝未知业务类型() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileBizType.fromBizType("unknown")
        );

        assertEquals("不支持的文件业务类型: unknown", exception.getMessage());
    }

    @Test
    void visibility_头像公开且其他文件私有() {
        assertEquals(FileVisibility.PUBLIC, FileBizType.AVATAR.getVisibility());
        assertTrue(Arrays.stream(FileBizType.values())
                .filter(type -> type != FileBizType.AVATAR)
                .allMatch(type -> type.getVisibility() == FileVisibility.PRIVATE));
    }
}
