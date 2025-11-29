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
import com.lys.record.pojo.query.TimeWeekQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 查询指定日期的记录
     * @param query 查询参数
     */
    @PostMapping("/queryForWeek")
    public ApiResponse<List<TimeRecordEntity>> queryForWeek(
            @RequestBody CommonQuery<TimeWeekQuery> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(TimeRecordEntity::getId,
                TimeRecordEntity::getCategoryId,
                TimeRecordEntity::getDate,
                TimeRecordEntity::getStartTime,
                TimeRecordEntity::getEndTime,
                TimeRecordEntity::getTitle,
                TimeRecordEntity::getDescription,
                TimeRecordEntity::getIsManual);
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, userId);
        TimeWeekQuery condition = query.getCondition();
        lambdaQueryWrapper.between(TimeRecordEntity::getDate, condition.getStartDate(), condition.getEndDate());

        lambdaQueryWrapper.orderByDesc(TimeRecordEntity::getUpdateTime);
        List<TimeRecordEntity> list = timeRecordMapper.selectList(lambdaQueryWrapper);
        return ApiResponse.success(list);
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

    /**
     * 推荐分类
     * @param date 日期
     * @param time 时间
     * @return 分类id
     */
    @GetMapping("/recommendType")
    public ApiResponse<String> recommendType(String date, int time) {
        int userId = StpUtil.getLoginIdAsInt();
        // 判断 如果是周末的话 date 变为减7天
        if (LocalDate.parse(date).getDayOfWeek().getValue() >= 6) {
            date = LocalDate.parse(date).minusDays(7).toString();
        } else  {
            // date 转成昨天
            LocalDate localDate = LocalDate.parse(date);
            LocalDate yesterday = localDate.minusDays(1);
            date = yesterday.toString();
        }

        TimeRecordEntity timeRecordEntity = timeRecordMapper.recommendType(userId, date, time);
        if (timeRecordEntity == null) {
            return ApiResponse.success("");
        }
        return ApiResponse.success(timeRecordEntity.getCategoryId());
    }
}
