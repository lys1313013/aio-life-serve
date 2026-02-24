package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.query.TimeWeekQuery;
import top.aiolife.record.pojo.req.TimeRecordReq;
import top.aiolife.record.pojo.vo.RecommendNextVO;
import top.aiolife.record.pojo.vo.TimeRecordVO;
import top.aiolife.record.service.IExerciseRecordService;
import top.aiolife.record.service.ITimeRecordService;

import java.time.LocalDate;
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
    private final IExerciseRecordService exerciseRecordService;

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

    /**
     * 根据 id 查询
     * @param id id
     */
    @GetMapping("/{id}")
    public ApiResponse<TimeRecordVO> getById(@PathVariable("id") String id) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<TimeRecordEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TimeRecordEntity::getId, id);
        lambdaQueryWrapper.eq(TimeRecordEntity::getUserId, userId);
        TimeRecordEntity entity = timeRecordService.getOne(lambdaQueryWrapper);
        if (entity == null) {
            return ApiResponse.success(null);
        }

        TimeRecordVO vo = new TimeRecordVO();
        BeanUtil.copyProperties(entity, vo);

        List<ExerciseRecordEntity> exercises = exerciseRecordService.lambdaQuery()
                .eq(ExerciseRecordEntity::getTimeId, id)
                .eq(ExerciseRecordEntity::getUserId, userId)
                .list();
        vo.setExercises(exercises);

        return ApiResponse.success(vo);
    }

    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody TimeRecordReq timeRecordReq) {
        timeRecordService.saveTimeRecord(timeRecordReq);
        return ApiResponse.success();
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TimeRecordReq timeRecordReq) {
        timeRecordService.updateTimeRecord(timeRecordReq);
        return ApiResponse.success();
    }

    /**
     * 删除
     * @param entity id
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody TimeRecordEntity entity) {
        timeRecordService.removeById(entity.getId(),  StpUtil.getLoginIdAsInt());
        return ApiResponse.success();
    }

    /**
     * 删除
     * @param entity id
     */
    @PostMapping("/deleteByDate")
    public ApiResponse<Void> deleteByDay(@RequestBody TimeRecordEntity entity) {
        timeRecordService.removeByDate(entity.getDate(), StpUtil.getLoginIdAsInt());
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
