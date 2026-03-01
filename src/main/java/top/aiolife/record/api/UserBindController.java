package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.record.pojo.entity.UserBindEntity;
import top.aiolife.record.service.IUserBindService;

import java.util.List;

/**
 * 用户第三方账号绑定接口
 */
@RestController
@RequestMapping("/userbinds")
public class UserBindController {

    @Autowired
    private IUserBindService userBindService;

    /**
     * 获取当前用户的绑定列表
     */
    @GetMapping("/list")
    public ApiResponse<List<UserBindEntity>> list(@RequestParam(required = false) Boolean includeToken) {
        long userId = StpUtil.getLoginIdAsLong();
        List<UserBindEntity> list = userBindService.getBindsByUserId(userId);
        list.forEach(item -> {
            // 如果不要求包含Token，则隐藏Token
            if (includeToken == null || !includeToken) {
                item.setAccessToken(null);
            }
        });
        return ApiResponse.success(list);
    }

    /**
     * 新增绑定
     */
    @PostMapping
    public ApiResponse<Boolean> add(@RequestBody UserBindEntity userBindEntity) {
        long userId = StpUtil.getLoginIdAsLong();
        userBindEntity.setUserId(userId);

        // 检查是否存在同平台绑定
        UserBindEntity exist = userBindService.getBindByUserIdAndPlatform(userId, userBindEntity.getPlatform());
        if (exist != null) {
            return ApiResponse.error("该平台账号已绑定，请勿重复添加");
        }

        userBindEntity.fillCreateCommonField(userId);
        return ApiResponse.success(userBindService.save(userBindEntity));
    }

    /**
     * 更新绑定
     */
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody UserBindEntity userBindEntity) {
        long userId = StpUtil.getLoginIdAsLong();
        userBindEntity.setUserId(userId);

        // 检查记录是否存在
        if (userBindEntity.getId() == null) {
            return ApiResponse.error("ID不能为空");
        }
        UserBindEntity exist = userBindService.getById(userBindEntity.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return ApiResponse.error("记录不存在或无权操作");
        }

        userBindEntity.fillUpdateCommonField(userId);
        
        // 如果Access Token为空，则保留原值（不更新）
        if (userBindEntity.getAccessToken() == null || userBindEntity.getAccessToken().isEmpty()) {
            userBindEntity.setAccessToken(exist.getAccessToken());
        }
        
        return ApiResponse.success(userBindService.updateById(userBindEntity));
    }

    /**
     * 删除绑定
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        UserBindEntity entity = userBindService.getById(id);
        if (entity == null || !entity.getUserId().equals(userId)) {
            return ApiResponse.error("无权操作或记录不存在");
        }
        return ApiResponse.success(userBindService.removeById(id));
    }
}
