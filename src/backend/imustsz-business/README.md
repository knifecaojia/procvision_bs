# 核心业务模块 (ruoyi-business)

本模块存放项目特定的所有业务逻辑，是开发工作的重点区域。

## 功能模块
1.  **MOM 对接**: WebService 客户端/服务端实现
2.  **工艺管理**: 工艺包解析、存储、版本管理
3.  **工单管理**: 生产任务的接收、分发与状态流转
4.  **检测结果**: 检测数据的上报、存储与统计

## 建议包结构
*   `com.ruoyi.business.controller`
*   `com.ruoyi.business.service`
*   `com.ruoyi.business.mapper`
*   `com.ruoyi.business.domain`
*   `com.ruoyi.business.mom`
