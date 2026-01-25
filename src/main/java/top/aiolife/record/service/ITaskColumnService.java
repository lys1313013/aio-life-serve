package top.aiolife.record.service;

import top.aiolife.record.pojo.entity.TaskColumnEntity;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 20:19
 */
public interface ITaskColumnService {
    void updateBatchById(List<TaskColumnEntity> list);
}
