package top.aiolife.record.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class NotificationCryptoService {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final String PREFIX = "v1:";

    private final SecureRandom secureRandom = new SecureRandom();
    private final String encryptionKey;

    public NotificationCryptoService(
            @Value("${aio.life.server.notification.credential-encryption-key:}") String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        ensureConfigured();
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec(), new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] result = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();
            return PREFIX + Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new IllegalStateException("通知密钥加密失败", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (!StringUtils.hasText(ciphertext) || !ciphertext.startsWith(PREFIX)) {
            throw new IllegalArgumentException("通知密文格式无效");
        }
        ensureConfigured();
        try {
            byte[] bytes = Base64.getDecoder().decode(ciphertext.substring(PREFIX.length()));
            if (bytes.length <= IV_LENGTH) {
                throw new IllegalArgumentException("通知密文格式无效");
            }
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[bytes.length - IV_LENGTH];
            System.arraycopy(bytes, 0, iv, 0, IV_LENGTH);
            System.arraycopy(bytes, IV_LENGTH, encrypted, 0, encrypted.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec(), new GCMParameterSpec(TAG_LENGTH, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("通知密钥解密失败，请检查环境变量", e);
        }
    }

    private SecretKeySpec keySpec() throws Exception {
        byte[] key = MessageDigest.getInstance("SHA-256")
                .digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(encryptionKey) || encryptionKey.length() < 16) {
            throw new IllegalStateException(
                    "请配置长度不少于 16 位的 AIO_LIFE_NOTIFICATION_ENCRYPTION_KEY");
        }
    }
}
