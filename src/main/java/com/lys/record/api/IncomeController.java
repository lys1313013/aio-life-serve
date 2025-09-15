package com.lys.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lys.core.query.CommonQuery;
import com.lys.core.resq.ApiResponse;
import com.lys.core.resq.PageResp;
import com.lys.core.util.SysUtil;
import com.lys.record.mapper.IIncomeMapper;
import com.lys.record.pojo.entity.IncomeEntity;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/09/14 21:09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/income")
public class IncomeController {
    private IIncomeMapper incomeMapper;

    public IIncomeMapper getBaseMapper() {
        return incomeMapper;
    }


    @PostMapping("/query")
    public ApiResponse<PageResp<IncomeEntity>> query(
            @RequestBody CommonQuery<IncomeEntity> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<IncomeEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IncomeEntity::getUserId, userId);
        IncomeEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getIncTypeId()), IncomeEntity::getIncTypeId,
                condition.getIncTypeId());
        lambdaQueryWrapper.orderByDesc(IncomeEntity::getIncDate);
        Page<IncomeEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<IncomeEntity> iPage = incomeMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<IncomeEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }


    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody IncomeEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsInt());
        boolean b = getBaseMapper().insertOrUpdate(entity);
        return ApiResponse.success(b);
    }

    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody IncomeEntity entity) {
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }
}