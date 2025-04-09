package com.lys.record.pojo.leetcode;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * leetcode热力图接口返回值
 *
 * @author Lys
 * @date 2025/04/09 23:45
 */
@Getter
@Setter
public class UserCalendarResponse {
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private UserCalendar userCalendar;
    }

    @Getter
    @Setter
    public static class UserCalendar {
        private Integer streak;
        private Integer totalActiveDays;
        private String submissionCalendar;
        private List<Integer> activeYears;
        private Object monthlyMedals; // 根据实际情况可替换为具体类型
        private Integer recentStreak;
    }
}