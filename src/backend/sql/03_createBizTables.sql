DROP TABLE IF EXISTS biz_craft;
DROP TABLE IF EXISTS biz_process;
DROP TABLE IF EXISTS biz_step;
DROP TABLE IF EXISTS biz_order;
DROP TABLE IF EXISTS biz_work_order;
DROP TABLE IF EXISTS biz_dispatch_order;


# 工艺信息表
create table biz_craft(
    id int primary key auto_increment comment '主键',
    code varchar(50) comment '编码',
    name varchar(50) comment '名称',
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
    algorithm_code varchar(50) comment '算法编码',
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
    process_id int comment '工序id(工序id或派单id)',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工步信息表';

# 工单表
create table biz_work_order(
    id int primary key auto_increment comment '主键',
    work_order_code varchar(50) comment '工单编码',
    work_order_quantity int comment '工单数量',
    craft_code varchar(50) comment '工艺编码',
    craft_version varchar(50) comment '工艺版本',
    status tinyint comment '工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)',
    process_code varchar(50) comment '工序编码',
    process_name varchar(50) comment '工序名称',
    dispatch_quantity int comment '派单数量',
    start_time datetime comment '计划开始时间',
    end_time datetime comment '计划结束时间',
    guide_map_url varchar(255) comment '引导图url(minio key)',
    worker_code varchar(50) comment '装配工人编码',
    worker_name varchar(50) comment '装配工人姓名',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '工单表';

# 算法表
create table biz_algorithm(
    id int primary key auto_increment comment '主键',
    name varchar(50) comment '算法名称',
    version varchar(50) comment '算法版本',
    `desc` varchar(255) comment '算法描述',
    object_name varchar(100) comment 'minio上的算法objectName',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '算法表';

# 过程记录表
create table biz_process_record(
    id int primary key auto_increment comment '主键',
    work_order_code varchar(50) comment '工单编码',
    step_id int comment '工步id',
    step_status varchar(10) comment '工步状态',
    image_path varchar(255) comment 'Minio Key',
    data text comment '拓展数据',
    submit_time datetime comment '提交时间',
    created_time datetime comment '创建时间',
    updated_time datetime comment '更新时间',
    created_by varchar(50) comment '创建人',
    updated_by varchar(50) comment '更新人',
    remarks varchar(255) comment '备注'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '过程记录表';