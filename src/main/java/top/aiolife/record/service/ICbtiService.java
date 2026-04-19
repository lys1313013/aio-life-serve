package top.aiolife.record.service;

import top.aiolife.record.pojo.entity.CbtiPersonalityEntity;

import java.util.List;
import java.util.Map;

/**
 * CBTI 服务接口
 *
 * @author Ethan
 * @date 2026/04/18
 */
public interface ICbtiService {

    /**
     * 获取 CBTI 题库与维度定义。
     *
     * @return 题库与维度定义的集合数据
     * @throws Exception 读取资源或解析失败时抛出
     */
    Map<String, Object> getQuestions() throws Exception;

    /**
     * 获取人格类型列表。
     *
     * @return 人格类型列表
     */
    List<CbtiPersonalityEntity> listPersonalities();

    /**
     * 根据人格代码获取人格类型详情。
     *
     * @param code 人格代码
     * @return 人格类型详情
     */
    CbtiPersonalityEntity getPersonalityByCode(String code);

    /**
     * 计算 CBTI 测试结果并保存为历史记录。
     *
     * @param userId 用户ID
     * @param answers 答题数据（题号 -> 选项值）
     * @param hiddenAnswers 彩蛋答题数据
     * @return 测试结果（包含人格、匹配度、维度结果、匹配排行等）
     * @throws Exception 计算或保存失败时抛出
     */
    Map<String, Object> testAndSave(long userId, Map<Integer, Integer> answers, Map<String, Object> hiddenAnswers) throws Exception;

    /**
     * 获取用户 CBTI 测试历史。
     *
     * @param userId 用户ID
     * @return 历史列表
     */
    List<Map<String, Object>> getUserHistory(long userId);

    /**
     * 获取用户 CBTI 测试历史详情。
     *
     * @param id 历史记录ID
     * @param userId 用户ID
     * @return 历史详情
     */
    Map<String, Object> getHistoryDetail(long id, long userId);
}

