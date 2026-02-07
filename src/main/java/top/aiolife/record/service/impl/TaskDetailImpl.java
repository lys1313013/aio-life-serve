package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.aiolife.record.mapper.ITaskDetailMapper;
import top.aiolife.record.pojo.entity.TaskDetailEntity;
import top.aiolife.record.service.ITaskDetail;

import top.aiolife.core.util.SysUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-07 23:24
 */
@Service
public class TaskDetailImpl extends ServiceImpl<ITaskDetailMapper, TaskDetailEntity> implements ITaskDetail {


    @Override
    public Map<Long, Integer> getUnCompletedCount(List<Long> taskIdList, Long userId) {
        if (SysUtil.isEmpty(taskIdList)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<TaskDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskDetailEntity::getUserId, userId);
        queryWrapper.in(TaskDetailEntity::getTaskId, taskIdList);
        queryWrapper.eq(TaskDetailEntity::getIsCompleted, 0);
        return list(queryWrapper).stream()
                .collect(Collectors.groupingBy(TaskDetailEntity::getTaskId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }
}
