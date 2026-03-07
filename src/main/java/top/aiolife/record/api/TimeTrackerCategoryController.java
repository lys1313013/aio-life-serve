package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.record.pojo.entity.entity.TimeTrackerCategoryEntity;
import top.aiolife.record.service.ITimeTrackerCategoryService;

import java.util.List;

/**
 * 时间追踪-分类配置(TimeTrackerCategory) 控制器
 *
 * @author Lys1313013
 * @since 2026-03-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/timeTrackerCategory")
public class TimeTrackerCategoryController {

    private final ITimeTrackerCategoryService categoryService;

    /**
     * 获取当前用户的所有分类
     */
    @GetMapping("/list")
    public ApiResponse<List<TimeTrackerCategoryEntity>> list() {
        long userId = StpUtil.getLoginIdAsLong();
        List<TimeTrackerCategoryEntity> list = categoryService.list(
                new LambdaQueryWrapper<TimeTrackerCategoryEntity>()
                        .eq(TimeTrackerCategoryEntity::getUserId, userId)
                        .orderByAsc(TimeTrackerCategoryEntity::getSort)
        );
        return ApiResponse.success(list);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody TimeTrackerCategoryEntity entity) {
        long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setCode(IdUtil.fastSimpleUUID());
        entity.fillCreateCommonField(userId);
        return ApiResponse.success(categoryService.save(entity));
    }

    /**
     * 更新分类
     */
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody TimeTrackerCategoryEntity entity) {
        long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.fillUpdateCommonField(userId);
        return ApiResponse.success(categoryService.updateById(entity));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(categoryService.removeById(id));
    }

    /**
     * 拖拽排序
     *
     * @param list 只传id和sort
     */
    @PostMapping("/reSort")
    public ApiResponse<Void> reSort(@RequestBody List<TimeTrackerCategoryEntity> list) {
        long userId = StpUtil.getLoginIdAsLong();
        for (TimeTrackerCategoryEntity entity : list) {
            categoryService.update(
                    new LambdaUpdateWrapper<TimeTrackerCategoryEntity>()
                            .eq(TimeTrackerCategoryEntity::getId, entity.getId())
                            .eq(TimeTrackerCategoryEntity::getUserId, userId)
                            .set(TimeTrackerCategoryEntity::getSort, entity.getSort())
            );
        }
        return ApiResponse.success();
    }
}
