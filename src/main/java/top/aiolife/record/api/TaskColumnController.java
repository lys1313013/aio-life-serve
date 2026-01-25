package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.constant.StatusConst;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.record.mapper.ITaskColumnMapper;
import top.aiolife.record.pojo.entity.TaskColumnEntity;
import top.aiolife.record.service.ITaskColumnService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务栏控制器
 *
 * @author Lys
 * @date 2025/04/12 14:36
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/taskColumn")
public class TaskColumnController {

    private ITaskColumnMapper taskColumnMapper;

    private ITaskColumnService taskColumnService;

    public ITaskColumnMapper getBaseMapper() {
        return taskColumnMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<TaskColumnEntity>> query(
            @RequestBody CommonQuery<TaskColumnEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TaskColumnEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TaskColumnEntity::getUserId, userId);
        lambdaQueryWrapper.eq(TaskColumnEntity::getIsDeleted, StatusConst.NO_DELETE);
        TaskColumnEntity condition = query.getCondition();
        lambdaQueryWrapper.orderByAsc(TaskColumnEntity::getSortOrder);        // 分页
        Page<TaskColumnEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<TaskColumnEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<TaskColumnEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    /**
     * 插入或更新
     *
     * @param entity
     */
    @PostMapping("/save")
    public ApiResponse<TaskColumnEntity> save(@RequestBody TaskColumnEntity entity) {
        // 查询当前最大的sort_order
        LambdaQueryWrapper<TaskColumnEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskColumnEntity::getUserId, entity.getUserId());
        queryWrapper.orderByAsc(TaskColumnEntity::getSortOrder);
        TaskColumnEntity taskColumnEntity = getBaseMapper().selectOne(queryWrapper);
        if (taskColumnEntity == null) {
            entity.setSortOrder(1);
        } else {
            entity.setSortOrder(taskColumnEntity.getSortOrder() + 1);
        }

        entity.setId(null);
        entity.setIsDeleted(StatusConst.NO_DELETE);
        // 获取token
        entity.setUserId(StpUtil.getLoginIdAsInt());
        getBaseMapper().insertOrUpdate(entity);

        return ApiResponse.success(entity);
    }

    /**
     * 更新
     *
     * @param entity
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TaskColumnEntity entity) {
        // 获取token
        entity.setUserId(StpUtil.getLoginIdAsInt());
        getBaseMapper().updateById(entity);
        return ApiResponse.success();
    }

    /**
     * 删除
     *
     * @param entity id
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody TaskColumnEntity entity) {
        getBaseMapper().deleteById(entity);
        return ApiResponse.success();
    }

    /**
     * 拖拽排序
     *
     * @param list 只传id和sortOrder
     */
    @PostMapping("/reSort")
    public ApiResponse<Void> reSort(@RequestBody List<TaskColumnEntity> list) {
        taskColumnService.updateBatchById(list);
        return ApiResponse.success();
    }
}
