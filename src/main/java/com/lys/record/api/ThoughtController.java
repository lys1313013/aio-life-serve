package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.record.mapper.IRelaEventMapper;
import com.lys.record.mapper.IThoughtMapper;
import com.lys.record.pojo.entity.RelaEventEntity;
import com.lys.record.pojo.entity.ThoughtEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025-11-16 17:01
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/thought")
public class ThoughtController {
    private final IThoughtMapper thoughtMapper;

    private final IRelaEventMapper relaEventMapper;

    public IThoughtMapper getBaseMapper() {
        return thoughtMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<ThoughtEntity>> query(
            @RequestBody CommonQuery<ThoughtEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<ThoughtEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ThoughtEntity::getUserId, userId);
        ThoughtEntity condition = query.getCondition();

        lambdaQueryWrapper.orderByDesc(ThoughtEntity::getUpdateTime);
        Page<ThoughtEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<ThoughtEntity> iPage = thoughtMapper.selectPage(page, lambdaQueryWrapper);


        // 查询明细
        List<Long> thoughtIdList = iPage.getRecords().stream().map(ThoughtEntity::getId).toList();
        LambdaQueryWrapper<RelaEventEntity> relaEventLambdaQueryWrapper = new LambdaQueryWrapper<>();
        relaEventLambdaQueryWrapper.in(RelaEventEntity::getThoughtId, thoughtIdList);
        List<RelaEventEntity> relaEventEntityList = relaEventMapper.selectList(relaEventLambdaQueryWrapper);
        // 关联事件
        iPage.getRecords().forEach(thoughtVO -> {
            List<RelaEventEntity> eventEntityList = relaEventEntityList.stream().filter(eventEntity -> eventEntity.getThoughtId().equals(thoughtVO.getId())).toList();
            thoughtVO.setEvents(eventEntityList);
        });

        PageResp<ThoughtEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());

        return ApiResponse.success(objectPageResp);
    }
    
    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody ThoughtEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsInt());
        entity.setUpdateTime(LocalDateTime.now());
        getBaseMapper().insert(entity);
        entity.getEvents().forEach(eventEntity -> {
            eventEntity.setThoughtId(entity.getId());
            relaEventMapper.insert(eventEntity);
        });
        return ApiResponse.success(true);
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody ThoughtEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        getBaseMapper().updateById(entity);
        // 更新事件
        entity.getEvents().forEach(eventEntity -> {
            eventEntity.setThoughtId(entity.getId());
            relaEventMapper.insertOrUpdate(eventEntity);
        });
        return ApiResponse.success(true);
    }

}
