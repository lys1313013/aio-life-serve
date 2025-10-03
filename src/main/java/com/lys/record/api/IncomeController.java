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
import com.lys.record.pojo.entity.SysDictDataEntity;
import com.lys.record.pojo.vo.IncStaByYearVO;
import com.lys.record.pojo.vo.IncStaticByYearVO;
import com.lys.record.service.ISysDictService;
import lombok.AllArgsConstructor;
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
 * @date 2025/09/14 21:09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/income")
public class IncomeController {

    private IIncomeMapper incomeMapper;

    private ISysDictService sysDictService;

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

    @PostMapping("/static")
    public ApiResponse<Object> staticData() {
        int userId = StpUtil.getLoginIdAsInt();
        List<IncStaByYearVO> list = incomeMapper.list(userId);
        List<IncStaticByYearVO> ans = new ArrayList<>();
        
        // 获取收入类型字典数据
        List<SysDictDataEntity> dictDataList = sysDictService.getDictDataByDictType("income_type");
        Map<Integer, String> dictMap = dictDataList.stream()
                .collect(Collectors.toMap(SysDictDataEntity::getDictCode, SysDictDataEntity::getDictLabel));

        // 按照年度汇总
        Map<Integer, List<IncStaByYearVO>> collect = list.stream()
                .collect(Collectors.groupingBy(IncStaByYearVO::getYear));
        for (Map.Entry<Integer, List<IncStaByYearVO>> entry : collect.entrySet()) {
            IncStaticByYearVO incStaticByYearVO = new IncStaticByYearVO();
            incStaticByYearVO.setYear(entry.getKey());
            List<IncStaByYearVO> value = entry.getValue();
            value.forEach(item -> {
                item.setIncTypeName(dictMap.get(item.getIncTypeId()));
            });
            incStaticByYearVO.setDetail(value);
            ans.add(incStaticByYearVO);
        }
        return ApiResponse.success(ans);
    }
}