package top.aiolife.mcp.handler;

import cn.dev33.satoken.stp.StpUtil;
import io.modelcontextprotocol.server.McpServerFeatures;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.aiolife.mcp.invoker.ControllerMcpToolInvoker;
import top.aiolife.mcp.config.McpServerConfig;
import top.aiolife.mcp.registry.ControllerMcpToolRegistry;

import java.util.List;

/**
 * MCP 工具处理器
 *
 * @author Lys
 * @date 2026/04/25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpToolHandlers {

    private final ControllerMcpToolRegistry toolRegistry;
    private final ControllerMcpToolInvoker toolInvoker;

    public List<McpServerFeatures.SyncToolSpecification> getToolSpecifications() {
        return toolRegistry.getAllTools().stream()
                .map(tool -> McpServerFeatures.SyncToolSpecification.builder()
                        .tool(tool.mcpTool())
                        .callHandler((exchange, request) -> {
                            Object loginId = currentLoginId(exchange);
                            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                            log.info("[MCP] 用户 {} 调用工具 {}", loginId == null ? "unknown" : loginId, tool.name());
                            return toolInvoker.invoke(tool, request.arguments(), loginId, requestAttributes);
                        })
                        .build())
                .toList();
    }

    private Object currentLoginId(io.modelcontextprotocol.server.McpSyncServerExchange exchange) {
        Object transportLoginId = exchange.transportContext().get(McpServerConfig.LOGIN_ID_CONTEXT_KEY);
        if (transportLoginId != null) {
            return transportLoginId;
        }
        try {
            return StpUtil.getLoginIdDefaultNull();
        } catch (Exception exception) {
            return null;
        }
    }
}
