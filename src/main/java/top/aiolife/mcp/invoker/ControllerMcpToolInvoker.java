package top.aiolife.mcp.invoker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.mcp.auth.McpSaTokenScope;
import top.aiolife.mcp.registry.ControllerMcpToolRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Controller MCP 工具调用器
 *
 * @author Lys
 * @date 2026/04/25
 */
@Component
@RequiredArgsConstructor
public class ControllerMcpToolInvoker {

    private final ObjectMapper objectMapper;

    public McpSchema.CallToolResult invoke(ControllerMcpToolRegistry.RegisteredMcpTool tool,
                                           Map<String, Object> arguments,
                                           Object loginId,
                                           RequestAttributes requestAttributes) {
        try {
            Object rawResult = McpSaTokenScope.runWithContext(loginId, requestAttributes, () -> invokeTool(tool, arguments));
            Object result = unwrapIfNecessary(rawResult, tool.operation().unwrapApiResponseData());
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(toJson(result))),
                    false,
                    result,
                    null
            );
        } catch (InvocationTargetException exception) {
            Throwable targetException = exception.getTargetException();
            return errorResult(targetException == null ? exception.getMessage() : targetException.getMessage());
        } catch (Exception exception) {
            return errorResult(exception.getMessage());
        }
    }

    private Object invokeTool(ControllerMcpToolRegistry.RegisteredMcpTool tool,
                              Map<String, Object> arguments) throws IllegalAccessException, InvocationTargetException {
        Object input = objectMapper.convertValue(arguments, tool.inputType());
        tool.method().setAccessible(true);
        return tool.method().invoke(tool.bean(), input);
    }

    private Object unwrapIfNecessary(Object rawResult, boolean unwrapApiResponseData) {
        if (unwrapApiResponseData && rawResult instanceof ApiResponse<?> apiResponse) {
            return apiResponse.getData();
        }
        return rawResult;
    }

    private String toJson(Object result) throws JsonProcessingException {
        if (result instanceof String value) {
            return value;
        }
        return objectMapper.writeValueAsString(result);
    }

    private McpSchema.CallToolResult errorResult(String errorMessage) {
        String safeMessage = errorMessage == null ? "工具执行失败" : errorMessage;
        return McpSchema.CallToolResult.builder()
                .addTextContent(safeMessage)
                .isError(true)
                .build();
    }
}
