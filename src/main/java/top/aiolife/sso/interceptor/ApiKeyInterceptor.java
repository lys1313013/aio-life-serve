package top.aiolife.sso.interceptor;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.aiolife.sso.pojo.entity.ApiKeyEntity;
import top.aiolife.sso.service.IApiKeyLogService;
import top.aiolife.sso.service.IApiKeyService;

import java.time.LocalDateTime;

/**
 * API Key 认证拦截器
 *
 * @author Lys
 * @date 2026/03/09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final IApiKeyService apiKeyService;
    private final IApiKeyLogService apiKeyLogService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 1. 获取 Authorization 头
        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isBlank(authHeader)) {
            return true;
        }

        // 2. 检查是否为 API Key 格式 (ak- 开头)
        String apiKeyStr = authHeader.replace("Bearer ", "").trim();
        if (!apiKeyStr.startsWith("ak-")) {
            return true;
        }

        // 3. 校验 API Key
        ApiKeyEntity apiKeyEntity = apiKeyService.getByApiKey(apiKeyStr);
        if (apiKeyEntity == null || apiKeyEntity.getIsDeleted() == 1) {
            log.warn("API Key {} 不存在", apiKeyStr);
            throw new NotLoginException("API Key 无效", "API_KEY", apiKeyStr);
        }

        // 4. 检查是否过期
        if (apiKeyEntity.getExpiredAt() != null && apiKeyEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            log.warn("API Key {} 已过期", apiKeyStr);
            throw new NotLoginException("API Key 已过期", "API_KEY", apiKeyStr);
        }

        // 5. 自动登录 (仅限本次请求上下文)
        StpUtil.login(apiKeyEntity.getUserId());
        
        // 6. 将认证信息存入 SaStorage，以便后续 SaInterceptor 跳过校验
        SaHolder.getStorage().set("API_KEY_ID", apiKeyEntity.getId());
        SaHolder.getStorage().set("IS_API_KEY_AUTH", true);
        
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        Boolean isApiKeyAuth = (Boolean) SaHolder.getStorage().get("IS_API_KEY_AUTH");
        if (Boolean.TRUE.equals(isApiKeyAuth)) {
            Long apiKeyId = (Long) SaHolder.getStorage().get("API_KEY_ID");
            // 记录调用日志
            apiKeyLogService.log(
                    apiKeyId,
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus(),
                    getIpAddress(request)
            );
            // 本次请求结束，注销登录，保持会话清洁
            StpUtil.logout();
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
