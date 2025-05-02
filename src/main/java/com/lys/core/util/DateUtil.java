package com.lys.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 *
 * @author Lys
 * @date 2025/05/02 23:55
 */
public class DateUtil {

    /**
     * 获取当前时间 yyyy-MM-dd格式
     */
    public static String getNowFormatDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
