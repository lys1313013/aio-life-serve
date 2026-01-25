package top.aiolife.record.pojo.leetcode;

import lombok.Data;

/**
 * 题目信息
 *
 * @author Lys
 * @date 2025/05/02 19:22
 */
@Data
public class QuestionDataResponse {

    private Data data;

    @lombok.Data
    public static class Data {
        private QuestionData question;
    }

    @lombok.Data
    public static class QuestionData {
        /**
         * 问题ID
         */
        private String questionId;

        /**
         * 题目标题
         */
        private String translatedTitle;

        /**
         *
         */
        private String titleSlug;

        /**
         * 题目中文内容
         */
        private String translatedContent;
    }
}
