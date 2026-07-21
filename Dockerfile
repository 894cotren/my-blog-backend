# 基础镜像 (Java 21 运行环境，本项目要求 JDK 21)
# 国内拉取慢可换华为云加速源：
# FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/eclipse-temurin:21-jre
FROM eclipse-temurin:21-jre

# 作者
MAINTAINER grey

# 运行参数：可通过 -e PARAMS="--spring.profiles.active=prod" 传入启动参数
ENV PARAMS=""

# 时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 工作目录 (日志等相对路径会在此目录下生成，对应 compose 挂载的 /app/logs)
WORKDIR /app

# 添加应用 (build.sh 会把打包产物拷贝为固定名 my-blog-backend.jar)
ADD target/my-blog-backend.jar /app.jar

# 容器启动时执行的命令
# JAVA_OPTS: JVM 参数 (如 -Xmx512m)，通过 -e JAVA_OPTS="..." 传入
# PARAMS:    应用参数 (如 --spring.profiles.active=prod)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar $PARAMS"]
