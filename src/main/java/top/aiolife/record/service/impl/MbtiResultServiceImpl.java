package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.aiolife.record.mapper.IMbtiResultMapper;
import top.aiolife.record.pojo.entity.MbtiResultEntity;
import top.aiolife.record.service.IMbtiResultService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MBTI测试结果服务实现
 *
 * @author Lys
 * @date 2026-03-23
 */
@Slf4j
@Service
public class MbtiResultServiceImpl extends ServiceImpl<IMbtiResultMapper, MbtiResultEntity> implements IMbtiResultService {
    
    @Value("${aio.life.serve.mbti.api-key:}")
    private String mbtiApiKey;
    
    @Value("${aio.life.serve.mbti.base-url:https://api.devil.ai}")
    private String mbtiBaseUrl;
    
    private final RestTemplate restTemplate;
    
    public MbtiResultServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> createTest() {
        try {
            // 文档地址：https://devil.ai/api
            String url = mbtiBaseUrl + "/v1/new_test?api_key=" + mbtiApiKey + "&lang=cn";
            log.info("创建MBTI测试请求: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("创建MBTI测试响应: {}", response);
            
            Map<String, Object> result = new HashMap<>();
            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("test_id")) {
                    result.put("testId", data.get("test_id"));
                    result.put("testUrl", data.get("test_url"));
                    result.put("success", true);
                } else {
                    result.put("success", false);
                    result.put("message", "创建测试失败: 未获取到test_id");
                }
            } else {
                result.put("success", false);
                result.put("message", "创建测试失败: 响应格式错误");
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建MBTI测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "创建测试失败: " + e.getMessage());
            return result;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> checkTest(String testId) {
        try {
            String url = mbtiBaseUrl + "/v1/check_test?api_key=" + mbtiApiKey + "&test_id=" + testId;
            log.info("查询MBTI测试结果请求: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("查询MBTI测试结果响应: {}", response);
            
            Map<String, Object> result = new HashMap<>();
            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                result.put("success", true);
                result.put("data", data);
                
                if (data != null && data.containsKey("prediction")) {
                    result.put("mbtiType", data.get("prediction"));
                    result.put("predictions", data.get("predictions"));
                    result.put("traitOrderConscious", data.get("trait_order_conscious"));
                    result.put("traitOrderShadow", data.get("trait_order_shadow"));
                    result.put("matches", data.get("matches"));
                    result.put("resultsPage", data.get("results_page"));
                }
            } else {
                result.put("success", false);
                result.put("message", "查询结果失败: 响应格式错误");
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询MBTI测试结果失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询结果失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public void saveResult(MbtiResultEntity result) {
        this.save(result);
        log.info("保存MBTI测试结果: userId={}, mbtiType={}", result.getUserId(), result.getMbtiType());
    }
    
    @Override
    public List<MbtiResultEntity> getUserHistory(Long userId) {
        LambdaQueryWrapper<MbtiResultEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MbtiResultEntity::getUserId, userId);
        queryWrapper.orderByDesc(MbtiResultEntity::getCreateTime);
        return this.list(queryWrapper);
    }
}
