package top.aiolife.llm.service.impl;

import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LLMServiceTest {

    @InjectMocks
    @Spy
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

        ChatModel chatModel = mock(ChatModel.class);
        doReturn(chatModel).when(llmService).getChatModel(apiKey, baseUrl, modelName);
        when(chatModel.chat(context + "\n" + prompt)).thenReturn("mock response");

        String response = llmService.generateResponse(apiKey, baseUrl, modelName, prompt, context);

        assertEquals("mock response", response);
        verify(chatModel).chat(context + "\n" + prompt);
    }

    @Test
    void testGenerateResponseWithoutContext() {
        String apiKey = "test-api-key";
        String baseUrl = "https://api.openai.com/v1";
        String modelName = "gpt-4";
        String prompt = "Hello, how are you?";

        ChatModel chatModel = mock(ChatModel.class);
        doReturn(chatModel).when(llmService).getChatModel(apiKey, baseUrl, modelName);
        when(chatModel.chat(prompt)).thenReturn("mock response");

        String response = llmService.generateResponse(apiKey, baseUrl, modelName, prompt, null);

        assertEquals("mock response", response);
        verify(chatModel).chat(prompt);
    }

}
