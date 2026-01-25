package top.aiolife.core.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 类功能描述
 *
 * @author lys
 * @date 2025/2/26
 */
@Setter
@Getter
public class CommonQuery<T> {

  /**
   * 当前页数
   */
  private Integer page = 1;

  /**
   * 页数
   */
  private Integer pageSize = 50;

  T condition;
}
