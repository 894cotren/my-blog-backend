#!/bin/bash
set -e

# 一键打包脚本：Maven 打包 + Docker 镜像构建

APP_NAME="my-blog-backend"
IMAGE_TAG="grey/${APP_NAME}:1.0"

# 1. Maven 打包 (跳过测试)
echo "==> 1/2 Maven 打包中..."
./mvnw clean package -DskipTests

# 2. 拷贝打包产物为固定文件名 (Dockerfile 引用固定名，避免版本号变化时也要改 Dockerfile)
JAR_FILE=$(ls target/*.jar 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "❌ 未找到打包产物 target/*.jar，请检查 Maven 打包是否成功"
    exit 1
fi
cp "$JAR_FILE" "target/${APP_NAME}.jar"
echo "==> 打包产物：$JAR_FILE -> target/${APP_NAME}.jar"

# 3. Docker 镜像构建
echo "==> 2/2 Docker 镜像构建中..."

# 普通镜像构建，随系统版本构建 amd/arm
# docker build -t ${IMAGE_TAG} -f ./Dockerfile .

# 兼容 amd、arm 构建镜像
# docker buildx build --load --platform linux/amd64,linux/arm64 -t ${IMAGE_TAG} -f ./Dockerfile .

# 真兼容 mac m4 芯片构建镜像 (只构建 amd64 并加载到本地，用于部署到 amd64 服务器)
docker buildx build --platform linux/amd64 -t ${IMAGE_TAG} -f ./Dockerfile . --load

echo "==> 构建完成：${IMAGE_TAG}"
