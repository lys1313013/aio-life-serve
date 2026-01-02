package com.lys.record.service;

import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.sso.pojo.entity.UserEntity;
import jakarta.mail.MessagingException;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/10 00:05
 */
public interface ILeetcodeService {

    /**
     * 检查当天是否有提交，没有提交就发邮件提醒
     *
     * @author Lys
     * @date 2025/4/12 00:55
     */
    void check();

    /**
     * 检查是否完成每日一题
     *
     * @param userEntity
     */
    boolean checkToday(UserEntity userEntity, boolean sendEmail);

    /**
     * 获取leetcode看板卡片数据
     *
     * @param userId 用户ID
     */
    List<DashboardCardVO> getDashboardCard(int userId);

    /**
     * 获取今天提交次数
     *
     * @param leetcodeAcct leetcode账号
     * @return 今日提交次数
     */
    Integer getTodaySubmissionCount(String leetcodeAcct);

    /**
     * 邮件通知今天每日一题
     */
    void notifyTodayQuestion() throws MessagingException;
}
