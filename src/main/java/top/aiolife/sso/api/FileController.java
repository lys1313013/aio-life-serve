package top.aiolife.sso.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.aiolife.core.util.MinioUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
     * @param fileName 文件名（包含路径）
     */
    @GetMapping("/file/preview")
    public void preview(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        if (!StringUtils.hasText(fileName)) {
            return;
        }

        try (InputStream inputStream = minioUtil.getFile(fileName);
             OutputStream outputStream = response.getOutputStream()) {
            
            // 设置响应头
            // response.setContentType("image/jpeg"); // 默认为图片，也可以根据文件名后缀判断
            
            // 根据文件后缀名动态设置Content-Type
            String contentType = "application/octet-stream"; // 默认类型
            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.toLowerCase().endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (fileName.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }
            response.setContentType(contentType);
            
            // 如果是下载，可以设置 Content-Disposition
            // response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), StandardCharsets.UTF_8));

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("获取文件失败: {}", fileName, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
