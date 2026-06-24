package top.aiolife.record.api;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestTokenController {
    @GetMapping("/test-token")
    public String testToken(HttpServletRequest request) {
        String cookiesStr = "";
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                cookiesStr += c.getName() + "=" + c.getValue() + "; ";
            }
        }
        return "cookies: " + cookiesStr + " | isLogin: " + StpUtil.isLogin();
    }
}
