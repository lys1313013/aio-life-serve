package top.aiolife.record.mcp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCardMcpVO {
    private String type;
    private String title;
    private String value;
    private String totalTitle;
    private String totalValue;
}
