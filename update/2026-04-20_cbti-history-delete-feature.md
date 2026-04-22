# 2026-04-20_cbti-history-delete-feature

## 1. 新增功能

### 1.1 CBTI 历史记录删除能力
- 在 CBTI“测试历史”弹窗的“操作”列新增“删除”按钮（带二次确认）。
- 前端新增删除接口封装：`DELETE /cbti/result/{id}`。
- 删除成功后自动刷新历史列表，界面即时反馈。

### 1.2 后端删除接口补齐
- 新增后端接口：`DELETE /api/cbti/result/{id}`。
- Service 新增 `deleteHistory(id, userId)`，用于执行删除逻辑。

---

## 2. 修复的问题

### 2.1 历史记录只能查看、无法清理
- 现象：用户只能查看 CBTI 历史，无法移除不需要的数据。
- 修复：
  - 前端新增“删除”交互与 API 调用。
  - 后端新增逻辑删除接口与服务实现。

### 2.2 接口一致性问题
- 现象：前端新增删除能力后，若后端未提供 DELETE 接口会报“Method not supported”。
- 修复：
  - 补齐 `DELETE /api/cbti/result/{id}`。
  - 重启服务后验证 DELETE 返回 `rscode=0, data=true`。

---

## 3. 修改原因

- 历史记录属于用户私有数据，需要提供可自主管理（删除）能力。
- 采用逻辑删除可与现有 `is_deleted=0` 查询机制保持一致，避免数据硬删除风险。
- 删除本人历史可减少误删和越权风险。

---

## 4. 技术实现细节

### 4.1 前端实现
- 文件：`apps/web-antd/src/api/core/cbti.ts`
  - 新增 `deleteCbtiHistoryApi(id: string)`。
- 文件：`apps/web-antd/src/views/_core/profile/cbti-setting.vue`
  - 在历史记录表格 `action` 列新增 `Popconfirm + 删除按钮`。
  - 新增 `deleteHistoryItem(record)`：删除成功后调用 `getCbtiHistoryApi()` 刷新。
  - 增加行级删除中状态，避免重复点击。

### 4.2 后端实现
- 文件：`src/main/java/top/aiolife/record/api/CbtiController.java`
  - 新增 `@DeleteMapping("/result/{id}")`。
- 文件：`src/main/java/top/aiolife/record/service/ICbtiService.java`
  - 新增 `boolean deleteHistory(long id, long userId)`。
- 文件：`src/main/java/top/aiolife/record/service/impl/CbtiServiceImpl.java`
  - 校验记录存在、归属当前用户、未删除后执行逻辑删除：`is_deleted=1`。
  - 通过 `fillUpdateCommonField(userId)` 写入更新人和更新时间。

---

## 5. 遇到的坑

- 删除接口新增后，如果后端未重启，前端会继续命中旧路由，表现为 DELETE 不支持。
- 联调时必须同时校验：
  - 接口返回成功。
  - 列表刷新后数据确实减少。
