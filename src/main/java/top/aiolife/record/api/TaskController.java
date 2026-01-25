package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.record.mapper.ITaskMapper;
import top.aiolife.record.pojo.entity.TaskEntity;
import top.aiolife.record.service.ITaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务控制器
 *
 * @author Lys
 * @date 2025/04/12 14:46
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final ITaskService taskService;

    private final ITaskMapper taskMapper;

    public ITaskMapper getBaseMapper() {
        return taskMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<TaskEntity>> query(
            @RequestBody CommonQuery<TaskEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TaskEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TaskEntity::getUserId, userId);
        TaskEntity condition = query.getCondition();
        lambdaQueryWrapper.orderByAsc(TaskEntity::getSortOrder);        // 分页
        Page<TaskEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<TaskEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<TaskEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    /**
     * 插入或更新
     *
     * @param entity
     */
    @PostMapping("/save")
    public ApiResponse<TaskEntity> save(@RequestBody TaskEntity entity) {
        entity.setId(null);
        // 获取token
        entity.setUserId(StpUtil.getLoginIdAsInt());
        getBaseMapper().insert(entity);
        return ApiResponse.success(entity);
    }

    /**
     * 更新
     *
     * @param entity
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TaskEntity entity) {
        // 获取token
        entity.setUserId(StpUtil.getLoginIdAsInt());
        getBaseMapper().updateById(entity);
        return ApiResponse.success();
    }

    /**
     * 删除
     * @param entity id
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody TaskEntity entity) {
        getBaseMapper().deleteById(entity);
        return ApiResponse.success();
    }

    /**
     * 拖拽排序
     *
     * @param list 只传id、columnId和sortOrder
     */
    @PostMapping("/reSort")
    public ApiResponse<Void> reSort(@RequestBody List<TaskEntity> list) {
        taskService.updateBatchById(list);
        return ApiResponse.success();
    }
}
