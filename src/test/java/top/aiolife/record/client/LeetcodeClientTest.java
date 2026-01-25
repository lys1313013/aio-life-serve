package top.aiolife.record.client;

import top.aiolife.record.pojo.leetcode.QuestionDataResponse;
import top.aiolife.record.pojo.leetcode.TodayRecordResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/05/02 19:08
 */
class LeetcodeClientTest {

    @Test
    void getTodayRecord() {
        LeetcodeClient leetcodeClient = new LeetcodeClient(new RestTemplate());
        TodayRecordResponse todayRecord = leetcodeClient.getTodayRecord();
        System.out.println(todayRecord);
    }

    @Test
    void getQuestionData() {
        LeetcodeClient leetcodeClient = new LeetcodeClient(new RestTemplate());
        QuestionDataResponse questionData = leetcodeClient.getQuestionData("two-sum");
        System.out.println(questionData);
    }

    @Test
    void getUserCalendar() {
        LeetcodeClient leetcodeClient = new LeetcodeClient(new RestTemplate());
        var userCalendar = leetcodeClient.getUserCalendar("lys1313013");
        System.out.println(userCalendar);
    }
}