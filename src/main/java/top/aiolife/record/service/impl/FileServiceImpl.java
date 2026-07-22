package top.aiolife.record.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.record.enums.FileBizType;
import top.aiolife.record.mapper.IFileMapper;
import top.aiolife.record.pojo.entity.FileEntity;
import top.aiolife.record.pojo.vo.FileVO;
import top.aiolife.record.service.IFileService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<IFileMapper, FileEntity> implements IFileService {

    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;

    public FileServiceImpl(MinioUtil minioUtil, MinioConfig minioConfig) {
        this.minioUtil = minioUtil;
        this.minioConfig = minioConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO upload(MultipartFile file, FileBizType bizType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        long userId = StpUtil.getLoginIdAsLong();
        String bucketName = resolveBucketName();
        String objectName = buildObjectName(userId, bizType, file.getOriginalFilename());

        try {
            minioUtil.uploadFile(bucketName, file, objectName);
            registerRollbackCleanup(bucketName, objectName);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(objectName);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setBizType(bizType.getBizType());
            fileEntity.setIsPublic(bizType.getVisibility().getValue());
            fileEntity.setHashValue("");
            fileEntity.fillCreateCommonField(userId);

            if (!this.save(fileEntity)) {
                throw new IllegalStateException("文件记录保存失败");
            }
            return toVO(fileEntity);
        } catch (Exception e) {
            throw new IllegalStateException("上传失败: " + e.getMessage(), e);
        }
    }

    private String resolveBucketName() {
        return StringUtils.hasText(minioConfig.getBucketName()) ? minioConfig.getBucketName() : "aiolife";
    }

    private String buildObjectName(long userId, FileBizType bizType, String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String suffix = StringUtils.hasText(extension) && extension.matches("[A-Za-z0-9]{1,10}")
                ? "." + extension.toLowerCase(Locale.ROOT)
                : "";
        return userId + "/" + bizType.getDirectory() + "/" + UUID.randomUUID() + suffix;
    }

    private void registerRollbackCleanup(String bucketName, String objectName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_ROLLED_BACK) {
                    return;
                }
                try {
                    minioUtil.removeObject(bucketName, objectName);
                } catch (Exception cleanupException) {
                    log.error("回滚后清理 MinIO 文件失败: bucket={}, objectName={}", bucketName, objectName, cleanupException);
                }
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindBizId(List<String> fileIds, String bizType, Long bizId) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        // 只允许绑定当前用户自己上传的文件
        long userId = StpUtil.getLoginIdAsLong();
        LambdaUpdateWrapper<FileEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FileEntity::getId, fileIds)
                .eq(FileEntity::getCreateUser, userId)
                .set(FileEntity::getBizId, bizId)
                .set(FileEntity::getBizType, bizType);
        this.update(updateWrapper);
    }

    @Override
    public List<FileVO> getByBiz(String bizType, Long bizId) {
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getBizType, bizType)
                .eq(FileEntity::getBizId, bizId)
                .eq(FileEntity::getIsDeleted, 0);
        List<FileEntity> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public FileVO toVO(FileEntity entity) {
        if (entity == null) {
            return null;
        }
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setId(entity.getId());
        // 构造文件预览 URL
        String bucketName = resolveBucketName();
        vo.setFileUrl(minioUtil.getPreviewUrl(bucketName, entity.getFileName()));
        return vo;
    }
}
