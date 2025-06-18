package com.lys.datasource;

import com.lys.core.resq.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/06/18 23:33
 */
@RequestMapping("/zhihu")
@RestController
@AllArgsConstructor
public class ZhihuController {

    private ZhihuDataSource zhihuDataSource;

    @GetMapping("/hotList")
    public ApiResponse<Object> hotList() {
        return ApiResponse.success(zhihuDataSource.hotList());
    }
}
