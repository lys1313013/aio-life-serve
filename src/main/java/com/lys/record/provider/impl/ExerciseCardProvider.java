package com.lys.record.provider.impl;

import com.lys.record.pojo.vo.DashboardCardVO;
import com.lys.record.provider.DashboardCardProvider;
import com.lys.record.service.IExerciseRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 运动卡片提供者
 *
 * @author Lys
 * @date 2026/01/23 23:08
 */
@Slf4j
@Component
@AllArgsConstructor
public class ExerciseCardProvider implements DashboardCardProvider {

    private final IExerciseRecordService exerciseRecordService;

    @Override
    public String getType() {
        return "EXERCISE";
    }

    @Override
    public String getTitle() {
        return "今日运动";
    }

    @Override
    public String getTotalTitle() {
        return "连续运动";
    }

    @Override
    public String getIcon() {
        return "mdi:run";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public DashboardCardVO getCard(int userId) {
        DashboardCardVO card = new DashboardCardVO();
        card.setType(getType());
        card.setIcon(getIcon());
        card.setTitle(getTitle());
        card.setIconClickUrl("/my-hub/exercise");
        card.setTitleClickUrl("/my-hub/exercise");
        try {
            int count = exerciseRecordService.countTodayExerciseTypes((long) userId);
            card.setValue(String.valueOf(count));
            card.setValueColor(count == 0 ? "red" : "#3FB27F");
            card.setTotalTitle(getTotalTitle());
            card.setTotalValue(exerciseRecordService.getConsecutiveExerciseDays((long) userId) + " 天");
            card.setRefreshInterval(600);
        } catch (Exception e) {
            log.error("获取运动数据失败", e);
            card.setValue("获取失败");
            card.setTotalValue("获取失败");
        }
        return card;
    }
}
