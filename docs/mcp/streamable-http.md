# MCP Streamable HTTP 接入说明

## 端点信息

- MCP 端点：`/api/mcp`
- 传输协议：`streamable-http`
- 认证方式：复用现有接口认证
  - `Authorization: Bearer <token>`
  - 或现有 API Key 认证头

## 当前已接入工具

- `thought_save`
  - 说明：保存一条想法，并可附带多个关联事件
  - 入参主体：
    - `content`
    - `events[].content`

## 调试示例

### 初始化

```bash
curl -X POST 'http://localhost:45678/api/mcp' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Authorization: Bearer <token>' \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "initialize",
    "params": {
      "protocolVersion": "2025-03-26",
      "capabilities": {},
      "clientInfo": {
        "name": "curl-client",
        "version": "1.0.0"
      }
    }
  }'
```

### 查询工具列表

```bash
curl -X POST 'http://localhost:45678/api/mcp' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Authorization: Bearer <token>' \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/list",
    "params": {}
  }'
```

### 调用想法保存工具

```bash
curl -X POST 'http://localhost:45678/api/mcp' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Authorization: Bearer <token>' \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "thought_save",
      "arguments": {
        "content": "今天完成了 MCP 接入",
        "events": [
          {
            "content": "新增 streamable-http MCP server"
          }
        ]
      }
    }
  }'
```

## 后续新增接口步骤

1. 在目标 Controller 方法上增加 `@Tool`
2. 在同一方法上增加 `@McpOperation`
3. 配置 `name`、`description` 与 `ignoreInputFields`
4. 保持方法签名为单个请求体对象，返回 `ApiResponse<T>`
5. 编译后通过 `tools/list` 检查新工具是否自动注册
