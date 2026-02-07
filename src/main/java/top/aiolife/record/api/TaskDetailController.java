package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.record.pojo.entity.TaskDetailEntity;
import top.aiolife.record.service.ITaskDetail;

import java.util.List;

/**
 * 任务详情控制器
 *
 * @author Lys
 * @date 2026-02-07 23:50
 */
@RestController
@AllArgsConstructor
@RequestMapping("/taskDetails")
public class TaskDetailController {

    private final ITaskDetail taskDetailService;

    /**
     * 获取任务详情列表
     *
     * @param taskId 任务ID
     * @return 详情列表
     */
    @GetMapping
    public ApiResponse<List<TaskDetailEntity>> list(@RequestParam Long taskId) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<TaskDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskDetailEntity::getTaskId, taskId);
        queryWrapper.eq(TaskDetailEntity::getUserId, userId);
        queryWrapper.orderByAsc(TaskDetailEntity::getIsCompleted, TaskDetailEntity::getId);
        return ApiResponse.success(taskDetailService.list(queryWrapper));
    }

    /**
     * 创建任务详情
     *
     * @param entity 详情实体
     * @return 创建后的实体
     */
    @PostMapping
    public ApiResponse<TaskDetailEntity> create(@RequestBody TaskDetailEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setCreateCommonField(userId);
        taskDetailService.save(entity);
        return ApiResponse.success(entity);
    }

    /**
     * 更新任务详情
     *
     * @param entity 详情实体
     * @return 是否成功
     */
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody TaskDetailEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setUpdateCommonField(userId);
        // 确保只能更新自己的详情
        LambdaQueryWrapper<TaskDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskDetailEntity::getId, entity.getId());
        queryWrapper.eq(TaskDetailEntity::getUserId, userId);
        return ApiResponse.success(taskDetailService.update(entity, queryWrapper));
    }

    /**
     * 删除任务详情
     *
     * @param id 详情ID
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<TaskDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskDetailEntity::getId, id);
        queryWrapper.eq(TaskDetailEntity::getUserId, userId);
        taskDetailService.remove(queryWrapper);
        return ApiResponse.success();
    }
}
