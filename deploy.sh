#!/bin/bash

# 设置严格模式
set -euo pipefail

# 从环境变量读取配置
SERVER_IP="${AIO_LIFE_SERVER_IP:-}"
USERNAME="${AIO_LIFE_SERVER_USER:-}"
PASSWORD="${AIO_LIFE_SERVER_PASSWORD:-}"
PROJECT_DIR="${PROJECT_DIR:-.}"  # 项目根目录，默认为当前目录

# 验证必需的环境变量
if [[ -z "$SERVER_IP" ]]; then
    echo "错误: 未设置环境变量 AIO_LIFE_SERVER_IP" >&2
    exit 1
fi

if [[ -z "$USERNAME" ]]; then
    echo "错误: 未设置环境变量 AIO_LIFE_SERVER_USER" >&2
    exit 1
fi

if [[ -z "$PASSWORD" ]]; then
    echo "错误: 未设置环境变量 AIO_LIFE_SERVER_PASSWORD" >&2
    exit 1
fi

# 查找 target 目录下的 JAR 文件
echo "在 $PROJECT_DIR/target/ 目录下查找 JAR 文件..."

# 检查 target 目录是否存在
if [[ ! -d "$PROJECT_DIR/target" ]]; then
    echo "错误: target 目录不存在: $PROJECT_DIR/target" >&2
    echo "请确保已在项目目录下执行了构建命令（如 mvn package 或 ./gradlew build）" >&2
    exit 1
fi

# 查找 JAR 文件（排除原始 JAR，优先选择可执行 JAR）
# 先尝试找包含 original 的排除，找普通的 JAR
JAR_FILES=($(find "$PROJECT_DIR/target" -maxdepth 1 -name "*.jar" -not -name "*original*.jar" 2>/dev/null))

# 如果没找到，再找所有 JAR 文件
if [[ ${#JAR_FILES[@]} -eq 0 ]]; then
    JAR_FILES=($(find "$PROJECT_DIR/target" -maxdepth 1 -name "*.jar" 2>/dev/null))
fi

# 验证是否找到 JAR 文件
if [[ ${#JAR_FILES[@]} -eq 0 ]]; then
    echo "错误: 在 $PROJECT_DIR/target/ 目录下未找到 JAR 文件" >&2
    echo "请先构建项目：mvn package 或 ./gradlew build" >&2
    exit 1
fi

# 如果找到多个 JAR 文件，选择最新的一个（按修改时间）
if [[ ${#JAR_FILES[@]} -gt 1 ]]; then
    echo "找到多个 JAR 文件，选择最新的一个："
    # 按修改时间排序，取最新的
    LOCAL_JAR_PATH=$(ls -t "${JAR_FILES[@]}" | head -n 1)
else
    LOCAL_JAR_PATH="${JAR_FILES[0]}"
fi

echo "选择的 JAR 文件: $LOCAL_JAR_PATH"

# 获取 JAR 文件名
JAR_FILENAME=$(basename "$LOCAL_JAR_PATH")

echo "开始部署到服务器: $SERVER_IP"
echo "本地文件: $LOCAL_JAR_PATH"
echo "远程路径: /projects/service/$JAR_FILENAME"

# 创建远程目录（如果不存在）
echo "创建远程目录..."
sshpass -p "$PASSWORD" ssh -o StrictHostKeyChecking=no "$USERNAME@$SERVER_IP" \
    "mkdir -p /projects/service"

# 上传 JAR 文件
echo "上传 JAR 文件..."
# 使用 rsync 显示进度条
PROGRESS_FLAG="--progress"
if rsync --help | grep -q "--info"; then
    PROGRESS_FLAG="--info=progress2"
fi

RSYNC_CMD="sshpass -p '$PASSWORD' rsync -e 'ssh -o StrictHostKeyChecking=no' $PROGRESS_FLAG '$LOCAL_JAR_PATH' '$USERNAME@$SERVER_IP:/projects/service/$JAR_FILENAME'"
eval $RSYNC_CMD

# 检查上传是否成功
if [[ $? -ne 0 ]]; then
    echo "错误: 文件上传失败" >&2
    exit 1
fi

# 检查上传是否成功
if [[ $? -ne 0 ]]; then
    echo "错误: 文件上传失败" >&2
    exit 1
fi

# 执行远程 run.sh 脚本
echo "执行远程 run.sh 脚本..."
sshpass -p "$PASSWORD" ssh -o StrictHostKeyChecking=no "$USERNAME@$SERVER_IP" \
    "cd /projects/service && ./run.sh"

# 检查脚本执行结果
if [[ $? -eq 0 ]]; then
    echo "部署和执行成功完成！"
else
    echo "警告: run.sh 脚本执行完成，但可能有错误" >&2
fi