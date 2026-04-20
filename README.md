# aio-life-serve - 人生管理系统

> 记录、统计、分析人生的所有数据

## 项目简介

**AIO Life** 是一款 All-in-One 人生管理系统，致力于全方位记录、统计与分析生活数据。通过主动录入与第三方同步，实现人生痕迹的全面数字化，助您洞察规律，掌控生活节奏。

## 仓库说明（Fork）

本项目当前为 Fork 版本，仓库地址如下：

- 后端（当前）：https://github.com/guoxuanm42-lang/aio-life-serve
- 前端（当前）：https://github.com/guoxuanm42-lang/aio-life-front
- 后端（原始）：https://github.com/lys1313013/aio-life-serve
- 前端（原始）：https://github.com/lys1313013/aio-life-front

## 技术栈

- Java 21
- Spring Boot 3.x
- MyBatis Plus
- MySQL 8.x
- Redis
- Sa-Token（登录认证）
- Lombok
- MapStruct
- Hutool工具库
- MinIO (对象存储)
- Maven

## 项目结构

```

```

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.x
- Redis
- MinIO
- Maven 3.6+

### 安装步骤

1. 克隆项目
```bash
git clone https://github.com/guoxuanm42-lang/aio-life-serve
```

2. 配置数据库
   - 创建MySQL数据库
   - 执行 sql 文件夹下的脚本文件
   - 修改`application.yml`中的数据库连接配置
3. 启动依赖服务（MySQL、Redis、MinIO）
4. 启动后端程序
5. 启动前端程序

## 启动项目（命令版）

以下命令基于 Windows + PowerShell 示例。

### 1) 启动后端（aio-life-serve）

```bash
cd d:\my_document\program_product\life_os\aio-life-serve-main\aio-life-serve-main
mvn spring-boot:run
```

后端默认地址：

- API: `http://localhost:45678/api`
- 健康检查: `http://localhost:45678/api/actuator/health`

### 2) 启动前端（aio-life-front）

```bash
cd d:\my_document\program_product\life_os\aio-life-front-main\aio-life-front-main
pnpm install
pnpm --filter @vben/web-antd dev
```

前端默认地址：

- Web: `http://localhost:5173`

### 3) 前端本地环境变量（必需）

在 `apps/web-antd/.env.development` 中至少配置：

```env
VITE_APP_TITLE=AIO-LIFE
VITE_GLOB_API_URL=http://localhost:45678/api
```

### 4) MinIO 启动示例

如果本机未安装 Docker，可使用 MinIO 可执行文件：

```bash
cd d:\my_document\program_product\life_os\tools\minio
.\minio.exe server .\data --address ":1300" --console-address ":1301"
```

建议环境变量：

- `MINIO_ROOT_USER=aio_life`
- `MINIO_ROOT_PASSWORD=aio_life`

后端默认读取的 MinIO 地址：

- Endpoint: `http://localhost:1300`
- Console: `http://localhost:1301`
