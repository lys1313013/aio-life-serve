package top.aiolife.llm.service.impl;

import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LLMServiceTest {

    @InjectMocks
    private LLMServiceImpl llmService;

    @Test
    void testGetChatModel() {
        String apiKey = "test-api-key";
        String baseUrl = "https://api.openai.com/v1";
        String modelName = "gpt-4";

        ChatModel model = llmService.getChatModel(apiKey, baseUrl, modelName);

        assertNotNull(model);
    }

    @Test
    void testGetChatModelWithInvalidParams() {
        ChatModel model = llmService.getChatModel(null, null, null);
        assertNotNull(model);
    }

    @Test
    void testGenerateResponseWithContext() {
        String apiKey = "test-api-key";
        String baseUrl = "https://api.openai.com/v1";
        String modelName = "gpt-4";
        String prompt = "Hello, how are you?";
        String context = "Previous conversation context";

        assertThrows(RuntimeException.class, () -> {
            llmService.generateResponse(apiKey, baseUrl, modelName, prompt, context);
        });
    }

    @Test
    void testGenerateResponseWithoutContext() {
        String apiKey = "test-api-key";
        String baseUrl = "https://api.openai.com/v1";
        String modelName = "gpt-4";
        String prompt = "Hello, how are you?";

        assertThrows(RuntimeException.class, () -> {
            llmService.generateResponse(apiKey, baseUrl, modelName, prompt, null);
        });
    }

    @Test
    void testSummarizeTimeRecords() {
        String apiKey = "test-api-key";
        String baseUrl = "https://api.openai.com/v1";
        String modelName = "gpt-4";
        String timeRecords = "Test time records data";

        assertThrows(RuntimeException.class, () -> {
            llmService.summarizeTimeRecords(apiKey, baseUrl, modelName, timeRecords);
        });
    }
}
