package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.vo.RecommendNextVO;
import top.aiolife.record.pojo.query.TimeWeekQuery;
import top.aiolife.record.service.ITimeRecordService;
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
    private final ITimeRecordService timeRecordService;

    public ITimeRecordService getBaseMapper() {
        return timeRecordService;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<TimeRecordEntity>> query(
            @RequestBody CommonQuery<TimeRecordEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(
                TimeRecordEntity::getId,
                TimeRecordEntity::getDate,
                TimeRecordEntity::getCategoryId,
                TimeRecordEntity::getStartTime,
                TimeRecordEntity::getEndTime,
                TimeRecordEntity::getTitle);
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, userId);
        TimeRecordEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(TimeRecordEntity::getDate, condition.getDate());

        lambdaQueryWrapper.orderByDesc(TimeRecordEntity::getUpdateTime);
        Page<TimeRecordEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<TimeRecordEntity> iPage = timeRecordService.page(page, lambdaQueryWrapper);
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
                TimeRecordEntity::getTitle);
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, userId);
        TimeWeekQuery condition = query.getCondition();
        lambdaQueryWrapper.between(TimeRecordEntity::getDate, condition.getStartDate(), condition.getEndDate());

        lambdaQueryWrapper.orderByDesc(TimeRecordEntity::getUpdateTime);
        List<TimeRecordEntity> list = timeRecordService.list(lambdaQueryWrapper);
        return ApiResponse.success(list);
    }

    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody TimeRecordEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsInt());
        entity.setUpdateTime(LocalDateTime.now());
        
        // 限制时间最大值为 1439 (23:59)
        if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
        if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);
        
        entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);

        timeRecordService.save(entity);
        return ApiResponse.success(true);
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TimeRecordEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        
        // 限制时间最大值为 1439 (23:59)
        if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
        if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);

        entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);
        timeRecordService.updateById(entity);
        return ApiResponse.success(true);
    }

    @PostMapping("/batchUpdate")
    public ApiResponse<Boolean> batchUpdate(@RequestBody List<TimeRecordEntity> entityList) {
        // 按照日期删除
        LocalDate date = entityList.get(0).getDate();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, StpUtil.getLoginIdAsLong());
        lambdaQueryWrapper.eq(TimeRecordEntity::getDate, date);
        timeRecordService.remove(lambdaQueryWrapper);

        entityList.forEach(entity -> {
            entity.setUserId(StpUtil.getLoginIdAsLong());
            entity.setCreateUser(StpUtil.getLoginIdAsInt());
            entity.setUpdateTime(LocalDateTime.now());
            
            // 限制时间最大值为 1439 (23:59)
            if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
            if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);

            entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);
        });
        timeRecordService.saveBatch(entityList);
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

        timeRecordService.remove(queryWrapper);
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

        timeRecordService.remove(queryWrapper);
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
        // 周一取上周五，周六取上周日
        // todo 按照工作日计算
        int dayOfWeek = LocalDate.parse(date).getDayOfWeek().getValue();
        if (dayOfWeek == 6) {
            date = LocalDate.parse(date).minusDays(6).toString();
        } else if (dayOfWeek == 1) {
            date = LocalDate.parse(date).minusDays(3).toString();
        } else  {
            // date 转成昨天
            LocalDate localDate = LocalDate.parse(date);
            LocalDate yesterday = localDate.minusDays(1);
            date = yesterday.toString();
        }

        TimeRecordEntity timeRecordEntity = timeRecordService.recommendType(userId, date, time);
        if (timeRecordEntity == null) {
            return ApiResponse.success("");
        }
        return ApiResponse.success(timeRecordEntity.getCategoryId());
    }

    /**
     * 推荐下一个时间块
     * @param date 日期 yyyy-MM-dd
     */
    @GetMapping("/recommendNext")
    public ApiResponse<RecommendNextVO> recommendNext(String date) {
        int userId = StpUtil.getLoginIdAsInt();
        RecommendNextVO result = timeRecordService.recommendNext(userId, date);
        
        // 获取推荐分类
        TimeRecordEntity recommend = result.getRecommend();
        String categoryId = recommendType(date, recommend.getStartTime()).getData();
        recommend.setCategoryId(categoryId);

        return ApiResponse.success(result);
    }
}
