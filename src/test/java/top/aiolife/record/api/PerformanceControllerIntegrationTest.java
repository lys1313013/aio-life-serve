package top.aiolife.record.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.record.mapper.IPerformanceMapper;
import top.aiolife.record.pojo.entity.PerformanceEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PerformanceController 集成测试
 * 用于验证 SQL 脚本执行是否正确
 *
 * @author Lys
 * @date 2026/05/23
 */
class PerformanceControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PerformanceController performanceController;

    @Autowired
    private IPerformanceMapper performanceMapper;

    @Test
    void testQuery_查询绩效记录() {
        Long perfId = System.currentTimeMillis() % 1000000 + 900000L;
        performanceMapper.insert(createPerformance(perfId));

        CommonQuery<PerformanceEntity> query = new CommonQuery<>();
        query.setPage(1);
        query.setPageSize(10);
        query.setCondition(new PerformanceEntity());

        var response = performanceController.query(query);
        assertSuccess(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().getTotal() >= 1);
    }

    private PerformanceEntity createPerformance(Long perfId) {
        PerformanceEntity entity = new PerformanceEntity();
        entity.setId(perfId);
        entity.setCreateBy(TEST_USER_ID);
        entity.setUpdateBy(TEST_USER_ID);
        entity.setPerformanceName("测试演出");
        entity.setPerformer("测试演员");
        entity.setPerformanceType("演唱会");
        entity.setPerformanceDate(java.time.LocalDate.now());
        entity.setCity("北京");
        entity.setVenue("国家大剧院");
        entity.setTicketPrice(java.math.BigDecimal.valueOf(100));
        return entity;
    }
}
