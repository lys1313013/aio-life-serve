package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.langchain4j.agent.tool.Tool;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.mcp.annotation.McpOperation;
import top.aiolife.record.mapper.IRelaEventMapper;
import top.aiolife.record.mapper.IThoughtMapper;
import top.aiolife.record.pojo.entity.ThoughtRelaEventEntity;
import top.aiolife.record.pojo.entity.ThoughtEntity;
import top.aiolife.record.pojo.req.CommonReq;
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
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<ThoughtEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ThoughtEntity::getUserId, userId);
        ThoughtEntity condition = query.getCondition();

        lambdaQueryWrapper.orderByDesc(ThoughtEntity::getUpdateTime);
        Page<ThoughtEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<ThoughtEntity> iPage = thoughtMapper.selectPage(page, lambdaQueryWrapper);


        // 查询明细
        List<Long> thoughtIdList = iPage.getRecords().stream().map(ThoughtEntity::getId).toList();
        if (!thoughtIdList.isEmpty()) {
            LambdaQueryWrapper<ThoughtRelaEventEntity> relaEventLambdaQueryWrapper = new LambdaQueryWrapper<>();
            relaEventLambdaQueryWrapper.in(ThoughtRelaEventEntity::getThoughtId, thoughtIdList);
            List<ThoughtRelaEventEntity> thoughtRelaEventEntityList = relaEventMapper.selectList(relaEventLambdaQueryWrapper);
            // 关联事件
            iPage.getRecords().forEach(thoughtVO -> {
                List<ThoughtRelaEventEntity> eventEntityList = thoughtRelaEventEntityList.stream().filter(eventEntity -> eventEntity.getThoughtId().equals(thoughtVO.getId())).toList();
                thoughtVO.setEvents(eventEntityList);
            });
        }

        PageResp<ThoughtEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());

        return ApiResponse.success(objectPageResp);
    }
    
    @PostMapping("/save")
    @Tool("保存一条想法，并可附带多个关联事件")
    @McpOperation(
            name = "thought_save",
            description = "保存一条想法，并可附带多个关联事件",
            ignoreInputFields = {
                    "id", "userId", "createUser", "createTime", "updateTime", "updateUser", "isDeleted",
                    "events[].id", "events[].thoughtId", "events[].createUser", "events[].createTime",
                    "events[].updateTime", "events[].updateUser", "events[].isDeleted"
            }
    )
    public ApiResponse<Boolean> save(@RequestBody ThoughtEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        getBaseMapper().insert(entity);
        List<ThoughtRelaEventEntity> events = entity.getEvents();
        if (events != null) {
            events.forEach(eventEntity -> {
                eventEntity.setThoughtId(entity.getId());
                relaEventMapper.insert(eventEntity);
            });
        }
        return ApiResponse.success(true);
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody ThoughtEntity entity) {
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setUpdateTime(LocalDateTime.now());
        
        LambdaQueryWrapper<ThoughtEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThoughtEntity::getId, entity.getId());
        wrapper.eq(ThoughtEntity::getUserId, userId);
        
        int rows = getBaseMapper().update(entity, wrapper);
        
        if (rows > 0) {
            // 更新事件
            entity.getEvents().forEach(eventEntity -> {
                eventEntity.setThoughtId(entity.getId());
                relaEventMapper.insertOrUpdate(eventEntity);
            });
            return ApiResponse.success(true);
        }
        return ApiResponse.error("无权操作或记录不存在");
    }

    /**
     * 批量删除
     */
    @PostMapping("/batchDelete")
    public ApiResponse<Boolean> delete(@RequestBody CommonReq CommonReq) {
        LambdaUpdateWrapper<ThoughtEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(ThoughtEntity::getUserId, StpUtil.getLoginIdAsLong());
        lambdaUpdateWrapper.in(ThoughtEntity::getId, CommonReq.getIdList());
        getBaseMapper().delete(lambdaUpdateWrapper);
        return ApiResponse.success(true);
    }

}
