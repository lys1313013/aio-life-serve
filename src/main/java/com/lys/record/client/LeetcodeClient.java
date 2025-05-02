package com.lys.record.client;

import com.lys.record.pojo.leetcode.QuestionDataResponse;
import com.lys.record.pojo.leetcode.TodayRecordResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/05/02 19:07
 */
@Service
@AllArgsConstructor
public class LeetcodeClient {

    private final RestTemplate restTemplate;

    public static final String url = "https://leetcode.cn/graphql/";

    /**
     * 获取每日一题信息
     */
    public TodayRecordResponse getTodayRecord() {

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("query", """
                  query questionOfToday {
                  todayRecord {
                    date
                    userStatus
                    question {
                      questionId
                      frontendQuestionId: questionFrontendId
                      difficulty
                      title
                      titleCn: translatedTitle
                      titleSlug
                      paidOnly: isPaidOnly
                      freqBar
                      isFavor
                      acRate
                      status
                      solutionNum
                      hasVideoSolution
                      topicTags {
                        name
                        nameTranslated: translatedName
                        id
                      }
                      extra {
                        topCompanyTags {
                          imgUrl
                          slug
                          numSubscribed
                        }
                      }
                    }
                    lastSubmission {
                      id
                    }
                  }
                }
                """);

        // 发送请求并获取响应
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, null);

        ResponseEntity<TodayRecordResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<TodayRecordResponse>() {
        });
        return responseEntity.getBody();
    }

    /**
     * 获取题目数据
     */
    public QuestionDataResponse getQuestionData(String titleSlug) {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("query", """
                query questionData($titleSlug: String!) {
                         question(titleSlug: $titleSlug) {
                           questionId
                           questionFrontendId
                           categoryTitle
                           boundTopicId
                           title
                           titleSlug
                           content
                           translatedTitle
                           translatedContent
                           isPaidOnly
                           difficulty
                           likes
                           dislikes
                           isLiked
                           similarQuestions
                           contributors {
                             username
                             profileUrl
                             avatarUrl
                             __typename
                           }
                           langToValidPlayground
                           topicTags {
                             name
                             slug
                             translatedName
                             __typename
                           }
                           companyTagStats
                           codeSnippets {
                             lang
                             langSlug
                             code
                             __typename
                           }
                           stats
                           hints
                           solution {
                             id
                             canSeeDetail
                             __typename
                           }
                           status
                           sampleTestCase
                           metaData
                           judgerAvailable
                           judgeType
                           mysqlSchemas
                           enableRunCode
                           envInfo
                           book {
                             id
                             bookName
                             pressName
                             source
                             shortDescription
                             fullDescription
                             bookImgUrl
                             pressImgUrl
                             productUrl
                             __typename
                           }
                           isSubscribed
                           isDailyQuestion
                           dailyRecordStatus
                           editorType
                           ugcQuestionId
                           style
                           exampleTestcases
                           jsonExampleTestcases
                           __typename
                         }
                       }
                """);
        Map<String, Object> variables = new HashMap<>();
        variables.put("titleSlug", titleSlug);

        requestBody.put("variables", variables);


        // 发送请求并获取响应
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, null);
        ResponseEntity<QuestionDataResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<QuestionDataResponse>() {
        });
        return responseEntity.getBody();
    }
}
