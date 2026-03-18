package top.aiolife.llm.api;

import cn.dev33.satoken.stp.StpUtil;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.aiolife.core.constant.ResponseCodeConst;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.llm.pojo.entity.ChatMessageEntity;
import top.aiolife.llm.pojo.entity.ConversationEntity;
import top.aiolife.llm.service.ChatMessageService;
import top.aiolife.llm.service.ConversationService;
import top.aiolife.llm.service.LLMKeyService;
import top.aiolife.llm.service.LLMService;
import top.aiolife.record.service.ITimeRecordService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/llm")
public class LLMController {

    private final LLMService llmService;
    private final LLMKeyService llmKeyService;
    private final ITimeRecordService timeRecordService;
    private final ChatMessageService chatMessageService;
    private final ConversationService chatSessionService;

    @PostMapping("/chat")
    public ApiResponse<String> chat(@RequestBody Map<String, Object> request) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            String prompt = (String) request.get("prompt");
            String context = (String) request.get("context");
            Long conversationId = request.get("conversationId") != null ? Long.valueOf(request.get("conversationId").toString()) : null;

            var llmKey = llmKeyService.getDefaultLLMKey(userId);
            if (llmKey == null) {
                return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "请先配置大模型 API Key");
            }

            String fullPrompt = context != null && !context.isEmpty() ? context + "\n" + prompt : prompt;
            chatMessageService.saveMessage(userId, conversationId, "user", fullPrompt, llmKey.getModelName());

            String response = llmService.generateResponse(
                    llmKey.getApiKey(),
                    llmKey.getBaseUrl(),
                    llmKey.getModelName(),
                    prompt,
                    context
            );

            chatMessageService.saveMessage(userId, conversationId, "assistant", response, llmKey.getModelName());

            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to chat with LLM: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> request) {
        long userId = StpUtil.getLoginIdAsLong();
        String prompt = (String) request.get("prompt");
        String context = (String) request.get("context");
        Long conversationId = request.get("conversationId") != null ? Long.valueOf(request.get("conversationId").toString()) : null;

        SseEmitter emitter = new SseEmitter(300000L);
        StringBuilder fullResponse = new StringBuilder();

        try {
            var llmKey = llmKeyService.getDefaultLLMKey(userId);
            if (llmKey == null) {
                emitter.send(SseEmitter.event().data("{\"event\":\"error\",\"data\":\"请先配置大模型 API Key\"}"));
                emitter.complete();
                return emitter;
            }

            String fullPrompt = context != null && !context.isEmpty() ? context + "\n" + prompt : prompt;
            chatMessageService.saveMessage(userId, conversationId, "user", fullPrompt, llmKey.getModelName());

            var streamingModel = llmService.getStreamingChatModel(
                    llmKey.getApiKey(),
                    llmKey.getBaseUrl(),
                    llmKey.getModelName()
            );

            String modelName = llmKey.getModelName();
            streamingModel.chat(fullPrompt, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    try {
                        fullResponse.append(token);
                        emitter.send(SseEmitter.event().data(token));
                    } catch (IOException e) {
                        log.error("Failed to send token: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    try {
                        chatMessageService.saveMessage(userId, conversationId, "assistant", fullResponse.toString(), modelName);
                        emitter.send(SseEmitter.event().data("[DONE]"));
                        emitter.complete();
                    } catch (IOException e) {
                        log.error("Failed to send complete event: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    try {
                        emitter.send(SseEmitter.event().data("[ERROR] " + error.getMessage()));
                        emitter.complete();
                    } catch (IOException e) {
                        log.error("Failed to send error event: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Failed to start streaming chat: {}", e.getMessage(), e);
            try {
                emitter.send(SseEmitter.event().data("[ERROR] " + e.getMessage()));
            } catch (IOException ioException) {
                log.error("Failed to send error event: {}", ioException.getMessage());
            }
            emitter.complete();
        }

        emitter.onTimeout(() -> {
            log.warn("SSE emitter timeout");
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.debug("SSE emitter completed");
        });

        return emitter;
    }

    @PostMapping("/summarize/time-records")
    public ApiResponse<String> summarizeTimeRecords(@RequestBody Map<String, String> request) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            String type = request.get("type");

            LocalDate date = LocalDate.now();
            var timeRecords = timeRecordService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<top.aiolife.record.pojo.entity.TimeRecordEntity>()
                            .eq(top.aiolife.record.pojo.entity.TimeRecordEntity::getUserId, userId)
                            .eq(top.aiolife.record.pojo.entity.TimeRecordEntity::getDate, date)
                            .orderByDesc(top.aiolife.record.pojo.entity.TimeRecordEntity::getStartTime)
            );

            StringBuilder timeRecordsText = new StringBuilder();
            for (var record : timeRecords) {
                timeRecordsText.append(String.format("时间: %s-%s, 分类: %s, 标题: %s, 描述: %s\n",
                        record.getStartTime(), record.getEndTime(), record.getCategoryId(),
                        record.getTitle(), record.getDescription()));
            }

            var llmKey = llmKeyService.getDefaultLLMKey(userId);
            if (llmKey == null) {
                return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, "请先配置大模型 API Key");
            }

            String summary = llmService.summarizeTimeRecords(
                    llmKey.getApiKey(),
                    llmKey.getBaseUrl(),
                    llmKey.getModelName(),
                    timeRecordsText.toString()
            );

            return ApiResponse.success(summary);
        } catch (Exception e) {
            log.error("Failed to summarize time records: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @GetMapping("/chat/history")
    public ApiResponse<List<ChatMessageEntity>> getChatHistory(@RequestParam(required = false) Long conversationId) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            List<ChatMessageEntity> history;
            if (conversationId != null) {
                history = chatMessageService.listByconversationId(userId, conversationId);
            } else {
                history = chatMessageService.listByUserId(userId);
            }
            return ApiResponse.success(history);
        } catch (Exception e) {
            log.error("Failed to get chat history: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @DeleteMapping("/chat/history")
    public ApiResponse<Void> clearChatHistory(@RequestParam(required = false) Long conversationId) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            if (conversationId != null) {
                chatMessageService.deleteByconversationId(userId, conversationId);
            } else {
                chatMessageService.deleteByUserId(userId);
            }
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Failed to clear chat history: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @GetMapping("/sessions")
    public ApiResponse<List<ConversationEntity>> getSessions() {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            return ApiResponse.success(chatSessionService.listByUserId(userId));
        } catch (Exception e) {
            log.error("Failed to get chat sessions: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @PostMapping("/sessions")
    public ApiResponse<ConversationEntity> createSession(@RequestBody Map<String, String> request) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            String title = request.get("title");
            return ApiResponse.success(chatSessionService.createSession(userId, title));
        } catch (Exception e) {
            log.error("Failed to create chat session: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @PutMapping("/sessions/{conversationId}")
    public ApiResponse<Void> updateSession(@PathVariable Long conversationId, @RequestBody Map<String, String> request) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            String title = request.get("title");
            chatSessionService.updateTitle(userId, conversationId, title);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Failed to update chat session: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }

    @DeleteMapping("/sessions/{conversationId}")
    public ApiResponse<Void> deleteSession(@PathVariable Long conversationId) {
        try {
            long userId = StpUtil.getLoginIdAsLong();
            chatSessionService.deleteSession(userId, conversationId);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Failed to delete chat session: {}", e.getMessage(), e);
            return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
        }
    }
}
