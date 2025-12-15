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
将MOM推送的任务转换为本地`WorkOrder`记录，并进行状态流转：
- `PENDING`(待领取) → `PROCESSING`(进行中) → `COMPLETED`(完成) / `FAILED`(异常)
- 客户端登录后获取相关的`PENDING`工单

### 检测结果模块
处理高并发写入：
- 客户端请求上传凭证并直接PUT图片到MinIO
- 提交包含JSON数据和图片路径(对象键)的结果
- 后端接收轻量级JSON并存入MQ或数据库
- 图片绕过后端减少IO压力

## API接口

所有接口统一使用`/api/v1`前缀。

### 面向MOM系统
- `POST /mom/task/push` - 接收来自MOM的生产任务（含PDF链接和物料信息）
- `POST /mom/msg/notify` - 接收来自MOM的临时通知消息

### 面向客户端
- `POST /client/auth/login` - 设备/工位登录获取Token
- `GET /client/task/list` - 获取当前工位的待处理任务列表
- `GET /client/craft/url/{pid}` - 获取工艺包的MinIO下载链接（预签名GET）
- `GET /client/algo/check_update` - 检查算法更新（预签名GET）
- `GET /client/file/presigned/put` - 获取文件上传凭证（预签名PUT URL）
- `POST /client/result/submit` - 提交检测结果（仅JSON，包含图像Key）
- `POST /client/log/error` - 上报设备运行错误日志

## 开发规范

### 目录结构
业务代码应在`ruoyi-system`或新建的`ruoyi-business`模块中开发，而不是在`ruoyi-admin`中：

```
ruoyi-business/
├── src/main/java/com/ruoyi/business/
│   ├── controller/    # 控制层
│   ├── domain/        # 实体类
│   ├── service/       # 业务逻辑层
│   ├── mapper/        # 数据持久层
│   └── mom/           # 专门处理 MOM 对接的逻辑
```

### 命名规范
- **Java类名**：PascalCase（如`CraftService`）
- **方法名**：camelCase（如`getTaskById`）
- **数据库表**：snake_case并加模块前缀：
  - 系统表：`sys_`
  - 工艺表：`biz_craft_`
  - 工单表：`biz_order_`
  - 日志表：`biz_detect_log_`

### 数据库设计规约
- 每个表必须包含`create_time`, `update_time`, `create_by`, `update_by`, `remark`字段（继承自RuoYi的`BaseEntity`）
- 严禁使用外键，关联关系在代码层面维护
- 图片和文件字段只存储MinIO相对路径或URL，不存储二进制流

## 部署建议

1. 建议使用Docker部署MinIO，并配置Nginx代理图片访问路径
2. C端与服务端之间建议使用HTTPS通信保证安全性
3. 对于大量检测图片（例如每日10万+），考虑对`biz_detect_log`表按月分表或迁移至时序数据库/ElasticSearch

## 快速开始

详细的设置说明和开发指南可以在[设计规范文档](design_spec_v1.md)中找到。