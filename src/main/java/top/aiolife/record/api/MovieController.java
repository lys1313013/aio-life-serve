package top.aiolife.record.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.record.pojo.query.MovieQuery;
import top.aiolife.record.pojo.req.MovieReq;
import top.aiolife.record.pojo.vo.MovieVO;
import top.aiolife.record.service.IMovieService;

/**
 * 影视记录控制器
 *
 * @author Trae
 * @date 2026/06/18
 */
@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
@SaCheckLogin
public class MovieController {

    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;
    private final IMovieService movieService;

    @PostMapping("/page")
    public ApiResponse<Page<MovieVO>> pageList(@RequestBody MovieQuery query) {
        return ApiResponse.success(movieService.pageList(query));
    }

    @PostMapping
    public ApiResponse<Void> save(@RequestBody MovieReq req) {
        movieService.saveRecord(req);
        return ApiResponse.success();
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody MovieReq req) {
        movieService.updateRecord(req);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        movieService.deleteRecord(id);
        return ApiResponse.success();
    }

    @GetMapping("/parse-douban")
    public ApiResponse<MovieReq> parseDouban(@RequestParam String url) {
        return ApiResponse.success(movieService.parseDouban(url));
    }

    /**
     * 上传影视封面图
     *
     * @param file 封面图片文件
     * @return 封面图的预览链接
     */
    @PostMapping("/upload-cover")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioUtil.generateUniqueFileName(file.getOriginalFilename());
            String objectName = StpUtil.getLoginIdAsLong() + "/movie/" + fileName;
            
            String bucketName = StringUtils.hasText(minioConfig.getBucketName()) ? minioConfig.getBucketName() : "aiolife";
            
            minioUtil.uploadFile(bucketName, file, objectName);
            
            String imageUrl = minioUtil.getPreviewUrl(bucketName, objectName);
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "上传失败: " + e.getMessage());
        }
    }
}
