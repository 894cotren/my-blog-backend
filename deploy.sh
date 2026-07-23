#!/bin/bash
set -e

# 服务器端部署脚本 (本地 build.sh 的服务器版)
# 作用：用已上传的 jar 在服务器上构建 Docker 镜像，并启动/重启容器
# 与本地 build.sh 的区别：不含 Maven 打包步骤 (jar 已由本地打好并上传)
#
# 服务器目录结构要求 (与本脚本同级)：
#   ./Dockerfile
#   ./docker-compose-app-v1.0.yml
#   ./target/my-blog-backend.jar
#
# 用法：./deploy.sh

APP_NAME="my-blog-backend"
IMAGE_TAG="grey/${APP_NAME}:1.0"
COMPOSE_FILE="docker-compose-app-v1.0.yml"
JAR_PATH="target/${APP_NAME}.jar"

# 0. 环境检查：docker + compose (兼容 v2 插件 / v1 老命令)
command -v docker >/dev/null 2>&1 || { echo "❌ 未安装 docker，请先安装"; exit 1; }
if docker compose version >/dev/null 2>&1; then
    COMPOSE="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE="docker-compose"
else
    echo "❌ 未找到 docker compose (v2 插件) 或 docker-compose (v1)，请先安装"
    exit 1
fi

# 1. 检查 jar 产物就位 (Dockerfile 按 target/my-blog-backend.jar 查找)
echo "==> 1/3 检查打包产物..."
if [ ! -f "$JAR_PATH" ]; then
    echo "❌ 未找到 $JAR_PATH"
    echo "   请先在本地打 jar 并上传到该路径"
    exit 1
fi
echo "    产物就位：$JAR_PATH ($(du -h "$JAR_PATH" | cut -f1))"

# 2. 构建 Docker 镜像 (服务器原生 amd64，无需 buildx / --platform)
echo "==> 2/3 Docker 镜像构建中..."
docker build -t ${IMAGE_TAG} -f ./Dockerfile .

# 3. 启动/重启容器 (镜像更新后 compose 会自动重建容器)
echo "==> 3/3 启动容器..."
$COMPOSE -f ${COMPOSE_FILE} up -d

echo ""
echo "==> 部署完成：${IMAGE_TAG}"
echo "==> 查看日志：docker logs -f ${APP_NAME}"
echo "==> 查看状态：$COMPOSE -f ${COMPOSE_FILE} ps"
