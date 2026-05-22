# LangChain4j + Streamable HTTP MCP 接入计划（Thought save）

## Summary

- 目标是在现有 Spring Boot MVC 项目内新增一个基于 Streamable HTTP 的 MCP Server。
- 工具暴露方式采用“直接标注 Controller 方法”，首个接入点为 `ThoughtController.save`。
- 元数据尽量复用 LangChain4j 的 `@Tool` 能力，MCP 服务端传输使用 MCP Java SDK 的 Spring MVC Transport。
- 为了支撑后续批量接入多个接口，本次不做单点硬编码，而是同时落一套“注解扫描 + 统一注册 + 通用调用”的最小基础设施。

## Current State Analysis

### 已确认的项目现状

- 项目为 Spring Boot 3.3.10 + Spring MVC，应用前缀为 `/api`，端口 `45678`。
- 已集成 `dev.langchain4j:langchain4j:1.12.2` 与 `langchain4j-open-ai:1.12.2`，但 `pom.xml` 中还没有 MCP 服务端相关依赖。
- 仓库根目录已有 `TestStreamable.java`，说明本地已验证过 `WebMvcStreamableServerTransportProvider` 这一方向。
- `SaTokenConfig` 当前对大多数接口统一做登录校验，并且已支持“Bearer Token 或 API Key 二选一”的认证方式。
- `ThoughtController.save` 当前签名为 `ApiResponse<Boolean> save(@RequestBody ThoughtEntity entity)`，保存前会覆盖 `userId`、`createUser`、`updateTime`，并插入 `events` 关联记录。

### 已锁定的实现决策

- 注解位置：直接标注 Controller。
- MCP 认证：复用现有 `Authorization: Bearer ...` / API Key 拦截链路。
- 首个工具入参：复用现有 `ThoughtEntity`，不改动现有 `/thought/save` HTTP 协议。

### 需要在方案中额外处理的风险

- `ThoughtEntity` 继承 `BaseEntity`，天然带有 `id`、`createUser`、`createTime`、`updateTime`、`updateUser`、`isDeleted` 等服务端字段；如果直接原样生成 MCP schema，会把不该由 AI 输入的字段暴露出去。
- `ThoughtController.save` 当前直接遍历 `entity.getEvents()`，若 AI 省略 `events` 或传 `null`，会出现空指针风险。
- LangChain4j 官方 MCP 文档主要覆盖“作为 MCP 客户端消费远端工具”；本需求的服务端 Streamable HTTP 暴露仍需以 MCP Java SDK 为主，LangChain4j 主要承担注解与工具描述复用职责。

## Proposed Changes

### 1. `pom.xml`

#### 变更内容

- 增加 MCP Java SDK 相关依赖：
  - `io.modelcontextprotocol.sdk:mcp`
  - `io.modelcontextprotocol.sdk:mcp-spring-webmvc`
- 保持现有 `langchain4j` 依赖不动。

#### 原因

- `mcp` 提供 MCP Server 核心能力。
- `mcp-spring-webmvc` 提供 `WebMvcStreamableServerTransportProvider`，可以直接挂到当前 Spring MVC 应用中，不需要切到 WebFlux。
- 这样与现有项目技术栈最贴合，也能直接复用 `/api` 上下文路径和鉴权拦截器。

### 2. `src/main/java/top/aiolife/mcp/annotation/McpOperation.java`

#### 变更内容

- 新增一个面向 Controller 方法的自定义注解，建议字段：
  - `name`：MCP 工具名
  - `description`：工具说明
  - `ignoreInputFields`：需要从 schema 中剔除的输入字段
  - `unwrapApiResponseData`：是否对 `ApiResponse` 做返回值解包

#### 原因

- 仅使用 `@Tool` 不足以表达 MCP 暴露策略，例如字段裁剪、返回值处理、后续权限标签等。
- 自定义注解可以保持 Controller 侧接入成本很低，同时为后续批量接入保留扩展位。

#### 使用方式

- 后续在 Controller 方法上组合使用：
  - `@Tool`
  - `@McpOperation`

### 3. `src/main/java/top/aiolife/mcp/registry/ControllerMcpToolRegistry.java`

#### 变更内容

- 新增 Controller 级工具注册表。
- 启动时扫描 Spring 容器中的 Bean，筛出同时具备 `@Tool` 与 `@McpOperation` 的方法。
- 为每个方法建立注册信息，至少包含：
  - 工具名
  - 描述
  - Spring Bean 实例
  - Method 反射对象
  - 入参类型
  - LangChain4j `ToolSpecification`
  - MCP `Tool`

#### 原因

- 后续要接很多接口，必须把“扫描、注册、查询”独立出去，避免把逻辑塞进配置类。
- 统一注册表后，`listTools` 和 `callTool` 都只依赖注册表，后续新增接口只需打注解。

### 4. `src/main/java/top/aiolife/mcp/adapter/LangChain4jToolSchemaAdapter.java`

#### 变更内容

- 新增一个适配器，把 LangChain4j 通过 `ToolSpecifications.toolSpecificationsFrom(...)` 产出的 `ToolSpecification` 转换成 MCP 需要的 `McpSchema.Tool`。
- 在转换阶段结合 `@McpOperation.ignoreInputFields` 对入参 schema 做字段裁剪。
- 本次首个接口至少裁剪这些字段：
  - `id`
  - `userId`
  - `createUser`
  - `createTime`
  - `updateTime`
  - `updateUser`
  - `isDeleted`
  - `events[].id`
  - `events[].thoughtId`
  - `events[].createUser`
  - `events[].createTime`
  - `events[].updateTime`
  - `events[].updateUser`
  - `events[].isDeleted`

#### 原因

- 用户希望复用 `ThoughtEntity`，但又不能把服务端管理字段直接暴露给 AI。
- 通过 schema 过滤可以保留实体复用，同时把 MCP 输入约束收敛到真正需要的业务字段。

### 5. `src/main/java/top/aiolife/mcp/invoker/ControllerMcpToolInvoker.java`

#### 变更内容

- 新增通用调用器，负责：
  - 将 MCP `arguments` 反序列化为目标方法入参对象
  - 调用目标 Controller 方法
  - 统一处理返回值
- 本期只支持当前首批最常见签名：
  - 单个 `@RequestBody` Java Bean 入参
  - 返回 `ApiResponse<T>`
- 返回值策略：
  - 对 `ThoughtController.save` 这类返回 `ApiResponse<Boolean>` 的方法，默认保留完整响应结构，不只返回 `data`。

#### 原因

- 直接调用 Controller 反射方法时，需要独立封装参数绑定和返回值适配。
- 保留完整 `ApiResponse` 更贴近现有后端语义，也方便后续统一处理成功/失败信息。

### 6. `src/main/java/top/aiolife/mcp/config/McpServerConfig.java`

#### 变更内容

- 新增 MCP Server 配置类，完成以下职责：
  - 声明 `WebMvcStreamableServerTransportProvider`
  - 约定 MCP 端点为 `/mcp`
  - 声明 `RouterFunction` 并挂入 Spring MVC
  - 创建同步 `McpSyncServer`
  - 注册 `listTools` / `callTool` 处理逻辑
- 端点落地后，实际访问地址为：
  - `POST /api/mcp`
  - `GET /api/mcp`（用于 Streamable HTTP 的 SSE/消息通道）

#### 原因

- 项目已有 Spring MVC，因此应直接走 `WebMvcStreamableServerTransportProvider`，避免引入额外 Web 技术栈。
- `/api` 前缀会自动继承现有应用配置，便于与现有系统统一部署。

### 7. `src/main/java/top/aiolife/mcp/handler/McpToolHandlers.java`

#### 变更内容

- 新增 MCP 请求处理层，封装：
  - `listTools`：从 `ControllerMcpToolRegistry` 返回全部可用工具
  - `callTool`：根据工具名查找并调用 `ControllerMcpToolInvoker`
- 在调用失败时统一返回 MCP 标准错误内容，并记录工具名、用户 ID、异常摘要。

#### 原因

- 配置、注册、执行应拆层，避免 `McpServerConfig` 过重。
- 后续如果要加权限校验、调用日志、限流，这一层最适合扩展。

### 8. `src/main/java/top/aiolife/record/api/ThoughtController.java`

#### 变更内容

- 给 `save` 方法增加 LangChain4j `@Tool` 注解和自定义 `@McpOperation` 注解。
- 明确工具名称与中文描述，例如语义为“保存一条想法，并可附带多个关联事件”。
- 对 `entity.getEvents()` 做空安全处理，允许 `events` 为空数组或 `null`。

#### 原因

- 这是本次首个真实接入点。
- AI 生成参数时，`events` 很可能缺省；若不先做空安全兜底，MCP 工具一接入就容易因为参数不完整而失败。
- 保持原 `/thought/save` HTTP 接口地址与主体逻辑不变，只是在方法层增加 MCP 暴露能力。

#### 本次方法级注解策略

- `@Tool` 负责让 LangChain4j 产出工具说明与参数结构。
- `@McpOperation` 负责声明 MCP 专属元数据与字段裁剪策略。

### 9. `src/main/java/top/aiolife/mcp/README 或 docs/...`（二选一，按现有文档习惯）

#### 变更内容

- 补一份最小接入说明，包含：
  - MCP 端点地址
  - 认证头示例
  - `initialize` / `tools/list` / `tools/call` 调试方式
  - 后续新增接口的接入步骤

#### 原因

- 本次重点是做成可复制模板，文档必须覆盖“别人如何继续接下一个 Controller 方法”。

## Assumptions & Decisions

- 不改造现有 `/thought/save` 对前端的 REST 协议和 URL。
- 不在本次引入独立 Tool 层，避免和用户已确认的“Controller 直标注”冲突。
- 不在本次引入单独 MCP Token，继续复用现有 Bearer Token / API Key。
- 不在本次把所有 Controller 一次性纳入 MCP，只完成首个接口与通用骨架。
- 本次通用调用器先聚焦“单个请求体对象入参 + `ApiResponse` 返回值”这一路径，后续再扩展到分页查询、多参数、路径参数等类型。

## Verification Steps

### 编译验证

- 执行 Maven 编译，确认新增依赖、MCP 配置类、扫描注册逻辑可以通过编译。

### 启动验证

- 启动应用后确认 `/api/mcp` 端点可访问。
- 确认访问 MCP 端点时仍走现有 Sa-Token / API Key 鉴权链路。

### MCP 协议验证

- 用 MCP 客户端或本地 HTTP 请求完成一次 `initialize`。
- 调用 `tools/list`，确认能看到 `ThoughtController.save` 对应工具。
- 检查工具 schema，确认未暴露 `BaseEntity` 的服务端管理字段。

### 业务验证

- 调用工具保存一条仅包含 `content` 的想法，确认成功。
- 调用工具保存一条带 `events[].content` 的想法，确认主记录和关联事件都成功入库。
- 调用工具时省略 `events`，确认不会空指针。

### 回归验证

- 保持原 HTTP `POST /api/thought/save` 仍可正常调用。
- 检查 `ThoughtController.query` / `update` / `batchDelete` 不受本次改动影响。
