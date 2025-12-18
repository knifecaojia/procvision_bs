# 工业视觉装配引导与检测系统 (B/S架构)

## 项目简介

本项目为工业视觉装配引导与检测系统的B/S架构管理平台。该平台主要用于与MOM（制造运营管理）系统交互，管理工艺数据、算法模型、工单任务，并为桌面端（C/S架构）提供鉴权、任务分发及数据收集服务。

### 核心功能

1. **MOM集成**：通过网闸与企业内网MOM系统通信，接收生产任务，上报检测结果。
2. **工艺数字化**：提供可视化工具，将产品装配工艺转化为计算机可识别的结构化数据（JSON + 图片）。
3. **算法管理**：统一管理视觉算法包，提供版本控制与分发服务。
4. **数据闭环**：收集C端检测日志与图片，实现质量追溯。

### 技术架构

该项目基于 **RuoYi-Vue**（前后端分离版本）进行二次开发，采用Spring Boot、Vue 3、MySQL、Redis、MinIO等技术栈构建。

详细的设计文档请参考[设计规范文档](design_spec_v1.md)。

## 核心模块

### 系统管理模块

基于若依原生功能，管理用户、角色、菜单、部门。适配创建专门的"C端设备"或"工位账号"用于API鉴权。

### MOM通信模块

作为与MOM系统的唯一交互窗口：

1. 通过MOM调用的HTTP接口接收任务，包含JSON数据和PDF下载链接
2. 异步处理请求，立即存入数据库(`sys_mom_log`)并返回成功，通过MQ解析PDF和生成本地工单避免阻塞MOM调用
3. 通过定时任务或触发式扫描"待上报队列"，将检测结果推送到MOM指定接口

### 工艺信息模块

连接MOM任务与视觉算法：

- `Product`(产品)：对应MOM中的物料/产品型号(PID)
- `CraftPackage`(工艺包)：一个产品对应一个工艺包，包含版本号
- `CraftStep`(工艺步骤)：具体的装配动作
- 具有基于Fabric.js的画布编辑功能，用于ROI选择和配置

### 算法管理模块

统一管理视觉算法包，提供版本控制：

- 使用预签名PUT URL模式上传算法包
- 后台异步下载并解压包进行验证
- 将文件存储在MinIO中，数据库记录版本号、MD5和对象键

### 工单信息模块

将MOM推送的任务转换为本地 `WorkOrder`记录，并进行状态流转：

- `PENDING`(待领取) → `PROCESSING`(进行中) → `COMPLETED`(完成) / `FAILED`(异常)
- 客户端登录后获取相关的 `PENDING`工单

### 检测结果模块

处理高并发写入：

- 客户端请求上传凭证并直接PUT图片到MinIO
- 提交包含JSON数据和图片路径(对象键)的结果
- 后端接收轻量级JSON并存入MQ或数据库
- 图片绕过后端减少IO压力

## API接口【参考设计规范】

## 开发规范

### 目录结构

本项目采用前后端分离架构，目录结构设计如下：

BS/
├── docker/                 # 容器化部署配置
│   ├── mysql/              # MySQL 配置与数据初始化
│   ├── redis/              # Redis 配置
│   ├── nginx/              # Nginx 配置与静态资源
│   └── minio/              # MinIO 数据存储配置
├── src/                    # 项目源码
│   ├── backend/            # 后端源码 (基于 RuoYi-Vue)
│   └── ruoyi-ui/           # 前端源码 (Vue3 + Element Plus)
├── MOM/                    # MOM 系统对接文档与协议规范
├── design_spec_v1.md       # 详细设计规范文档
└── README.md               # 项目说明文档

**业务开发说明**：

* 后端业务代码应统一在 `ruoyi-business` 模块中开发，避免污染 `ruoyi-system` 或 `ruoyi-admin`。
* `ruoyi-business` 模块内部建议参考以下分包结构：
  ```text
  com.ruoyi.business
  ├── controller  # 控制层
  ├── domain      # 实体类
  ├── service     # 业务逻辑层
  ├── mapper      # 数据持久层
  └── mom         # MOM WebService 对接逻辑
  ```

### 命名规范

- **Java类名**：PascalCase（如 `CraftService`）
- **方法名**：camelCase（如 `getTaskById`）
- **数据库表**：snake_case并加模块前缀：
  - 系统表：`sys_`
  - 工艺表：`biz_craft_`
  - 工单表：`biz_order_`
  - 日志表：`biz_detect_log_`

### 数据库设计规约

- 每个表必须包含 `create_time`, `update_time`, `create_by`, `update_by`, `remark`字段（继承自RuoYi的 `BaseEntity`）
- 严禁使用外键，关联关系在代码层面维护
- 图片和文件字段只存储MinIO相对路径或URL，不存储二进制流

## 部署建议

1. 建议使用Docker部署MinIO，并配置Nginx代理图片访问路径
2. C端与服务端之间建议使用HTTP通信

## 快速开始

详细的设置说明和开发指南可以在[设计规范文档](design_spec_v1.md)中找到。

## Todolist

* [ ] 评审项目设计规范
* [ ] 开发组checkout项目，初始化项目目录结构，上传ruoyi源码（前后端）
* [ ] 完成docker-compose编辑测试，实现B/S、Minio、MySQL、[Redis、RabbitMQ]全容器和运行
* [ ] 定义业务数据模型，设计数据表结构
* [ ] 定义CS业务接口与数据模型，开发并测试接口
* [ ] 定义BS业务接口与数据模型，开发并测试接口
* [ ] 开发B端用户交互页面
* [ ] 测试MOM通信
* [ ] 测试算法导入
* [ ] 联调测试
