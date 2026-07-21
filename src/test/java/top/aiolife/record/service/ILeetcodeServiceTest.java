package top.aiolife.record.service;

import top.aiolife.sso.pojo.entity.UserEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-01-11 12:25
 */
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-01-11 12:25
 */
@Tag("external")
@SpringBootTest
class ILeetcodeServiceTest {

    @Autowired
    private ILeetcodeService leetcodeService;

    @Test
    void checkToday() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("xxx@gmail.com");
        leetcodeService.checkToday(userEntity, true);
    }
}
