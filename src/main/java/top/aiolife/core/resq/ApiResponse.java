package top.aiolife.core.resq;

import top.aiolife.core.constant.ResponseCodeConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


/**
 * 接口统一返回值
 *
 * @author Lys
 * @date 2024/9/25
 */
@Setter
@Getter
@NoArgsConstructor
public class ApiResponse<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 状态码
   */
  private String rscode;

  /**
   * 返回信息，一般是报错提示
   */
  private String result;

  /**
   * 返回结果
   */
  private T data;

  public ApiResponse(String rscode, String result, T data) {
    this.rscode = rscode;
    this.result = result;
    this.data = data;
  }

  public static <T> ApiResponse<T> success() {
    return new ApiResponse<>(ResponseCodeConst.RSCODE_SUCCESS, null, null);
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(ResponseCodeConst.RSCODE_SUCCESS, null, data);
  }

  public static <T> ApiResponse<T> success(T data, String result) {
    return new ApiResponse<>(ResponseCodeConst.RSCODE_SUCCESS, result, data);
  }

  /**
   * 只有错误码的返回值，不推荐使用
   *
   * @param rscode 状态码
   * @author Lys
   * @date 2024/9/26
   */
  public static <T> ApiResponse<T> error(String rscode) {
    return new ApiResponse<>(rscode, null, null);
  }

  /**
   * 返回状态码和提示信息
   *
   * @param rscode 状态码
   * @param result 提示信息
   * @author Lys
   * @date 2024/9/26
   */
  public static <T> ApiResponse<T> error(String rscode, String result) {
    return new ApiResponse<>(rscode, result, null);
  }

  /**
   * 返回状态码、提示信息和数据
   *
   * @param rscode 状态码
   * @param result 提示信息
   * @param data 数据
   * @author Lys
   * @date 2024/9/26
   */
  public static <T> ApiResponse<T> error(String rscode, String result, T data) {
    return new ApiResponse<>(rscode, result, data);
  }
}