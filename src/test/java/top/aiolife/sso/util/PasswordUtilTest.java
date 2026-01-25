package top.aiolife.sso.util;

import org.junit.jupiter.api.Test;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/20 22:43
 */
class PasswordUtilTest {

    @Test
    void encryptPassword() {
        String password = "123456";
        String salt = "test";
        String encryptPassword = PasswordUtil.encryptPassword(password, salt);
        System.out.println(encryptPassword);
    }
}