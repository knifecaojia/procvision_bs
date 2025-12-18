# MySQL 服务配置

本目录用于存放 MySQL 数据库的配置、数据和初始化脚本。

## 目录结构

*   **conf/**: 存放 MySQL 配置文件 (`my.cnf`)
*   **data/**: 存放 MySQL 数据文件 (映射到容器内 `/var/lib/mysql`)
*   **init/**: 存放数据库初始化 SQL 脚本，容器首次启动时会自动执行
