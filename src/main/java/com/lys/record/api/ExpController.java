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
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.query.ExpenseQuery;
import com.lys.record.pojo.req.CommonReq;
import com.lys.record.pojo.vo.ExpStaByYearVO;
import com.lys.record.pojo.vo.ExpStaticByYearVO;
import com.lys.record.service.IExpenseService;
import com.lys.record.service.ISysDictService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private ISysDictService sysDictService;
    
    private IExpenseService expenseService;

    private IExpenseMapper expenseMapper;

    public IExpenseMapper getBaseMapper() {
        return expenseMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<ExpenseEntity>> query(
            @RequestBody CommonQuery<ExpenseQuery> query) {
        int userId = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<ExpenseEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExpenseEntity::getUserId, userId);
        lambdaQueryWrapper.eq(ExpenseEntity::getIsDeleted, StatusConst.NO_DELETE);
        ExpenseQuery condition = query.getCondition();
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getExpTypeId()), ExpenseEntity::getExpTypeId,
                condition.getExpTypeId());
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getPayTypeId()), ExpenseEntity::getPayTypeId,
                condition.getPayTypeId());
        lambdaQueryWrapper.likeRight(SysUtil.isNotEmpty(condition.getYear()), ExpenseEntity::getExpTime,
                condition.getYear());
        lambdaQueryWrapper.ge(condition.getStartTime() != null, ExpenseEntity::getExpTime, condition.getStartTime());
        lambdaQueryWrapper.le(condition.getEndTime() != null, ExpenseEntity::getExpTime, condition.getEndTime());
        lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getRemark()), ExpenseEntity::getRemark, condition.getRemark());
        lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getCounterparty()), ExpenseEntity::getCounterparty, condition.getCounterparty());
        lambdaQueryWrapper.like(SysUtil.isNotEmpty(condition.getExpDesc()), ExpenseEntity::getExpDesc, condition.getExpDesc());
        lambdaQueryWrapper.orderByDesc(ExpenseEntity::getExpTime);
        Page<ExpenseEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<ExpenseEntity> iPage = expenseMapper.selectPage(page, lambdaQueryWrapper);
        PageResp<ExpenseEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody ExpenseEntity entity) {
        entity.setUserId(StpUtil.getLoginIdAsInt());
        // 新增时，交易金额为空时，默认设置为记账金额
        if (entity.getId() == null && entity.getTransactionAmt() == null) {
            entity.setTransactionAmt(entity.getAmt());
        }
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

    @PostMapping("/delete")
    public ApiResponse<Boolean> delete(@RequestBody ExpenseEntity entity) {
        boolean b = getBaseMapper().deleteById(entity) > 0;
        return ApiResponse.success(b);
    }

    // 批量删除
    @PostMapping("/deleteBatch")
    public ApiResponse<Boolean> deleteBatch(@RequestBody CommonReq commonReq) {
        // todo 加权限删除
        expenseService.removeBatchByIds(commonReq.getIdList());
        return ApiResponse.success();
    }

    /**
     * 按年度统计支出
     */
    @PostMapping("/statisticsByYear")
    public ApiResponse<Object> statisticsByYear() {
        int userId = StpUtil.getLoginIdAsInt();
        List<ExpStaByYearVO> list = expenseMapper.statisticsByYear(userId);
        List<ExpStaticByYearVO> ans = new ArrayList<>();

        // 获取收入类型字典数据
        List<SysDictDataEntity> dictDataList = sysDictService.getDictDataByDictType("exp_type");
        Map<Integer, String> dictMap = dictDataList.stream()
                .collect(Collectors.toMap(SysDictDataEntity::getDictCode, SysDictDataEntity::getDictLabel));

        // 按照年度汇总
        Map<Integer, List<ExpStaByYearVO>> collect = list.stream()
                .collect(Collectors.groupingBy(ExpStaByYearVO::getYear));
        for (Map.Entry<Integer, List<ExpStaByYearVO>> entry : collect.entrySet()) {
            ExpStaticByYearVO expStaticByYearVO = new ExpStaticByYearVO();
            expStaticByYearVO.setYear(entry.getKey());
            List<ExpStaByYearVO> value = entry.getValue();
            value.forEach(item -> {
                item.setTypeName(dictMap.get(item.getTypeId()));
            });
            expStaticByYearVO.setDetail(value);
            ans.add(expStaticByYearVO);
        }
        return ApiResponse.success(ans);
    }

    /**
     * 按月度统计支出
     */
    @PostMapping("/statisticsByMonth")
    public ApiResponse<Object> statisticsByMonth() {
        int userId = StpUtil.getLoginIdAsInt();
        List<ExpStaByYearVO> list = expenseMapper.statisticsByMonth(userId);
        List<ExpStaticByYearVO> ans = new ArrayList<>();

        // 获取收入类型字典数据
        List<SysDictDataEntity> dictDataList = sysDictService.getDictDataByDictType("exp_type");
        Map<Integer, String> dictMap = dictDataList.stream()
                .collect(Collectors.toMap(SysDictDataEntity::getDictCode, SysDictDataEntity::getDictLabel));

        // 按照年月汇总
        Map<String, List<ExpStaByYearVO>> collect = list.stream()
                .collect(Collectors.groupingBy(item -> item.getYear() + "-" + String.format("%02d", item.getMonth())));
        for (Map.Entry<String, List<ExpStaByYearVO>> entry : collect.entrySet()) {
            ExpStaticByYearVO expStaticByYearVO = new ExpStaticByYearVO();
            String[] yearMonth = entry.getKey().split("-");
            expStaticByYearVO.setYear(Integer.parseInt(yearMonth[0]));
            expStaticByYearVO.setMonth(Integer.parseInt(yearMonth[1]));
            List<ExpStaByYearVO> value = entry.getValue();
            value.forEach(item -> {
                item.setTypeName(dictMap.get(item.getTypeId()));
            });
            expStaticByYearVO.setDetail(value);
            ans.add(expStaticByYearVO);
        }
        return ApiResponse.success(ans);
    }
}
