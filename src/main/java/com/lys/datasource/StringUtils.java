package com.lys.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
public class StringUtils {

    public static String extractNumber(String input) {
        // 正则表达式匹配数字
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }

}
