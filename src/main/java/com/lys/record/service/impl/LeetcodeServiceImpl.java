package com.lys.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.LeetcodeCalendarMapper;
import com.lys.record.pojo.entity.LeetcodeCalendarEntity;
import com.lys.record.service.ILeetcodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/10 00:06
 */
@Service
@AllArgsConstructor
public class LeetcodeServiceImpl extends ServiceImpl<LeetcodeCalendarMapper, LeetcodeCalendarEntity> implements ILeetcodeService {

    private LeetcodeCalendarMapper leetcodeCalendarMapper;

    @Override
    public int delete(Integer userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<LeetcodeCalendarEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeetcodeCalendarEntity::getUserId, userId)
                .ge(LeetcodeCalendarEntity::getSubmitDate, startDate)
                .le(LeetcodeCalendarEntity::getSubmitDate, endDate);
       return leetcodeCalendarMapper.delete(queryWrapper);
    }


    @Override
    public boolean batchInsert(List<LeetcodeCalendarEntity> leetcodeList) {
        return this.saveOrUpdateBatch(leetcodeList);
    }
}
