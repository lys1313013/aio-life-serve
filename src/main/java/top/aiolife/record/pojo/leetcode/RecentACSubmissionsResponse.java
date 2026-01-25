package top.aiolife.record.pojo.leetcode;

import lombok.Data;
import java.util.List;

@Data
public class RecentACSubmissionsResponse {
    private DataContainer data;

    @Data
    public static class DataContainer {
        private List<RecentACSubmission> recentACSubmissions;
    }

    @Data
    public static class RecentACSubmission {
        private String submissionId;
        private Long submitTime;
        private Question question;
    }

    @Data
    public static class Question {
        private String title;
        private String translatedTitle;
        private String titleSlug;
        private String questionFrontendId;
    }
}
