package com.lys.record.service;

import com.lys.record.pojo.entity.LeetcodeCalendarEntity;
import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.sso.pojo.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/10 00:05
 */
public interface ILeetcodeService {
    /**
     * 删除区间的数据
     *
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @author Lys
     * @date 2025/04/10 00:28
     */
    int delete(Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * 批量插入 Leetcode 记录
     *
     * @param leetcodeList 要插入的 Leetcode 记录列表
     * @return 是否插入成功
     */
    boolean batchInsert(List<LeetcodeCalendarEntity> leetcodeList);

    /**
     * 检查当天是否有提交，没有提交就发邮件提醒
     *
     * @author Lys
     * @date 2025/4/12 00:55
     */
    void check();

    /**
     * 同步leetcode信息
     *
     * @param userEntity
     */
    void syncLeetcodeInfo(UserEntity userEntity);

    /**
     * 获取leetcode看板卡片数据
     *
     * @param userId 用户ID
     */
    List<DashboardCardVO> getDashboardCard(int userId);
}
