# Docker 部署目录

本目录包含系统所有服务的容器化部署配置。

## 目录说明

*   **mysql/**: MySQL 数据库服务配置
*   **redis/**: Redis 缓存服务配置
*   **nginx/**: Nginx 网关及静态资源服务配置
*   **minio/**: MinIO 对象存储服务配置

## 使用说明

根目录下通常包含 `docker-compose.yml` 文件（待创建），用于一键启动所有服务。
