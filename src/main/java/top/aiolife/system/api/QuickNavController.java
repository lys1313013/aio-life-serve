package top.aiolife.system.api;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.sso.mapper.UserMapper;
import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.system.pojo.req.QuickNavSaveReq;
import top.aiolife.system.pojo.vo.QuickNavCandidateVO;
import top.aiolife.system.pojo.vo.QuickNavItemVO;
import top.aiolife.system.service.IQuickNavService;

import java.util.List;

/**
 * 用户首页快捷导航控制器
 *
 * @author Ethan
 * @date 2026/06/05
 */
@RestController
@RequestMapping("/quick-nav")
@RequiredArgsConstructor
public class QuickNavController {

    private final IQuickNavService quickNavService;

    private final UserMapper userMapper;

    /**
     * 获取当前用户可访问、且适合作为快捷入口的菜单叶子列表。
     */
    @GetMapping("/candidates")
    public ApiResponse<List<QuickNavCandidateVO>> candidates() {
        return ApiResponse.success(quickNavService.listCandidates(currentRoles()));
    }

    /**
     * 获取当前用户已保存的快捷导航布局。
     *
     * <p>未保存任何项时返回空数组（前端展示空态）。</p>
     */
    @GetMapping("/my")
    public ApiResponse<List<QuickNavItemVO>> my() {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(quickNavService.listMy(userId));
    }

    /**
     * 整块覆盖保存（可传空数组 = 清空全部）。
     */
    @PostMapping("/my")
    public ApiResponse<List<QuickNavItemVO>> save(@RequestBody QuickNavSaveReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(quickNavService.saveMy(userId, currentRoles(), req));
    }

    private List<String> currentRoles() {
        long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userMapper.selectById(userId);
        String role = user == null ? null : user.getRole();
        if (!StringUtils.hasText(role)) {
            return List.of("user");
        }
        return List.of(role.split(",")).stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }
}
