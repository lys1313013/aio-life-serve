package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.ITaskColumnMapper;
import com.lys.record.pojo.entity.TaskColumnEntity;
import com.lys.record.service.ITaskColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 20:19
 */
@Service
public class TaskColumnServiceImpl extends ServiceImpl<ITaskColumnMapper, TaskColumnEntity> implements ITaskColumnService {

    @Override
    public void updateBatchById(List<TaskColumnEntity> list) {
        super.updateBatchById(list);
    }
}
