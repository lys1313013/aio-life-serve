package com.lys.record.provider.impl;

import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.provider.DashboardCardProvider;
import com.lys.record.service.ITimeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 年份流逝卡片提供者
 *
 * @author Lys
 * @date 2026/01/23 23:08
 */
@Slf4j
@Component
@AllArgsConstructor
public class YearPassedCardProvider implements DashboardCardProvider {

    private final ITimeRecordService timeRecordService;

    @Override
    public String getType() {
        return "YEAR_PASSED";
    }

    @Override
    public String getTitle() {
        return "当前状态";
    }

    @Override
    public String getTotalTitle() {
        return "当年已过";
    }

    @Override
    public String getIcon() {
        return "tdesign:time";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public DashboardCardVO getCard(int userId) {
        DashboardCardVO card = new DashboardCardVO();
        card.setTitle(getTitle());
        card.setIcon(getIcon());
        try {
            card.setValue(timeRecordService.getLastRecordTimeDiff((long) userId));
        } catch (Exception e) {
            log.error("获取最后一条记录时间差失败", e);
            card.setValue("获取失败");
        }
        card.setTotalTitle(getTotalTitle());
        card.setTotalValue(getDaysPassed() + " / " + LocalDate.now().lengthOfYear() + " 天");
        card.setRefreshInterval(60);
        return card;
    }

    private int getDaysPassed() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now().withDayOfYear(1), LocalDate.now()) + 1;
    }
}
