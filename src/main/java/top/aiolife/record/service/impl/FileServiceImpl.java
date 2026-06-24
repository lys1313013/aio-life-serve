package top.aiolife.record.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.record.mapper.IFileMapper;
import top.aiolife.record.pojo.entity.FileEntity;
import top.aiolife.record.pojo.vo.FileVO;
import top.aiolife.record.service.IFileService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl extends ServiceImpl<IFileMapper, FileEntity> implements IFileService {

    private final MinioUtil minioUtil;

    @Value("${aio.life.serve.base-url}")
    private String serveBaseUrl;

    public FileServiceImpl(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileEntity uploadAndSave(MultipartFile file, String bizType, String bucketName, String objectName, Integer isPublic) throws Exception {
        // 上传到 MinIO
        minioUtil.uploadFile(bucketName, file, objectName);
        
        // 组装并保存文件记录
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(objectName); // 因为 fileUrl 已经废弃，统一将 MinIO 的 objectName（或外部链接）存入 fileName
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setBizType(bizType);
        fileEntity.setIsPublic(isPublic != null ? isPublic : 0);
        
        // 计算哈希值可在此处进行，简单起见暂略或由前端传，如果需要可以读取流计算
        fileEntity.setHashValue(""); 

        // 设置通用字段
        long userId = 0L;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }
        fileEntity.fillCreateCommonField(userId);

        this.save(fileEntity);
        
        return fileEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindBizId(List<String> fileIds, String bizType, Long bizId) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        LambdaUpdateWrapper<FileEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FileEntity::getId, fileIds)
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
        return vo;
    }
}