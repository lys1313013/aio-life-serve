package com.lys.record.pojo.leetcode;

import lombok.Data;

import java.util.List;

/**
 * 每日一题信息
 */
@Data
public class TodayRecordResponse {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<TodayRecord> todayRecord;
    }

    @lombok.Data
    public static class TodayRecord {
        private String date;
        private String userStatus;
        private Question question;
        private Object lastSubmission;
    }

    @lombok.Data
    public static class Question {
        private String questionId;
        private String frontendQuestionId;
        private String difficulty;
        private String title;
        private String titleCn;
        private String titleSlug;
        private Boolean paidOnly;
        private Object freqBar;
        private Boolean isFavor;
        private Double acRate;
        private Object status;
        private Integer solutionNum;
        private Boolean hasVideoSolution;
        private List<TopicTag> topicTags;
        private Extra extra;
    }

    @lombok.Data
    public static class TopicTag {
        private String name;
        private String nameTranslated;
        private String id;
    }

    @lombok.Data
    public static class Extra {
        private List<TopCompanyTag> topCompanyTags;
    }

    @lombok.Data
    public static class TopCompanyTag {
        private String imgUrl;
        private String slug;
        private Integer numSubscribed;
    }
}
