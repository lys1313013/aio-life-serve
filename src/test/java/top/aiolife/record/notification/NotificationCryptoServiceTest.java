package top.aiolife.record.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationCryptoServiceTest {

    @Test
    void encrypt_随机Iv且可正确解密() {
        NotificationCryptoService service =
                new NotificationCryptoService("test-notification-key-123456");

        String first = service.encrypt("secret-value");
        String second = service.encrypt("secret-value");

        assertNotEquals(first, second);
        assertEquals("secret-value", service.decrypt(first));
        assertEquals("secret-value", service.decrypt(second));
    }

    @Test
    void encrypt_未配置主密钥时拒绝加密() {
        NotificationCryptoService service = new NotificationCryptoService("");

        assertThrows(IllegalStateException.class, () -> service.encrypt("secret-value"));
    }

    @Test
    void decrypt_密文被篡改时拒绝解密() {
        NotificationCryptoService service =
                new NotificationCryptoService("test-notification-key-123456");
        String ciphertext = service.encrypt("secret-value");
        String tampered = ciphertext.substring(0, ciphertext.length() - 2) + "AA";

        assertThrows(IllegalStateException.class, () -> service.decrypt(tampered));
    }
}
