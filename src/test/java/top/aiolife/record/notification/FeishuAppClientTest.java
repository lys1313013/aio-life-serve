package top.aiolife.record.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeishuAppClientTest {

    private final FeishuAppClient client = new FeishuAppClient(new ObjectMapper());

    @Test
    void validateAppId_接受标准AppId() {
        assertDoesNotThrow(() -> client.validateAppId("cli_example123"));
    }

    @Test
    void validateAppId_拒绝非法AppId() {
        assertThrows(IllegalArgumentException.class, () -> client.validateAppId("example123"));
    }

    @Test
    void validateOpenId_接受标准OpenId() {
        assertDoesNotThrow(() -> client.validateOpenId("ou_example1234567890"));
    }

    @Test
    void validateOpenId_拒绝空值() {
        assertThrows(IllegalArgumentException.class, () -> client.validateOpenId(""));
    }

    @Test
    void validateOpenId_拒绝非OpenId标识() {
        assertThrows(IllegalArgumentException.class, () -> client.validateOpenId("user@example.com"));
    }
}
