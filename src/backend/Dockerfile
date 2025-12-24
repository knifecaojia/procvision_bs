FROM maven:3.8.5-openjdk-11 as module-install
WORKDIR /app
# 复制pom.xml和依赖（缓存依赖）
COPY pom.xml ./
RUN mkdir -p imustsz-admin && \
    mkdir -p imustsz-framework && \
    mkdir -p imustsz-system && \
    mkdir -p imustsz-quartz && \
    mkdir -p imustsz-generator && \
    mkdir -p imustsz-common && \
    mkdir -p imustsz-business

COPY imustsz-admin/pom.xml ./imustsz-admin/
COPY imustsz-framework/pom.xml ./imustsz-framework/
COPY imustsz-system/pom.xml ./imustsz-system/
COPY imustsz-quartz/pom.xml ./imustsz-quartz/
COPY imustsz-generator/pom.xml ./imustsz-generator/
COPY imustsz-common/pom.xml ./imustsz-common/
COPY imustsz-business/pom.xml ./imustsz-business/

RUN mvn clean install -DskipTests -Dmaven.main.skip=true -Dspring-boot.repackage.skip=true -B

FROM maven:3.8.5-openjdk-11 AS dependency-download

WORKDIR /app

# 复制阶段1的Maven仓库（包含已安装的本地模块）
COPY --from=module-install /root/.m2 /root/.m2
# 复制pom文件（触发依赖解析）
COPY pom.xml ./
RUN mkdir -p imustsz-admin imustsz-framework imustsz-system imustsz-quartz imustsz-generator imustsz-common imustsz-business
COPY imustsz-admin/pom.xml ./imustsz-admin/
COPY imustsz-framework/pom.xml ./imustsz-framework/
COPY imustsz-system/pom.xml ./imustsz-system/
COPY imustsz-quartz/pom.xml ./imustsz-quartz/
COPY imustsz-generator/pom.xml ./imustsz-generator/
COPY imustsz-common/pom.xml ./imustsz-common/
COPY imustsz-business/pom.xml ./imustsz-business/

RUN mvn dependency:go-offline -B

FROM maven:3.8.5-openjdk-11 AS build

WORKDIR /app

COPY --from=dependency-download /root/.m2 /root/.m2

COPY . ./

# 完整打包（此时有源码，能编译主类，repackage正常执行）
RUN mvn clean package -DskipTests -B

FROM openjdk:11.0.6

WORKDIR /app

COPY --from=build /app/imustsz-business/target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]