package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.TaskDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-07 23:23
 */
public interface ITaskDetail extends IService<TaskDetailEntity> {

    /**
     * 获取未完成任务数
     *
     * @param taskIdList 任务id列表，为空则直接返回空结果
     * @param userId 用户id
     * @return 任务id与未完成数的映射
     */
    Map<Long, Integer> getUnCompletedCount(List<Long> taskIdList, Long userId);
}
