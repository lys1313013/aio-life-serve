package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.IExerciseRecordMapper;
import com.lys.record.pojo.entity.ExerciseRecordEntity;
import com.lys.record.service.IExerciseRecordService;
import org.springframework.stereotype.Service;

/**
 * 运动记录Service实现类
 *
 * @author Lys
 * @date 2025-11-29 18:40
 */
@Service
public class ExerciseRecordServiceImpl extends ServiceImpl<IExerciseRecordMapper, ExerciseRecordEntity> implements IExerciseRecordService {
}