# 时迹关联阅读和观影的查询及表结构变更方案

## 1. 当前状态分析
目前系统中的“时迹（Time Track）”功能记录了用户在各个分类下花费的时间（存储在 `time_record` 表中），但它缺乏与具体业务实体（如正在阅读的书籍或正在观看的影视）关联的通用字段。目前针对“运动”分类，是在 `exercise_record` 中保存了 `time_id`，但这种方式对于“一次阅读/观影对应多次时间记录”的场景不够适用。阅读记录（`read_record`）和观影记录（`movie`）均有各自的 `status` 字段，且 `0` 和 `1` 均代表“未开始”和“进行中”状态。

## 2. 表结构与枚举字典变更方案
为实现时迹对不同业务模块（如阅读、观影等）的通用关联，并在前后端解耦硬编码，采用以下方案：

### 2.1 枚举类定义与接口暴露
不使用数据库字典（`sys_dict`），直接在后端定义标准的 Java 枚举：
1. **统一进度状态枚举 (`ProgressStatusEnum`)**:
   - `NOT_STARTED(0, "未开始")`
   - `IN_PROGRESS(1, "进行中")`
   - `COMPLETED(2, "已完成")`
   - `ON_HOLD(3, "搁置")`
2. **时迹关联类型枚举 (`RelateTypeEnum`)**:
   - `READ(1, "阅读")`
   - `MOVIE(2, "观影")`

**提供枚举查询接口**: 
在后端提供一个接口（例如 `/api/enum/relate-types`），以供前端动态获取 `RelateTypeEnum` 列表（如返回 `[{ "value": 1, "label": "阅读" }, { "value": 2, "label": "观影" }]`）。

### 2.2 SQL 变更脚本
```sql
ALTER TABLE time_record 
ADD COLUMN relate_id BIGINT COMMENT '关联业务ID（阅读记录/观影记录等的主键ID）',
ADD COLUMN relate_type TINYINT COMMENT '关联业务类型：1-阅读，2-观影';
```

### 2.3 实体类与 DTO 变更
在后端对应的 Java 类中增加关联字段：
- `TimeRecordEntity.java`
- `TimeRecordReq.java`
- `TimeRecordVO.java`
```java
/**
 * 关联业务类型，对应 RelateTypeEnum
 */
private Integer relateType;

/**
 * 关联业务ID
 */
private Long relateId;
```
同时在前端的 `TimeSlot` 和 `TimeSlotFormData` 接口（`types.ts`）中补充 `relateId` 和 `relateType`。

## 3. 查询方案
### 3.1 后端查询接口
为了在前端时迹表单中能够选择未开始和进行中的数据，需要分别对阅读和观影模块提供活跃数据查询支持。
可以在 `ReadRecordController` 和 `MovieController` 中增加 `listActive` 接口，或者扩展现有的 `pageList` 接口使其支持 `statusList` 查询条件。

- **阅读活跃数据查询**: 查询 `read_record` 表中 `status IN (0, 1)` 的数据，按 `update_time` 降序返回。
- **观影活跃数据查询**: 查询 `movie` 表中 `status IN (0, 1)` 的数据，按 `update_time` 降序返回。

### 3.2 前端交互与数据流方案
1. **动态识别关联类型 (消除硬编码)**: 
   - 前端在 `TimeSlotEditForm.vue` 初始化时，通过后端新增的枚举查询接口获取关联类型数据（例如返回 `[{label: '阅读', value: 1}, {label: '观影', value: 2}]`）。
   - 通过计算属性，用当前选中的分类名称（`category.name`）去匹配枚举列表中的 `label`，如果匹配成功，则获取到对应的 `relateType` 值，从而判断是否需要展示关联按钮。
   ```typescript
   // 伪代码示例
   const currentRelateType = computed(() => {
     const category = props.categories.find(c => c.id === formState.value.categoryId);
     const matchedEnum = relateTypeList.value.find(e => category?.name.includes(e.label));
     return matchedEnum ? Number(matchedEnum.value) : null;
   });
   ```
2. **动态加载并展示弹窗选项 (需注意手机端适配)**: 
   - 当 `currentRelateType` 为 `1`（阅读）时，表单中展示“关联阅读记录”的按钮。点击按钮弹出弹窗，弹窗内以**卡片形式**（Card）展示未开始和阅读中的数据供用户选择。
   - 当 `currentRelateType` 为 `2`（观影）时，表单中展示“关联观影记录”的按钮。点击按钮弹出弹窗，弹窗内以**卡片形式**展示想看和在看的数据供用户选择。
   - **手机端适配细节**: 弹窗宽度使用动态计算（例如 `isMobile ? '95vw' : 600`），内部的卡片列表采用响应式网格布局（如 PC 端 `grid-cols-3`，移动端 `grid-cols-2` 或 `grid-cols-1`），确保在手机端浏览和点击不会局促。
3. **表单展示 (需注意手机端适配)**:
   - 弃用简单的 `Select` 下拉框。在弹窗中选中卡片后，弹窗关闭，并在表单中展示已选中卡片的缩略信息（如封面、标题、进度等）。
   - 提供“重新选择”或“清除”的按钮，按钮在移动端需保证触控区域足够大。
   - 选中卡片的数据 ID 绑定到 `formState.value.relateId`。
   - 在选择后或保存前，将 `formState.value.relateType` 设为 `currentRelateType` 的值。
4. **数据反显**:
   - 当编辑已有的时间段时，如果存在 `relateId` 且 `relateType` 匹配当前分类，应默认确保能够加载出对应的记录信息，以卡片或缩略图的形式在表单中展示，而不是仅仅显示一个 ID 或名称。

## 4. 假设与决策
- **多态关联设计**: 采用 `relate_id` 和 `relate_type` 直接记录在 `time_record` 表中，可以应对一种业务对象（一本书）关联多条时间记录的场景，而不需要繁琐的中间表。
- **状态映射**: 确认阅读的 `0,1` 和观影的 `0,1` 均符合“未开始”与“进行中”的语义，可以直接使用。

## 5. 验证步骤
- 执行 SQL 并在本地数据库中确认表字段新增成功。
- 启动后端和前端服务。
- 在“我的记录-阅读/观影”中分别创建“未开始”和“进行中”的记录。
- 打开时迹页面，新建时间段并选择“阅读”或“观影”分类，确认能正确加载上述创建的数据。
- 选中具体关联项并保存，检查数据库中 `time_record` 表的 `relate_id` 和 `relate_type` 是否正确写入。
- 重新编辑该时间段，确认之前选中的关联项能够正常反显。