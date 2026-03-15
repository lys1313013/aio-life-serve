package top.aiolife.llm.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

public interface LLMService {

    /**
     * 获取聊天模型
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelName 模型名称
     * @return 聊天模型
     */
    ChatModel getChatModel(String apiKey, String baseUrl, String modelName);

    /**
     * 获取流式聊天模型
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelName 模型名称
     * @return 流式聊天模型
     */
    StreamingChatModel getStreamingChatModel(String apiKey, String baseUrl, String modelName);

    /**
     * 生成响应
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelName 模型名称
     * @param prompt 提示词
     * @param context 上下文
     * @return 响应内容
     */
    String generateResponse(String apiKey, String baseUrl, String modelName, String prompt, String context);

    /**
     * 总结时迹记录
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelName 模型名称
     * @param timeRecords 时迹记录
     * @return 总结内容
     */
    String summarizeTimeRecords(String apiKey, String baseUrl, String modelName, String timeRecords);
}
