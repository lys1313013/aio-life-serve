package top.aiolife.record.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.aiolife.record.pojo.entity.TaskDetailEntity;
import top.aiolife.record.pojo.entity.TaskEntity;
import top.aiolife.record.service.ITaskDetail;
import top.aiolife.record.service.ITaskService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskDetailController 集成测试
 * 用于验证 SQL 脚本执行是否正确
 *
 * @author Lys
 * @date 2026/05/23
 */
class TaskDetailControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskDetailController taskDetailController;

    @Autowired
    private ITaskDetail taskDetailService;

    @Autowired
    private ITaskService taskService;

    @Test
    void testGetWatched_获取关注的明细() {
        var response = taskDetailController.getWatched();
        assertSuccess(response);
        assertNotNull(response.getData());
    }
}
