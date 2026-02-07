package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.constant.StatusConst;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.core.util.SysUtil;
import top.aiolife.record.mapper.IBVideoMapper;
import top.aiolife.record.pojo.entity.BVideoEntity;
import top.aiolife.record.pojo.vo.BVideoStatisticsVO;
import top.aiolife.record.pojo.vo.StatusCount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 23:13
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/bilibili-video")
public class BVideoController {

    private IBVideoMapper bVideoMapper;

    public IBVideoMapper getBaseMapper() {
        return bVideoMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<BVideoEntity>> query(
            @RequestBody CommonQuery<BVideoEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<BVideoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BVideoEntity::getUserId, userId);
        lambdaQueryWrapper.eq(BVideoEntity::getIsDeleted, StatusConst.NO_DELETE);
        BVideoEntity condition = query.getCondition();
        if (SysUtil.isNotEmpty(condition.getStatus())) {
            // 0 为查询全部状态
            if (0 != condition.getStatus()) {
                lambdaQueryWrapper.eq(BVideoEntity::getStatus,
                        condition.getStatus());
            }
        }

        lambdaQueryWrapper.orderByAsc(BVideoEntity::getStatus);
        lambdaQueryWrapper.orderByDesc(BVideoEntity::getUpdateTime);
        Page<BVideoEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<BVideoEntity> iPage = bVideoMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<BVideoEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody BVideoEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setUpdateUser(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }


    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody BVideoEntity entity) {
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }

    @GetMapping("/getStatusCount")
    public ApiResponse<Map> getStatusCount() {
        int userId = StpUtil.getLoginIdAsInt();
        List<StatusCount> statusCount = getBaseMapper().getStatusCount(userId);
        Map<Integer, Integer> map = statusCount.stream().collect(
                Collectors.toMap(StatusCount::getStatus, StatusCount::getCount));
        return ApiResponse.success(map);
    }

    @GetMapping("/statistics")
    public ApiResponse<BVideoStatisticsVO> statistics() {
        int userId = StpUtil.getLoginIdAsInt();
        BVideoStatisticsVO statisticsVO = new BVideoStatisticsVO();
        Integer watchTime = bVideoMapper.getWatchTime(userId);
        Integer totalTime = bVideoMapper.getTotalTime(userId);
        if (watchTime != null) {
            statisticsVO.setStudiedSeconds(watchTime);
        }
        if (totalTime != null) {
            statisticsVO.setTotalSeconds(totalTime);
        }
        statisticsVO.setUnstudiedSeconds(totalTime - watchTime);

        return ApiResponse.success(statisticsVO);
    }

    @PostMapping("/tagVideo")
    public ApiResponse<Boolean> tagVideo(@RequestBody BVideoEntity entity) {
        log.info("tagVideo: {}", entity);
        entity.setUserId(StpUtil.getLoginIdAsLong());
        getBaseMapper().insert(entity);
        return ApiResponse.success();
    }

    @PostMapping("/syncProgress")
    public ApiResponse<Boolean> syncProgress(@RequestBody BVideoEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        log.info("bvid: {}", entity.getBvid());
        log.info("currentEpisode: {}", entity.getCurrentEpisode());
        LambdaUpdateWrapper<BVideoEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(BVideoEntity::getCurrentEpisode, entity.getCurrentEpisode());
        lambdaUpdateWrapper.set(BVideoEntity::getWatchedDuration, entity.getWatchedDuration());
        lambdaUpdateWrapper.eq(BVideoEntity::getUserId, entity.getUserId());
        lambdaUpdateWrapper.eq(BVideoEntity::getBvid, entity.getBvid());
        int update = getBaseMapper().update(null, lambdaUpdateWrapper);
        log.info("syncProgress: {}", update);
        return ApiResponse.success();
    }
}