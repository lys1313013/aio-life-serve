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
        /**
         * 最长连续打卡天数
         */
        private Integer streak;
        /**
         * 总活跃天数
         */
        private Integer totalActiveDays;
        /**
         * 每日提交的json数据
         */
        private String submissionCalendar;
        /**
         * 有几个年度有数据
         */
        private List<Integer> activeYears;
        private Object monthlyMedals;
        /**
         * 当前连续打卡的天数
         */
        private Integer recentStreak;
    }
}