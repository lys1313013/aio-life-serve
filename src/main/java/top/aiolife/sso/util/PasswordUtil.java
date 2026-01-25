package top.aiolife.sso.util;

import cn.hutool.crypto.SecureUtil;

/**
 * Md5工具类
 *
 * @author Lys
 * @date 2025/3/15
 */
public class PasswordUtil {

    public static String encryptPassword(String password, String salt) {
        // 使用hutool的SecureUtil进行MD5哈希，将密码和盐值拼接后哈希
        return SecureUtil.md5(password + salt);
    }
}
