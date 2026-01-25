package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.core.util.SysUtil;
import top.aiolife.record.mapper.IExerciseRecordMapper;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.req.CommonReq;
import top.aiolife.record.service.IExerciseRecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 运动记录控制器
 *
 * @author Lys
 * @date 2025-11-29 18:40
 */
@RestController
@AllArgsConstructor
@RequestMapping("/exerciseRecord")
public class ExerciseRecordController {

    private final IExerciseRecordMapper exerciseRecordMapper;

    private final IExerciseRecordService exerciseRecordService;

    public IExerciseRecordMapper getBaseMapper() {
        return exerciseRecordMapper;
    }

    /**
     * 查询运动记录列表
     */
    @PostMapping("/query")
    public ApiResponse<PageResp<ExerciseRecordEntity>> query(
            @RequestBody CommonQuery<ExerciseRecordEntity> query) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<ExerciseRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExerciseRecordEntity::getUserId, userId);
        ExerciseRecordEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getExerciseTypeId()), ExerciseRecordEntity::getExerciseTypeId, 
                condition.getExerciseTypeId());
//        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getExerciseDate()), ExerciseRecordEntity::getExerciseDate,
//                condition.getExerciseDate());
        lambdaQueryWrapper.orderByDesc(ExerciseRecordEntity::getExerciseDate, ExerciseRecordEntity::getCreateTime);
        // 分页
        Page<ExerciseRecordEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<ExerciseRecordEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<ExerciseRecordEntity> pageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(pageResp);
    }

    /**
     * 新增运动记录
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> add(@RequestBody ExerciseRecordEntity exerciseRecord) {
        Long userId = StpUtil.getLoginIdAsLong();
        exerciseRecord.setUserId(userId);
        exerciseRecord.setCreateUser(userId.intValue());
        return ApiResponse.success(getBaseMapper().insert(exerciseRecord) > 0);
    }

    /**
     * 修改运动记录
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody ExerciseRecordEntity exerciseRecord) {
        long userId = StpUtil.getLoginIdAsLong();
        exerciseRecord.setUpdateUser((int) userId);
        return ApiResponse.success(getBaseMapper().updateById(exerciseRecord) > 0);
    }

    // 批量删除
    @PostMapping("/deleteBatch")
    public ApiResponse<Boolean> deleteBatch(@RequestBody CommonReq commonReq) {
        LambdaQueryWrapper<ExerciseRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExerciseRecordEntity::getUserId, StpUtil.getLoginIdAsLong());
        lambdaQueryWrapper.in(ExerciseRecordEntity::getId, commonReq.getIdList());
        exerciseRecordService.remove(lambdaQueryWrapper);
        return ApiResponse.success();
    }

    /**
     * 获取运动记录详情
     */
    @GetMapping("/get/{id}")
    public ApiResponse<ExerciseRecordEntity> get(@PathVariable Long id) {
        LambdaQueryWrapper<ExerciseRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExerciseRecordEntity::getUserId, StpUtil.getLoginIdAsLong());
        lambdaQueryWrapper.eq(ExerciseRecordEntity::getId, id);
        return ApiResponse.success(getBaseMapper().selectOne(lambdaQueryWrapper));
    }
}