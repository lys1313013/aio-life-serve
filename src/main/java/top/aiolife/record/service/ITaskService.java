package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.TaskEntity;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 21:01
 */
public interface ITaskService extends IService<TaskEntity> {
    void updateBatchById(List<TaskEntity> list);
}
