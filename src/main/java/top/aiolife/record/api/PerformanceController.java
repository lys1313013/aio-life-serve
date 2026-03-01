package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.record.mapper.IPerformanceMapper;
import top.aiolife.record.pojo.entity.PerformanceEntity;
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
        lambdaQueryWrapper.eq(PerformanceEntity::getCreateBy, StpUtil.getLoginIdAsLong());

        // 分页
        Page<PerformanceEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<PerformanceEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<PerformanceEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody PerformanceEntity entity) {
        long userId = StpUtil.getLoginIdAsLong();
        entity.setCreateBy(userId);
        entity.setUpdateBy(userId);
        
        if (entity.getId() == null) {
            getBaseMapper().insert(entity);
        } else {
            LambdaQueryWrapper<PerformanceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PerformanceEntity::getId, entity.getId());
            wrapper.eq(PerformanceEntity::getCreateBy, userId);
            getBaseMapper().update(entity, wrapper);
        }
        return ApiResponse.success(true);
    }

    /**
     * 删除数据
     *
     * @author Lys
     * @date 2025/4/7
     */
    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody PerformanceEntity entity) {
        LambdaQueryWrapper<PerformanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEntity::getId, entity.getId());
        wrapper.eq(PerformanceEntity::getCreateBy, StpUtil.getLoginIdAsLong());
        boolean b = getBaseMapper().delete(wrapper) > 0;
        return ApiResponse.success(b);
    }

}
