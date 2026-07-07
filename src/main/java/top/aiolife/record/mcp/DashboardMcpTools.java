package top.aiolife.record.mcp;

import cn.dev33.satoken.stp.StpUtil;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import top.aiolife.mcp.annotation.McpToolProvider;
import top.aiolife.record.mcp.vo.DashboardCardMcpVO;
import top.aiolife.record.pojo.vo.DashboardCardVO;
import top.aiolife.record.provider.DashboardCardProvider;

import java.util.Comparator;
import java.util.List;

@McpToolProvider
@RequiredArgsConstructor
public class DashboardMcpTools {

    private final List<DashboardCardProvider> providers;

    @Tool("读取当前用户的仪表盘卡片信息，返回所有可见卡片的类型、标题、当前值和总量")
    public List<DashboardCardMcpVO> dashboard_cards() {
        long userId = StpUtil.getLoginIdAsLong();
        return providers.stream()
                .filter(p -> p.isVisible(userId))
                .sorted(Comparator.comparingInt(DashboardCardProvider::getOrder))
                .map(p -> {
                    DashboardCardVO card = p.getCard(userId);
                    return new DashboardCardMcpVO(
                            card.getType(),
                            card.getTitle(),
                            card.getValue(),
                            card.getTotalTitle(),
                            card.getTotalValue()
                    );
                })
                .toList();
    }
}
