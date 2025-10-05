package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.constant.StatusConst;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.core.util.SysUtil;
import com.lys.record.mapper.IExpenseMapper;
import com.lys.record.pojo.entity.ExpenseEntity;
import com.lys.record.pojo.req.CommonReq;
import com.lys.record.service.IExpenseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/03 21:01
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/expense")
public class ExpController {

    private IExpenseService expenseService;

    private IExpenseMapper expenseMapper;

    public IExpenseMapper getBaseMapper() {
        return expenseMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<ExpenseEntity>> query(
            @RequestBody CommonQuery<ExpenseEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<ExpenseEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExpenseEntity::getUserId, userId);
        lambdaQueryWrapper.eq(ExpenseEntity::getIsDeleted, StatusConst.NO_DELETE);
        ExpenseEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getExpTypeId()), ExpenseEntity::getExpTypeId,
                condition.getExpTypeId());
        lambdaQueryWrapper.orderByDesc(ExpenseEntity::getExpTime);
        Page<ExpenseEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<ExpenseEntity> iPage = expenseMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<ExpenseEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody ExpenseEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsInt());
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }

    // 批量新增
    @PostMapping("/saveBatch")
    public ApiResponse<Boolean> saveBatch(@RequestBody List<ExpenseEntity> list) {
        for (ExpenseEntity entity : list) {
            int userId = StpUtil.getLoginIdAsInt();
            entity.setUserId(userId);
            entity.setCreateUser(userId);
            entity.setUpdateUser(userId);

            log.info("entity:{}", entity);
        }
        expenseService.saveBatch(list);
        return ApiResponse.success();
    }

    // 批量删除
    @PostMapping("/deleteBatch")
    public ApiResponse<Boolean> deleteBatch(@RequestBody CommonReq commonReq) {
        // todo 加权限删除
        expenseService.removeBatchByIds(commonReq.getIdList());
        return ApiResponse.success();
    }

}
