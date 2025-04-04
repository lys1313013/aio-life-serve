package com.lys.core.resq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 分页数据返回对象
 *
 * @author Lys
 * @date 2024/9/25
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResp<T> {

  /**
   * 表格数据
   */
  private List<T> items;

  /**
   * 总数量
   */
  private Long total;

  /**
   * 创建分页数据返回对象
   *
   * @param rows 分页数据
   * @param total 总数量
   * @return 分页数据返回对象
   */
  public static <T> PageResp<T> of(List<T> rows, Integer total) {
    if (total == null) {
      return new PageResp<>(rows, null);
    } else {
      return new PageResp<>(rows, total.longValue());

    }
  }

  /**
   * 创建分页数据返回对象
   *
   * @param rows 分页数据
   * @param total 总数量
   * @return 分页数据返回对象
   */
  public static <T> PageResp<T> of(List<T> rows, Long total) {
    return new PageResp<>(rows, total);
  }
}