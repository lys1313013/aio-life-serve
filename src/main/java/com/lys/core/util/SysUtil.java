package com.lys.core.util;

/**
 * @author lys
 */
public class SysUtil {

  public static boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  public static boolean isEmpty(Integer integer) {
    return integer == null;
  }

  public static boolean isNotEmpty(Integer integer) {
    return !isEmpty(integer);
  }
}
