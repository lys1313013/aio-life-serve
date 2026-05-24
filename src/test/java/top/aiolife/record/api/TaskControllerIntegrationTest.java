package top.aiolife.record.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.aiolife.record.mapper.ITaskMapper;
import top.aiolife.record.pojo.entity.TaskEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskController 集成测试
 * 用于验证 SQL 脚本执行是否正确
 *
 * @author Lys
 * @date 2026/05/23
 */
class TaskControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskController taskController;

    @Autowired
    private ITaskMapper taskMapper;

    @Test
    void testQuery_查询任务列表() {
        Long taskId = System.currentTimeMillis() % 1000000 + 900000L;
        taskMapper.insert(createTask(taskId));

        var response = taskController.query(null, 1, 10);
        assertSuccess(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().getTotal() >= 1);
    }

    @Test
    void testQueryById_根据ID查询任务() {
        Long taskId = System.currentTimeMillis() % 1000000 + 900000L;
        taskMapper.insert(createTask(taskId));

        var response = taskController.query(taskId, 1, 1);
        assertSuccess(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().getTotal() >= 1);
    }

    private TaskEntity createTask(Long taskId) {
        TaskEntity entity = new TaskEntity();
        entity.setId(taskId);
        entity.setUserId(TEST_USER_ID);
        entity.setContent("测试任务");
        entity.setColumnId(1L);
        entity.setSortOrder(1);
        entity.setIsDeleted(0);
        return entity;
    }
}
