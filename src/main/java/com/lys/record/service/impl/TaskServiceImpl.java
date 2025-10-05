package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.ITaskMapper;
import com.lys.record.pojo.entity.TaskEntity;
import com.lys.record.service.ITaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 21:01
 */
@Service
public class TaskServiceImpl extends ServiceImpl<ITaskMapper, TaskEntity> implements ITaskService {

    @Override
    public void updateBatchById(List<TaskEntity> list) {
        super.saveBatch(list);
    }
}
