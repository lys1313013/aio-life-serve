# 2026-04-30_menu-backend-mode-and-password-dependency-fix

## 1. 新增功能

### 1.1 权限菜单状态变更后即时刷新侧边栏
- 在“系统管理 -> 权限菜单”中，启用/禁用菜单后，会重新拉取后端菜单并刷新前端 `accessStore`。
- 通过编辑弹窗修改菜单状态后，也会同步刷新侧边栏菜单。
- 刷新菜单前会先重置旧动态路由，避免已经挂载的旧路由继续参与菜单生成。

---

## 2. 修复的问题

### 2.1 密码管理页面 Vite 编译失败
- 现象：进入页面后 Vite overlay 报错：
  - `Failed to resolve import "gm-crypto" from "src/utils/crypto.ts"`
- 原因：
  - `apps/web-antd/src/utils/crypto.ts` 使用了 `gm-crypto` 的 `SM4`。
  - 但 `@vben/web-antd` 应用包自身没有声明 `gm-crypto` 依赖。
  - pnpm workspace 下，应用不能稳定依赖 monorepo 根包的 `dependencies`。
- 修复：
  - 在前端应用包 `apps/web-antd/package.json` 中补充 `gm-crypto`。
  - 同步更新 `pnpm-lock.yaml`。

### 2.2 权限菜单“隐藏/禁用”后左侧菜单仍显示
- 现象：
  - 权限菜单列表中 `/my-hub` 等菜单已经切换为禁用状态。
  - 但左侧侧边栏仍然显示“记录”等菜单项。
- 原因：
  - 后端 `sys_menu.status=0` 已生效，`/menu/all` 会过滤禁用菜单。
  - 前端当前会话已经生成过 `accessStore.accessMenus`，开关后没有重新生成。
  - 同时前端菜单生成仍读取 `preferences.app.accessMode`，浏览器本地偏好可能保存为 `frontend` 或 `mixed`，导致静态路由混入侧边栏，绕过后端菜单状态。
- 修复：
  - `router/access.ts` 固定使用 `backend` 菜单模式。
  - 权限菜单状态更新成功后，调用 `resetRoutes()` 清理旧动态路由。
  - 重新执行 `generateAccess()`，并写回：
    - `accessStore.setAccessMenus(...)`
    - `accessStore.setAccessRoutes(...)`
    - `accessStore.setIsAccessChecked(true)`

---

## 3. 修改原因

- 权限菜单属于后端可运营配置，菜单是否展示应以 `sys_menu.status` 为准，不能被浏览器本地偏好或前端静态路由绕过。
- 权限菜单管理页修改状态后，应立即反馈到侧边栏，避免用户误以为开关无效。
- 密码管理功能依赖 SM4 加密能力，入口应用必须显式声明运行时依赖，保证本地开发和团队安装结果一致。

---

## 4. 技术实现细节

### 4.1 前端依赖修复
- 文件：`aio-life-front-main/aio-life-front-main/apps/web-antd/package.json`
  - 新增依赖：`gm-crypto@0.1.12`
- 文件：`aio-life-front-main/aio-life-front-main/pnpm-lock.yaml`
  - 同步锁定 `gm-crypto` 版本。

### 4.2 固定后端菜单模式
- 文件：`aio-life-front-main/aio-life-front-main/apps/web-antd/src/router/access.ts`
- 关键调整：
  - 将 `generateAccessible(preferences.app.accessMode, ...)` 改为 `generateAccessible('backend', ...)`。
  - 避免浏览器缓存偏好把菜单模式切回 `frontend` 或 `mixed`。

### 4.3 权限菜单刷新链路
- 文件：`aio-life-front-main/aio-life-front-main/apps/web-antd/src/views/system/menu/index.vue`
- 新增逻辑：
  - 引入 `useAccessStore`、`useUserStore`、`useAuthStore`。
  - 引入 `generateAccess`、`resetRoutes`、`router`、`accessRoutes`。
  - 新增 `refreshAccessibleMenus()`：
    - 获取当前用户角色。
    - 调用 `resetRoutes()` 清理旧动态路由。
    - 调用 `generateAccess()` 重新生成后端菜单和路由。
    - 写回 `accessStore`。
- 调用位置：
  - `toggleStatus()` 状态开关成功后。
  - `save()` 编辑弹窗保存成功后。

### 4.4 本地验证
- 前端已重新启动并监听：
  - `http://localhost:5666/`
- Vite 日志显示启动成功：
  - `VITE v7.3.0 ready`
- 后端此前已启动：
  - API：`http://localhost:45678/api`
  - 管理端口：`http://localhost:45679`

---

## 5. 遇到的坑

- 只刷新权限菜单表格不够，侧边栏读取的是 `accessStore.accessMenus`，必须重新生成并写回。
- 只重新生成菜单也不够，旧动态路由可能仍保留在 router 中，需要先 `resetRoutes()`。
- `preferences.app.accessMode` 会被浏览器本地缓存影响，开发时即使默认配置是 `backend`，实际运行也可能被旧缓存改成 `frontend/mixed`。
- pnpm workspace 下，依赖必须声明在实际使用它的应用包里；只放在根 `package.json`，Vite 在应用内解析时仍可能失败。
- 项目当前 `typecheck` 存在较多历史 TypeScript 报错，本次只验证了 Vite 启动和相关页面编译，不把历史类型问题混入本次修复范围。
