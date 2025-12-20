DROP TABLE IF EXISTS craft;


# 工艺信息表
create table biz_craft(
    id int primary key auto_increment comment '主键',
    code varchar(50) comment '编码',
    version varchar(50) comment '版本',
    `desc` text comment '说明',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工艺信息表';

# 工序信息表
create table biz_process(
    id int primary key auto_increment comment '主键',
    code varchar(50) comment '编码',
    name varchar(50) comment '名称',
    `desc` text comment '说明',
    guide_granularity tinyint comment '引导粒度(1为工序，2为工步)',
    craft_id int comment '工艺id',
    craft_code varchar(50) comment '所属工艺编码',
    guide_map_url varchar(255) comment '引导图url',
    process_material_info text comment '工序材料信息(JSON形式)',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工序信息表';

# 工步信息表
create table biz_step(
    id int primary key auto_increment comment '主键',
    code varchar(50) comment '编码',
    name varchar(50) comment '名称',
    content varchar(255) comment '内容',
    guide_map_url varchar(255) comment '引导图url',
    type tinyint comment '工步类型(1为工序工步，2为派单工步)',
    type_id int comment '工序id(工序id或派单id)',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工步信息表';

# 订单表
create table biz_order(
    id int primary key auto_increment comment '主键',
    order_code varchar(50) comment '生产订单编码',
    product_batch varchar(50) comment '产品批次',
    material_code varchar(50) comment '物料编码',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '订单表';

# 工单表
create table biz_work_order(
    id int primary key auto_increment comment '主键',
    work_order_code varchar(50) comment '工单编码',
    work_order_quantity int comment '工单数量',
    craft_code varchar(50) comment '工艺编码',
    craft_version varchar(50) comment '工艺版本',
    status tinyint comment '工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)',
    order_id int comment '订单id',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工单表';

# 派单表
create table biz_dispatch_order(
    id int primary key auto_increment comment '主键',
    process_code varchar(50) comment '工序编码',
    process_name varchar(50) comment '工序名称',
    dispatch_quantity int comment '派单数量',
    start_time datetime comment '计划开始时间',
    end_time datetime comment '计划结束时间',
    worker_code varchar(50) comment '装配工人编码',
    worker_name varchar(50) comment '装配工人姓名',
    order_id int comment '订单id',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '派单表';