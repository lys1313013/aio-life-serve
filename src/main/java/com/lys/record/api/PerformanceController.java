package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.record.mapper.IPerformanceMapper;
import com.lys.record.pojo.entity.PerformanceEntity;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/07 22:31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/performance")
public class PerformanceController {

    private IPerformanceMapper performanceMapper;

    public IPerformanceMapper getBaseMapper() {
        return performanceMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<PerformanceEntity>> query(
            @RequestBody CommonQuery<PerformanceEntity> query) {
        LambdaQueryWrapper<PerformanceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 分页
        Page<PerformanceEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<PerformanceEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<PerformanceEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody PerformanceEntity entity) {
        int userId = StpUtil.getLoginIdAsInt();
        entity.setCreateBy(userId);
        entity.setUpdateBy(userId);
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }

    /**
     * 删除数据
     *
     * @author Lys
     * @date 2025/4/7
     */
    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody PerformanceEntity entity) {
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }

}
