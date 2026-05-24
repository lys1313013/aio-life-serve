package top.aiolife.record.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.aiolife.record.service.IMbtiResultService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MbtiController 集成测试
 * 用于验证 SQL 脚本执行是否正确
 *
 * @author Lys
 * @date 2026/05/23
 */
class MbtiControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MbtiController mbtiController;

    @Autowired
    private IMbtiResultService mbtiResultService;

    @Test
    void testGetHistory_获取历史记录() {
        var response = mbtiController.getHistory();
        assertSuccess(response);
        assertNotNull(response.getData());
    }
}
