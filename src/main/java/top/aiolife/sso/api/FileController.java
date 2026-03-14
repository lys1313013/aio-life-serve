package top.aiolife.sso.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.aiolife.core.util.MinioUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件控制器
 *
 * @author Lys
 * @date 2025/4/5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final MinioUtil minioUtil;

    /**
     * 预览/下载文件
     *
     * @param fileName 文件名（包含桶名和路径）
     */
    @GetMapping("/file/preview/{*fileName}")
    public void preview(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 去除前导 /
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        // 截取第一个 / 之前的内容作为桶名
        int splitIndex = fileName.indexOf("/");
        if (splitIndex == -1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String bucketName = fileName.substring(0, splitIndex);
        String objectName = fileName.substring(splitIndex + 1);

        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(objectName)) {
            return;
        }

        try (InputStream inputStream = minioUtil.getFile(bucketName, objectName);
             OutputStream outputStream = response.getOutputStream()) {
            
            // 根据文件后缀名动态设置Content-Type
            String contentType = "application/octet-stream"; // 默认类型
            if (objectName.toLowerCase().endsWith(".jpg") || objectName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (objectName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (objectName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (objectName.toLowerCase().endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (objectName.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }
            response.setContentType(contentType);
            
            // 如果是下载，可以设置 Content-Disposition
            // response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(objectName.substring(objectName.lastIndexOf("/") + 1), StandardCharsets.UTF_8));

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("获取文件失败: bucket={}, objectName={}", bucketName, objectName, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
