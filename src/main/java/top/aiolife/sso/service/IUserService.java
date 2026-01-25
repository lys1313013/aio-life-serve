package top.aiolife.sso.service;

import top.aiolife.sso.pojo.entity.UserEntity;
import top.aiolife.sso.pojo.req.LoginReq;
import top.aiolife.sso.pojo.vo.UserInfoVO;
import top.aiolife.sso.pojo.vo.UserLoginVO;

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

    void updateUser(UserEntity userEntity);
}
