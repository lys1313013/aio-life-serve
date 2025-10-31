package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.record.mapper.ITimeRecordEntity;
import com.lys.record.pojo.entity.TimeRecordEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/25 23:16
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/timeRecord")
public class TimeRecordController {
    private ITimeRecordEntity timeRecordMapper;

    public ITimeRecordEntity getBaseMapper() {
        return timeRecordMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<TimeRecordEntity>> query(
            @RequestBody CommonQuery<TimeRecordEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, userId);
        TimeRecordEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(TimeRecordEntity::getDate, condition.getDate());

        lambdaQueryWrapper.orderByDesc(TimeRecordEntity::getUpdateTime);
        Page<TimeRecordEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<TimeRecordEntity> iPage = timeRecordMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<TimeRecordEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody TimeRecordEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsInt());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDuration(entity.getEndTime() - entity.getStartTime());

        getBaseMapper().insert(entity);
        return ApiResponse.success(true);
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TimeRecordEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDuration(entity.getEndTime() - entity.getStartTime());
        getBaseMapper().updateById(entity);
        return ApiResponse.success(true);
    }

    @PostMapping("/batchUpdate")
    public ApiResponse<Boolean> batchUpdate(@RequestBody List<TimeRecordEntity> entityList) {
        // 按照日期删除
        LocalDate date = entityList.get(0).getDate();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, StpUtil.getLoginIdAsLong());
        lambdaQueryWrapper.eq(TimeRecordEntity::getDate, date);
        getBaseMapper().delete(lambdaQueryWrapper);

        entityList.forEach(entity -> {
            entity.setUserId(StpUtil.getLoginIdAsLong());
            entity.setCreateUser(StpUtil.getLoginIdAsInt());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDuration(entity.getEndTime() - entity.getStartTime());
        });
        getBaseMapper().insert(entityList);
        return ApiResponse.success(true);
    }

    /**
     * 删除
     * @param entity id
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody TimeRecordEntity entity) {
        LambdaQueryWrapper<TimeRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecordEntity::getId, entity.getId());
        queryWrapper.eq(TimeRecordEntity::getUserId, StpUtil.getLoginIdAsInt());

        getBaseMapper().delete(queryWrapper);
        getBaseMapper().deleteById(entity);
        return ApiResponse.success();
    }

    /**
     * 删除
     * @param entity id
     */
    @PostMapping("/deleteByDate")
    public ApiResponse<Void> deleteByDay(@RequestBody TimeRecordEntity entity) {
        LambdaQueryWrapper<TimeRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecordEntity::getDate, entity.getDate());
        queryWrapper.eq(TimeRecordEntity::getUserId, StpUtil.getLoginIdAsInt());

        getBaseMapper().delete(queryWrapper);
        return ApiResponse.success();
    }
}
