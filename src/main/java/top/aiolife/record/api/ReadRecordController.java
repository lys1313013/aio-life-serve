package top.aiolife.record.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.record.pojo.query.ReadRecordQuery;
import top.aiolife.record.pojo.req.ReadRecordReq;
import top.aiolife.record.pojo.vo.ReadRecordVO;
import top.aiolife.record.service.IReadRecordService;
import top.aiolife.record.pojo.entity.FileEntity;
import top.aiolife.record.pojo.vo.FileVO;
import top.aiolife.record.service.IFileService;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/read-record")
@RequiredArgsConstructor
@SaCheckLogin
public class ReadRecordController {

    private final IReadRecordService readRecordService;
    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;

    private final IFileService fileService;

    @PostMapping("/page")
    public ApiResponse<Page<ReadRecordVO>> pageList(@RequestBody ReadRecordQuery query) {
        return ApiResponse.success(readRecordService.pageList(query));
    }

    @PostMapping
    public ApiResponse<Void> save(@RequestBody ReadRecordReq req) {
        readRecordService.saveRecord(req);
        return ApiResponse.success();
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody ReadRecordReq req) {
        readRecordService.updateRecord(req);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        readRecordService.deleteRecord(id);
        return ApiResponse.success();
    }

    @GetMapping("/parse-douban")
    public ApiResponse<ReadRecordReq> parseDouban(@RequestParam String url) {
        return ApiResponse.success(readRecordService.parseDouban(url));
    }

    @GetMapping("/active")
    public ApiResponse<java.util.List<ReadRecordVO>> listActive() {
        return ApiResponse.success(readRecordService.listActive());
    }

    @PostMapping("/upload-cover")
    public ApiResponse<FileVO> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String extension = fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
            String objectName = StpUtil.getLoginIdAsLong() + "/read/" + java.util.UUID.randomUUID().toString() + extension;
            String bucketName = StringUtils.hasText(minioConfig.getBucketName()) ? minioConfig.getBucketName() : "aiolife";
            
            FileEntity fileEntity = fileService.uploadAndSave(file, "read", bucketName, objectName, 0);
            return ApiResponse.success(fileService.toVO(fileEntity));
        } catch (Exception e) {
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "上传失败: " + e.getMessage());
        }
    }
}
