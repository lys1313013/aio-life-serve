package top.aiolife.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.aiolife.sso.interceptor.ApiKeyInterceptor;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor apiKeyInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // API Key 拦截器，需在 Sa-Token 拦截器之前执行
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/auth/sendEmailCode", "/auth/sendResetPasswordCode",
                        "/auth/resetPassword",
                        "/actuator/**");

        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            // CORS 预检请求直接放行
            if (RequestMethod.OPTIONS.name().equalsIgnoreCase(SaHolder.getRequest().getMethod())) {
                return;
            }
            // 如果已经通过 API Key 认证了，就不要再 checkLogin 了（实现 API Key 或 Token 二选一）
            if (Boolean.TRUE.equals(SaHolder.getStorage().get("IS_API_KEY_AUTH"))) {
                return;
            }
            StpUtil.checkLogin();
        })).addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/auth/sendEmailCode", "/auth/sendResetPasswordCode",
                        "/auth/resetPassword",
                        "/actuator/**",
                        "/file/preview/**");
    }
}
