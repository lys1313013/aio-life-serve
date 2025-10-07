# aio-life-serve - 个人生活数据管理系统

掌控人生的各种数据，一站式管理你的生活信息。

## 项目简介

aio-life-serve（All-In-One Life Server）是一个个人生活数据管理系统，旨在帮助用户集中管理各种生活数据，包括设备信息、任务待办、编程练习记录、收支情况等。通过统一的平台，用户可以更好地了解和掌控自己的生活状态。

## 功能特性

1. 📱 设备信息管理
   - 记录电子设备信息
   - 跟踪设备购买日期和使用情况

2. ✅ 任务待办管理
   - 创建、编辑、删除任务
   - 任务拖拽排序
   - 任务分类管理

3. 💻 LeetCode刷题记录
   - 自动同步LeetCode刷题数据
   - 邮件提醒功能
   - 刷题热力图展示

4. 💰 收支管理
   - 记录收入和支出
   - 按年/月统计数据
   - 分类统计展示

5. 📊 个人看板
   - 年度数据统计
   - 生活状态可视化

6. 🔐 用户认证
   - 基于Sa-Token的安全认证
   - 用户权限管理

## 技术栈

- Java 17
- Spring Boot 3.x
- MyBatis Plus
- MySQL 8.x
- Redis
- Sa-Token（权限认证）
- Lombok
- MapStruct
- Hutool工具库
- Maven

## 项目结构

```
src/
├── main/
│   ├── java/com/lys/
│   │   ├── config/          # 配置文件
│   │   ├── core/            # 核心模块
│   │   ├── datasource/      # 数据源模块
│   │   ├── record/          # 记录模块
│   │   └── sso/             # 用户认证模块
│   └── resources/           # 资源文件
└── test/                   # 测试代码
```

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.x
- Redis
- Maven 3.6+

### 安装步骤

1. 克隆项目
```bash
git clone [项目地址]
```

2. 配置数据库
   - 创建MySQL数据库
   - 修改`application.yml`中的数据库连接配置
