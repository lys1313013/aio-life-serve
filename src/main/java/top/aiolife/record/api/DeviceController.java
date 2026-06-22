package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.core.util.SysUtil;
import top.aiolife.record.mapper.IDeviceMapper;
import top.aiolife.record.pojo.entity.DeviceEntity;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.core.util.MinioUtil;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/04 19:22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/device")
public class DeviceController {
    private IDeviceMapper eleDeviceMapper;
    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;

    public IDeviceMapper getBaseMapper() {
        return eleDeviceMapper;
    }

    @PostMapping("/query")
    public ApiResponse<PageResp<DeviceEntity>> query(
            @RequestBody CommonQuery<DeviceEntity> query) {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<DeviceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceEntity::getUserId, userId);
        DeviceEntity condition = query.getCondition();
        lambdaQueryWrapper.eq(SysUtil.isNotEmpty(condition.getType()), DeviceEntity::getType,
                condition.getType());
        lambdaQueryWrapper.orderByDesc(DeviceEntity::getPurchaseDate);        // 分页
        Page<DeviceEntity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<DeviceEntity> iPage = getBaseMapper().selectPage(page, lambdaQueryWrapper);
        PageResp<DeviceEntity> objectPageResp = PageResp.of(iPage.getRecords(), iPage.getTotal());
        return ApiResponse.success(objectPageResp);
    }

    /**
     * 插入或更新
     *
     * @param entity
     */
    @PostMapping("/insertOrUpdate")
    public ApiResponse<Boolean> insertOrUpdate(@RequestBody DeviceEntity entity) {
        // 获取token
        entity.setUserId(StpUtil.getLoginIdAsLong());

        return ApiResponse.success(getBaseMapper().insertOrUpdate(entity));
    }

    /**
     * 上传设备图片
     */
    @PostMapping("/upload-image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioUtil.generateUniqueFileName(file.getOriginalFilename());
            String objectName = StpUtil.getLoginIdAsLong() + "/device/" + fileName;
            String bucketName = StringUtils.hasText(minioConfig.getBucketName()) ? minioConfig.getBucketName() : "aiolife";
            minioUtil.uploadFile(bucketName, file, objectName);
            String imageUrl = minioUtil.getPreviewUrl(bucketName, objectName);
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Integer id) {
        LambdaQueryWrapper<DeviceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceEntity::getId, id);
        wrapper.eq(DeviceEntity::getUserId, StpUtil.getLoginIdAsLong());
        return ApiResponse.success(getBaseMapper().delete(wrapper) > 0);
    }
}
