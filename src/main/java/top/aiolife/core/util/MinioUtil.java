package top.aiolife.core.util;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.config.MinioConfig;

import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO 工具类
 */
@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 上传文件到 MinIO
     * @param file 上传的文件
     * @param objectName 文件对象名
     * @return 文件访问 URL
     */
    public String uploadFile(MultipartFile file, String objectName) throws Exception {
        // 确保桶存在
        ensureBucketExists();

        // 获取文件输入流
        try (InputStream inputStream = file.getInputStream()) {
            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回文件访问 URL
            return objectName;
        }
    }

    /**
     * 获取文件流
     * @param objectName 文件对象名
     * @return 文件输入流
     */
    public InputStream getFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 生成唯一的文件名
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 确保桶存在，如果不存在则创建
     */
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );
            log.info("创建 MinIO 桶: {}", minioConfig.getBucketName());
        }
    }
}
