# 指定基础镜像，在其上进行定制
FROM java:8

# 维护者信息
MAINTAINER LC ahlc@sina.cn

# 这里的 /tmp 目录就会在运行时自动挂载为匿名卷，任何向 /tmp 中写入的信息都不会记录进容器存储层
VOLUME /tmp

# 工作目录路径 这里将系统根目录【/】当成工作路径
WORKDIR /

# 复制上下文目录下的target/BotMan-App-20220805.jar 到容器里 起别名为 app.jar
ADD target/BotMan-App-20220805.jar /app.jar

# 如果pom.xml配置了【<finalName>app</finalName>】 所以会自动打包成 app.jar
# 则要改成下面的写法 复制上下文目录下的target/app.jar 到容器里 起别名也叫为 app.jar
# ADD target/app.jar /app.jar

# 默认环境变量
ENV APP_NAME 'BotMan => Push Anything To Anywhere'
ENV WHO_AM_I 'the BotMan'
# ENV APP_PORT 9528

# 声明暴露的端口
EXPOSE 9528 9090

# 指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT ["java","-jar","/app.jar"]
