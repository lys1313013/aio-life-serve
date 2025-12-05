package com.lys.sso.service;

import com.lys.sso.pojo.req.LoginReq;
import com.lys.sso.pojo.vo.UserInfoVO;
import com.lys.sso.pojo.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 *
 * @author Lys
 * @date 2025/12/06 23:58
 */
public interface IUserService {

    /**
     * 登录
     *
     * @param loginReq 登录参数
     * @param ip       客户端IP
     * @return 登录结果
     */
    UserLoginVO login(LoginReq loginReq, String ip);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(int userId);
}
