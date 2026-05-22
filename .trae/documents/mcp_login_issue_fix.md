# MCP 工具调用导致登录状态失效问题修复计划

## 问题分析 (Current State Analysis)
1. **现象**：用户反馈调用 MCP 工具时，会将自己的登录状态挤掉（登出），并且发现 MCP 工具会自动执行登录动作（实际上不应该登录）。
2. **原因定位**：
   - MCP 工具通常使用 API Key (`ak-xxx`) 进行鉴权。请求会被 `ApiKeyInterceptor` 拦截。
   - 目前在 `ApiKeyInterceptor` 中，为了让后续上下文能获取到用户 ID，使用了 `StpUtil.login(apiKeyEntity.getUserId())` 进行自动登录，并在 `afterCompletion` 中使用 `StpUtil.logout()` 注销。
   - `StpUtil.login()` 是一个有状态的操作，它会生成新的 Token，并通过 `Set-Cookie` 将 Token 写入到 HTTP 响应中。
   - 当客户端（如浏览器、Postman 或共享 Cookie 的环境）收到这个 Cookie 时，会覆盖原本的 Web 登录 Token。随后 `StpUtil.logout()` 销毁了该 Token，导致用户的 Web 会话失效（被挤下线）。
3. **正确做法**：API Key 属于无状态认证，不应该调用 `StpUtil.login()` 产生真实的会话和 Cookie。但由于底层业务逻辑（如 `timeRecordService` 等）强依赖 `StpUtil.getLoginIdAsLong()` 获取当前用户 ID，如果彻底解耦会导致大量重构。因此，最佳方案是使用 Sa-Token 提供的临时身份切换功能 `StpUtil.switchTo()`。

## 修改方案 (Proposed Changes)

### 1. 修改 `ApiKeyInterceptor`
- **文件路径**：`src/main/java/top/aiolife/sso/interceptor/ApiKeyInterceptor.java`
- **具体修改**：
  - 在 `preHandle` 方法中，将 `StpUtil.login(apiKeyEntity.getUserId());` 替换为 `StpUtil.switchTo(apiKeyEntity.getUserId());`。
  - 在 `afterCompletion` 方法中，将 `StpUtil.logout();` 替换为 `StpUtil.endSwitch();`。
- **说明**：`switchTo` 会将当前请求上下文的 LoginId 临时切换为指定的 userId，既满足了底层业务 `StpUtil.getLoginId()` 等方法的调用需求，又不会生成真实的 Token 和 Cookie，实现了完全无状态的 API Key 认证，改动最小且最安全。

## 验证步骤 (Verification Steps)
1. 运行并使用带有 API Key 的方式调用 MCP 接口。
2. 检查 HTTP 响应头，确认不再包含 `Set-Cookie: Authorization=...` 相关的写入或删除操作。
3. 确认同时在浏览器中保持 Web 登录状态时，调用 MCP 工具不再会导致 Web 端的登录状态失效。
4. 确保在修改后，依赖 `StpUtil.getLoginIdAsLong()` 的接口依然能够正常获取到用户 ID 并执行。