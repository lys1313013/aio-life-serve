package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.record.mapper.IMemoMapper;
import com.lys.record.pojo.entity.MemoEntity;
import com.lys.record.service.IMemoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 备忘录控制器
 *
 * @author Lys
 * @date 2025/12/07
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final IMemoService memoService;
    private final IMemoMapper memoMapper;

    /**
     * 查询列表
     */
    @PostMapping("/query")
    public ApiResponse<PageResp<MemoEntity>> query(@RequestBody CommonQuery<MemoEntity> query) {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<MemoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MemoEntity::getUserId, userId);
        
        if (query.getCondition() != null && query.getCondition().getContent() != null) {
            lambdaQueryWrapper.like(MemoEntity::getContent, query.getCondition().getContent());
        }
        
        lambdaQueryWrapper.orderByDesc(MemoEntity::getUpdateTime);
        
        Page<MemoEntity> page = new Page<>(query.getPage(), query.getPageSize());
        
        Page<MemoEntity> resultPage = memoMapper.selectPage(page, lambdaQueryWrapper);
        return ApiResponse.success(PageResp.of(resultPage.getRecords(), resultPage.getTotal()));
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> save(@RequestBody MemoEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setCreateUser(StpUtil.getLoginIdAsInt());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        memoMapper.insert(entity);
        return ApiResponse.success(true);
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody MemoEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setUpdateUser(StpUtil.getLoginIdAsInt());
        entity.setUpdateTime(LocalDateTime.now());
        memoMapper.updateById(entity);
        return ApiResponse.success(true);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody MemoEntity entity) {
        memoMapper.deleteById(entity.getId());
        return ApiResponse.success(true);
    }
}
