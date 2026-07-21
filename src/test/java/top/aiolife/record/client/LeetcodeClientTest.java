package top.aiolife.record.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import top.aiolife.record.pojo.leetcode.QuestionDataResponse;
import top.aiolife.record.pojo.leetcode.TodayRecordResponse;
import top.aiolife.record.pojo.leetcode.UserCalendarResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeetcodeClientTest {

    @Mock
    private RestTemplate restTemplate;

    private LeetcodeClient leetcodeClient;

    @BeforeEach
    void setUp() {
        leetcodeClient = new LeetcodeClient(restTemplate);
    }

    @Test
    void getTodayRecord_返回接口响应() {
        TodayRecordResponse expected = new TodayRecordResponse();
        mockExchange(LeetcodeClient.url, expected);

        TodayRecordResponse actual = leetcodeClient.getTodayRecord();

        assertSame(expected, actual);
    }

    @Test
    void getQuestionData_返回接口响应() {
        QuestionDataResponse expected = new QuestionDataResponse();
        mockExchange(LeetcodeClient.url, expected);

        QuestionDataResponse actual = leetcodeClient.getQuestionData("two-sum");

        assertSame(expected, actual);
    }

    @Test
    void getUserCalendar_返回接口响应() {
        UserCalendarResponse expected = new UserCalendarResponse();
        mockExchange(LeetcodeClient.url + "noj-go/", expected);

        UserCalendarResponse actual = leetcodeClient.getUserCalendar("lys1313013");

        assertSame(expected, actual);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void mockExchange(String url, Object response) {
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn((ResponseEntity) ResponseEntity.ok(response));
    }
}
