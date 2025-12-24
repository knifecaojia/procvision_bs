# Docker 部署目录

本目录包含系统所有服务的容器化部署配置。

## 目录说明

*   **mysql/**: MySQL 数据库服务配置
*   **redis/**: Redis 缓存服务配置
*   **nginx/**: Nginx 网关及静态资源服务配置
*   **minio/**: MinIO 对象存储服务配置

## 使用说明

* 进入docker目录，执行docker compose up -d

### 注意
* 若要使用mysql配置文件my.cnf，要先执行chmod 644 ./mysql/conf/my.cnf 来将/mysql/conf目录下的my.cnf权限设置为644，否则mysql会忽略该配置文件。
