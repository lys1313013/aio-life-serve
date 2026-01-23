package com.lys.record.provider.impl;

import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.provider.DashboardCardProvider;
import com.lys.record.service.ILeetcodeService;
import com.lys.sso.mapper.UserMapper;
import com.lys.sso.pojo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

/**
 * LeetCode 卡片提供者
 *
 * @author Lys
 * @date 2026/01/23 23:08
 */
@Slf4j
@Component
@AllArgsConstructor
public class LeetcodeCardProvider implements DashboardCardProvider {

    private final ILeetcodeService leetcodeService;
    private final UserMapper userMapper;

    @Override
    public String getType() {
        return "LEETCODE";
    }

    @Override
    public int getOrder() {
        return 30;
    }

    @Override
    public DashboardCardVO getCard(int userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null || user.getLeetcodeAcct() == null) {
            return null;
        }

        DashboardCardVO card = new DashboardCardVO();
        card.setIcon("devicon:leetcode");
        card.setIconClickUrl("https://leetcode.cn/u/" + user.getLeetcodeAcct());
        card.setTitle("每日一题");
        try {
            Pair<Boolean, String> result = leetcodeService.checkToday(user, false);
            card.setTitleClickUrl(result.getSecond());
            card.setValue(result.getFirst() ? "已完成" : "未完成");
            card.setValueColor(result.getFirst() ? "#3FB27F" : "red");
            card.setTotalTitle("今日提交");
            card.setTotalValue(String.valueOf(leetcodeService.getTodaySubmissionCount(user.getLeetcodeAcct())));
        } catch (Exception e) {
            log.error("获取 LeetCode 数据失败", e);
            card.setValue("获取失败");
        }
        return card;
    }
}
