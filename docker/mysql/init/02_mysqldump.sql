-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 39.104.202.123    Database: jz
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_BLOB_TRIGGERS`
--

LOCK TABLES `QRTZ_BLOB_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CALENDARS`
--

LOCK TABLES `QRTZ_CALENDARS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CRON_TRIGGERS`
--

LOCK TABLES `QRTZ_CRON_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_FIRED_TRIGGERS`
--

LOCK TABLES `QRTZ_FIRED_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_JOB_DETAILS`
--

LOCK TABLES `QRTZ_JOB_DETAILS` WRITE;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_LOCKS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_LOCKS`
--

LOCK TABLES `QRTZ_LOCKS` WRITE;
/*!40000 ALTER TABLE `QRTZ_LOCKS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_LOCKS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

LOCK TABLES `QRTZ_PAUSED_TRIGGER_GRPS` WRITE;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SCHEDULER_STATE`
--

LOCK TABLES `QRTZ_SCHEDULER_STATE` WRITE;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPROP_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPROP_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGERS`
--

LOCK TABLES `QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_algorithm`
--

DROP TABLE IF EXISTS `biz_algorithm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_algorithm` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) DEFAULT NULL COMMENT '算法编码',
  `name` varchar(50) DEFAULT NULL COMMENT '算法名称',
  `size` varchar(10) DEFAULT NULL COMMENT '算法大小(单位MB)',
  `version` varchar(50) DEFAULT NULL COMMENT '算法版本',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '算法下载地址',
  `desc` varchar(255) DEFAULT NULL COMMENT '算法描述',
  `object_name` varchar(100) DEFAULT NULL COMMENT 'minio上的算法objectName',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='算法表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_algorithm`
--

LOCK TABLES `biz_algorithm` WRITE;
/*!40000 ALTER TABLE `biz_algorithm` DISABLE KEYS */;
INSERT INTO `biz_algorithm` VALUES (1,NULL,'shijue','0.01','1.0',NULL,NULL,'2026-01-291f218552-03ea-41de-89c5-ae9b6baa8ba3',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `biz_algorithm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_craft`
--

DROP TABLE IF EXISTS `biz_craft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_craft` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `production_order_no` varchar(50) DEFAULT NULL COMMENT '生产订单编号',
  `code` varchar(50) DEFAULT NULL COMMENT '编码',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `version` varchar(50) DEFAULT NULL COMMENT '版本',
  `status` tinyint DEFAULT '1' COMMENT '工艺状态(1待处理，2未配置引导信息，3未配置检测算法，4已就绪)',
  `desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '说明',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工艺信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_craft`
--

LOCK TABLES `biz_craft` WRITE;
/*!40000 ALTER TABLE `biz_craft` DISABLE KEYS */;
INSERT INTO `biz_craft` VALUES (1,'100002346','JZ4.932.09296060GY-TX03','电源模块工艺规程','D.2',4,'testXXX','2026-01-29 17:15:52',NULL,'admin',NULL,NULL),(2,'100002357','JZ4.932.09296060GY-TX04','电源模块工艺规程','D.2',1,'testXXX','2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(3,NULL,'JZ4.932.09296060GY-TX05','电源模块工艺规程','D.2',1,'testXXX','2026-01-29 17:26:56',NULL,'admin',NULL,NULL),(4,NULL,'JZ4.932.09296060GY-TX06','电源模块工艺规程','D.2',1,'testXXX','2026-01-29 17:27:04',NULL,'admin',NULL,NULL),(5,'100002345','JZ4.932.09296060GY-TX01','电源模块工艺规程','D.2',1,'testXXX','2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(6,'100002348','JZ4.932.09296060GY-TX11','电源模块工艺规程','D.2',4,'testXXX','2026-03-08 15:15:51',NULL,'admin',NULL,NULL);
/*!40000 ALTER TABLE `biz_craft` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_data_collection`
--

DROP TABLE IF EXISTS `biz_data_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_data_collection` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `image_path` varchar(255) DEFAULT NULL COMMENT 'Minio Key',
  `data` text COMMENT '数据',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(10) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(10) DEFAULT NULL COMMENT '更新这',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据采集表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_data_collection`
--

LOCK TABLES `biz_data_collection` WRITE;
/*!40000 ALTER TABLE `biz_data_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_data_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_process`
--

DROP TABLE IF EXISTS `biz_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_process` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) DEFAULT NULL COMMENT '编码',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `algorithm_id` int DEFAULT NULL COMMENT '算法id',
  `desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '说明',
  `guide_granularity` tinyint DEFAULT NULL COMMENT '指导粒度(1为工序，2为工步)',
  `craft_id` int DEFAULT NULL COMMENT '工艺id',
  `craft_code` varchar(50) DEFAULT NULL COMMENT '所属工艺编码',
  `guide_map_url` varchar(255) DEFAULT NULL COMMENT '指导图url',
  `process_material_info` text COMMENT '工序材料信息(JSON形式)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工序信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_process`
--

LOCK TABLES `biz_process` WRITE;
/*!40000 ALTER TABLE `biz_process` DISABLE KEYS */;
INSERT INTO `biz_process` VALUES (1,'10','准备',1,'按具体清单准备XXXXX',NULL,1,'JZ4.932.09296060GY-TX03',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929403\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-01-29 17:15:52',NULL,'admin',NULL,NULL),(4,'10','准备',NULL,'按具体清单准备XXXXX',NULL,2,'JZ4.932.09296060GY-TX04',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929403\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(5,'20','装配',NULL,'按工艺内容装配XXX',NULL,2,'JZ4.932.09296060GY-TX04',NULL,'[{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(6,'30','装配',NULL,'装配信息描述XXX',NULL,2,'JZ4.932.09296060GY-TX04',NULL,'[{\"errorPreventionMark\":\"漏\",\"materialName\":\"螺钉\",\"materialNo\":\"1000000000034\",\"materialQuantity\":4,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(7,'10','准备',NULL,'按具体清单准备XXXXX',NULL,3,'JZ4.932.09296060GY-TX05',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929405\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-01-29 17:26:56',NULL,'admin',NULL,NULL),(8,'20','装配',NULL,'按工艺内容装配XXX',NULL,3,'JZ4.932.09296060GY-TX05',NULL,'[{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(9,'30','装配',NULL,'装配信息描述XXX',NULL,3,'JZ4.932.09296060GY-TX05',NULL,'[{\"errorPreventionMark\":\"漏\",\"materialName\":\"螺钉\",\"materialNo\":\"1000000000034\",\"materialQuantity\":4,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(10,'10','准备',NULL,'按具体清单准备XXXXX',NULL,4,'JZ4.932.09296060GY-TX06',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929403\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-01-29 17:27:04',NULL,'admin',NULL,NULL),(11,'20','装配',NULL,'按工艺内容装配XXX',NULL,4,'JZ4.932.09296060GY-TX06',NULL,'[{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(12,'30','装配',NULL,'装配信息描述XXX',NULL,4,'JZ4.932.09296060GY-TX06',NULL,'[{\"errorPreventionMark\":\"漏\",\"materialName\":\"螺钉\",\"materialNo\":\"1000000000034\",\"materialQuantity\":4,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(13,'10','准备',NULL,'按具体清单准备XXXXX',NULL,5,'JZ4.932.09296060GY-TX01',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929403\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(14,'20','装配',NULL,'按工艺内容装配XXX',NULL,5,'JZ4.932.09296060GY-TX01',NULL,'[{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(15,'30','装配',NULL,'装配信息描述XXX',NULL,5,'JZ4.932.09296060GY-TX01',NULL,'[{\"errorPreventionMark\":\"漏\",\"materialName\":\"螺钉\",\"materialNo\":\"1000000000034\",\"materialQuantity\":4,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"反\",\"materialName\":\"安装板\",\"materialNo\":\"1000000000001\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]','2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(16,'10','准备',1,'按具体清单准备XXXXX',NULL,6,'JZ4.932.09296060GY-TX11',NULL,'[{\"errorPreventionMark\":\"错\",\"materialName\":\"盖板\",\"materialNo\":\"9000000929403\",\"materialQuantity\":2,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"错\",\"materialName\":\"腔体\",\"materialNo\":\"3000000929433\",\"materialQuantity\":2,\"materialUnit\":\"件\"}]','2026-03-08 15:15:51',NULL,'admin',NULL,NULL);
/*!40000 ALTER TABLE `biz_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_process_record`
--

DROP TABLE IF EXISTS `biz_process_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_process_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_code` varchar(50) DEFAULT NULL COMMENT '工单编码',
  `process_code` varchar(50) DEFAULT NULL COMMENT '工序编码',
  `step_code` varchar(10) DEFAULT NULL COMMENT '工步编码',
  `step_name` varchar(50) DEFAULT NULL COMMENT '工步名称',
  `step_status` tinyint DEFAULT NULL COMMENT '工步状态(1未完成，2已完成)',
  `image_path` varchar(255) DEFAULT NULL COMMENT 'Minio Key',
  `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '算法返回的数据(json)',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='过程记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_process_record`
--

LOCK TABLES `biz_process_record` WRITE;
/*!40000 ALTER TABLE `biz_process_record` DISABLE KEYS */;
INSERT INTO `biz_process_record` VALUES (1,'100002345-02','10','1','工步1',2,'2026-01-3044406f06-7715-4f6a-9351-3697991b5136',NULL,'2026-03-08 12:44:38',NULL,NULL,NULL,NULL,NULL),(3,'100002345-02','20','1','工步1',2,'2026-01-3044406f06-7715-4f6a-9351-3697991b5136',NULL,'2026-03-08 12:48:07',NULL,NULL,NULL,NULL,NULL),(4,'100002345-02','10','2','工步2',2,'2026-01-3044406f06-7715-4f6a-9351-3697991b5136',NULL,'2026-03-08 12:48:52',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `biz_process_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_step`
--

DROP TABLE IF EXISTS `biz_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_step` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) DEFAULT NULL COMMENT '编码',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `guide_map_url` varchar(255) DEFAULT NULL COMMENT '引导图url',
  `process_id` int DEFAULT NULL COMMENT '工序id',
  `guide_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '引导信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工步信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_step`
--

LOCK TABLES `biz_step` WRITE;
/*!40000 ALTER TABLE `biz_step` DISABLE KEYS */;
INSERT INTO `biz_step` VALUES (1,'1','工步1','工步1内容描述信息','2026-01-2902bcd72a-edb1-4050-8033-0c91e6c66708',1,'[{\"label\":\"connector\",\"posList\":[{\"x\":1946,\"y\":2896,\"width\":555,\"height\":393,\"remark\":\"左\"}]}]','2026-01-29 17:15:53',NULL,'admin',NULL,NULL),(2,'2','工步2','工步2内容描述XXXX','2026-01-305cb1a3b1-073a-45f4-9965-880bc6c27a55',1,'[{\"label\":\"1\",\"posList\":[{\"x\":735,\"y\":239,\"width\":151,\"height\":290,\"remark\":\"1\"}]}]','2026-01-29 17:15:53',NULL,'admin',NULL,NULL),(3,'3','工步3','工步3内容描述XXXX','2026-01-3044406f06-7715-4f6a-9351-3697991b5136',1,'[{\"label\":\"3\",\"posList\":[{\"x\":608,\"y\":692,\"width\":380,\"height\":252,\"remark\":\"3\"}]}]','2026-01-29 17:15:53',NULL,'admin',NULL,NULL),(10,'1','工步1','工步1内容描述信息',NULL,4,NULL,'2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(11,'2','工步2','工步2内容描述XXXX',NULL,4,NULL,'2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(12,'3','工步3','工步3内容描述XXXX',NULL,4,NULL,'2026-01-29 17:26:48',NULL,'admin',NULL,NULL),(13,'1','工步1','工步内容XXX',NULL,5,NULL,'2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(14,'2','工步2','工步内容2描述XXX',NULL,5,NULL,'2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(15,'3','工步3','工步内容3XXXXX',NULL,5,NULL,'2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(16,'1','工步1 ','工步1内容描述Xxx',NULL,6,NULL,'2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(17,'2','工步2','工步2内容描述xxx',NULL,6,NULL,'2026-01-29 17:26:49',NULL,'admin',NULL,NULL),(18,'3','工步3','工步3内容描述',NULL,6,NULL,'2026-01-29 17:26:50',NULL,'admin',NULL,NULL),(19,'1','工步1','工步1内容描述信息',NULL,7,NULL,'2026-01-29 17:26:56',NULL,'admin',NULL,NULL),(20,'2','工步2','工步2内容描述XXXX',NULL,7,NULL,'2026-01-29 17:26:56',NULL,'admin',NULL,NULL),(21,'3','工步3','工步3内容描述XXXX',NULL,7,NULL,'2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(22,'1','工步1','工步内容XXX',NULL,8,NULL,'2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(23,'2','工步2','工步内容2描述XXX',NULL,8,NULL,'2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(24,'3','工步3','工步内容3XXXXX',NULL,8,NULL,'2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(25,'1','工步1 ','工步1内容描述Xxx',NULL,9,NULL,'2026-01-29 17:26:57',NULL,'admin',NULL,NULL),(26,'2','工步2','工步2内容描述xxx',NULL,9,NULL,'2026-01-29 17:26:58',NULL,'admin',NULL,NULL),(27,'3','工步3','工步3内容描述',NULL,9,NULL,'2026-01-29 17:26:58',NULL,'admin',NULL,NULL),(28,'1','工步1','工步1内容描述信息',NULL,10,NULL,'2026-01-29 17:27:04',NULL,'admin',NULL,NULL),(29,'2','工步2','工步2内容描述XXXX',NULL,10,NULL,'2026-01-29 17:27:04',NULL,'admin',NULL,NULL),(30,'3','工步3','工步3内容描述XXXX',NULL,10,NULL,'2026-01-29 17:27:04',NULL,'admin',NULL,NULL),(31,'1','工步1','工步内容XXX',NULL,11,NULL,'2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(32,'2','工步2','工步内容2描述XXX',NULL,11,NULL,'2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(33,'3','工步3','工步内容3XXXXX',NULL,11,NULL,'2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(34,'1','工步1 ','工步1内容描述Xxx',NULL,12,NULL,'2026-01-29 17:27:05',NULL,'admin',NULL,NULL),(35,'2','工步2','工步2内容描述xxx',NULL,12,NULL,'2026-01-29 17:27:06',NULL,'admin',NULL,NULL),(36,'3','工步3','工步3内容描述',NULL,12,NULL,'2026-01-29 17:27:06',NULL,'admin',NULL,NULL),(37,'1','工步1','工步1内容描述信息',NULL,13,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(38,'2','工步2','工步2内容描述XXXX',NULL,13,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(39,'3','工步3','工步3内容描述XXXX',NULL,13,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(40,'1','工步1','工步内容XXX',NULL,14,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(41,'2','工步2','工步内容2描述XXX',NULL,14,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(42,'3','工步3','工步内容3XXXXX',NULL,14,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(43,'1','工步1 ','工步1内容描述Xxx',NULL,15,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(44,'2','工步2','工步2内容描述xxx',NULL,15,NULL,'2026-03-04 20:10:17',NULL,'admin',NULL,NULL),(45,'3','工步3','工步3内容描述',NULL,15,NULL,'2026-03-04 20:10:18',NULL,'admin',NULL,NULL),(46,'1','工步1','工步1内容描述信息','2026-03-08f5f9913e-8238-49a7-a1d9-ffc47b98caa7',16,'[{\"label\":\"2\",\"posList\":[{\"x\":725,\"y\":247,\"width\":159,\"height\":290,\"remark\":\"2\"}]}]','2026-03-08 15:15:51',NULL,'admin',NULL,NULL);
/*!40000 ALTER TABLE `biz_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_work_order`
--

DROP TABLE IF EXISTS `biz_work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_work_order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_code` varchar(50) DEFAULT NULL COMMENT '工单编码',
  `craft_code` varchar(50) DEFAULT NULL COMMENT '工艺编码',
  `craft_version` varchar(50) DEFAULT NULL COMMENT '工艺版本',
  `status` tinyint DEFAULT NULL COMMENT '工作状态(-1：引导资源未就绪；-2：检测资源未就绪；1：待派单，2：进行中，3：已完成，4：手工通过)',
  `process_code` varchar(50) DEFAULT NULL COMMENT '工序编码',
  `process_name` varchar(50) DEFAULT NULL COMMENT '工序名称',
  `start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '计划结束时间',
  `guide_map_url` varchar(255) DEFAULT NULL COMMENT '引导图url',
  `worker_code` varchar(50) DEFAULT NULL COMMENT '装配工人编码',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '装配工人姓名',
  `prod_order_no` varchar(50) DEFAULT NULL COMMENT '生产订单号',
  `prod_batch_no` varchar(50) DEFAULT NULL COMMENT '生产批次号',
  `project_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '项目号',
  `material_no` varchar(255) DEFAULT NULL COMMENT '材料编号',
  `material_name` varchar(50) DEFAULT NULL COMMENT '材料名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_work_order`
--

LOCK TABLES `biz_work_order` WRITE;
/*!40000 ALTER TABLE `biz_work_order` DISABLE KEYS */;
INSERT INTO `biz_work_order` VALUES (6,'100002346-01','JZ4.932.09296060GY-TX03','D.2',1,'10','连接器安装','2025-12-09 08:00:00','2025-12-09 11:30:00',NULL,'0876','王五','100002345','100002346','K8989',' 9000000929401','连接器',NULL,NULL,NULL,NULL,NULL),(8,'100002357-01','JZ4.932.09296060GY-TX04','D.2',-2,'10','准备','2025-12-09 08:00:00','2025-12-09 11:30:00',NULL,'0901','赵一','100002357','100002357','K8989',' 9000000929401','连接器',NULL,NULL,NULL,NULL,NULL),(17,'100002345-01','JZ4.932.09296060GY-TX01','D.2',-2,'10','准备','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07488','张三','100002345',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,'100002345-01','JZ4.932.09296060GY-TX01','D.2',-2,'20','装配','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07698','李四','100002345',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,'100002345-02','JZ4.932.09296060GY-TX01','D.2',2,'10','准备','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07488','张三','100002345',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(20,'100002345-02','JZ4.932.09296060GY-TX01','D.2',2,'20','装配','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07698','李四','100002345',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,'100002348-01','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07488','张三','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,'100002348-02','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-09 08:30:00','2025-12-09 12:00:00',NULL,'07488','张三','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,'100002348-03','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-09 13:00:00','2025-12-09 16:30:00',NULL,'07512','王五','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,'100002348-04','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-09 13:00:00','2025-12-09 16:30:00',NULL,'07512','王五','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,'100002348-05','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-10 08:30:00','2025-12-10 12:00:00',NULL,'07589','孙七','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,'100002348-06','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-10 08:30:00','2025-12-10 12:00:00',NULL,'07589','孙七','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,'100002348-07','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-10 13:00:00','2025-12-10 16:30:00',NULL,'07499','吴九','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(28,'100002348-08','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-10 13:00:00','2025-12-10 16:30:00',NULL,'07499','吴九','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(29,'100002348-09','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-11 08:30:00','2025-12-11 12:00:00',NULL,'07521','钱十一','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(30,'100002348-10','JZ4.932.09296060GY-TX11','D.2',1,'10','准备','2025-12-11 08:30:00','2025-12-11 12:00:00',NULL,'07521','钱十一','100002348',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `biz_work_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (1,'craft','工艺信息表',NULL,NULL,'Craft','crud','element-plus','com.ruoyi.imustsz','craft','craft','工艺信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-18 11:45:44','','2025-12-18 12:09:10',NULL),(2,'process','工序信息表',NULL,NULL,'Process','crud','element-plus','com.ruoyi.imustsz','process','process','工序信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-18 12:42:31','','2025-12-18 12:43:20',NULL),(3,'biz_step','工步信息表',NULL,NULL,'BizStep','crud','element-plus','com.imustsz.craft','craft','step','工步信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-19 07:20:44','','2025-12-19 07:22:32',NULL),(7,'biz_work_order','工单表',NULL,NULL,'BizWorkOrder','crud','element-plus','com.imustsz.order','wo','workOrder','工单','imustsz','0','/','{\"parentMenuId\":2004}','admin','2025-12-22 08:27:49','','2025-12-22 08:35:26',NULL),(8,'biz_algorithm','算法表',NULL,NULL,'BizAlgorithm','crud','element-plus','com.imustsz.algorithm','algorithm','algorithm','算法','imustsz','0','/','{\"parentMenuId\":2007}','admin','2025-12-22 09:36:57','','2025-12-22 10:51:10',NULL),(9,'biz_process_record','过程记录表',NULL,NULL,'BizProcessRecord','crud','element-plus','com.imustsz.process','process','record','过程记录','imustsz','0','/','{}','admin','2025-12-22 11:45:02','','2025-12-22 11:46:21',NULL),(10,'biz_data_collection','数据采集表',NULL,NULL,'BizDataCollection','crud','element-plus','com.imustsz.collect','collection','data','数据采集','imustsz','0','/','{\"parentMenuId\":2012}','admin','2026-01-20 03:24:09','','2026-01-20 03:25:25',NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (1,1,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(2,1,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(3,1,'version','版本','varchar(50)','String','version','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(4,1,'brief','说明','text','String','brief','0','0','0','1','1','1','1','EQ','textarea','',4,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(5,2,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-18 12:42:31','','2025-12-18 12:43:20'),(6,2,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(7,2,'name','名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',3,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(8,2,'brief','说明','text','String','brief','0','0','0','1','1','1','1','EQ','textarea','',4,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(9,2,'guide_granularity','指导粒度(1为工序，2为工步)','tinyint','Long','guideGranularity','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(10,2,'craft_id','工艺id','int','Long','craftId','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(11,2,'craft_code','所属工艺编码','varchar(50)','String','craftCode','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(12,2,'guide_map_url','指导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(13,2,'process_material_info','工序材料信息(JSON形式)','text','String','processMaterialInfo','0','0','0','1','1','1','1','EQ','textarea','',9,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(14,3,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(15,3,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(16,3,'name','名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',3,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(17,3,'content','内容','varchar(255)','String','content','0','0','0','1','1','1','1','EQ','editor','',4,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(18,3,'guide_map_url','引导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(19,3,'process_id','工序id','int','Long','processId','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(20,3,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(21,3,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',8,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(22,3,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(23,3,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(24,3,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',11,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(60,7,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(61,7,'work_order_code','工单编码','varchar(50)','String','workOrderCode','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(62,7,'work_order_quantity','工单数量','int','Long','workOrderQuantity','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(63,7,'craft_code','工艺编码','varchar(50)','String','craftCode','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(64,7,'craft_version','工艺版本','varchar(50)','String','craftVersion','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(65,7,'status','工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)','tinyint','Long','status','0','0','0','1','1','1','1','EQ','radio','',6,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(66,7,'process_code','工序编码','varchar(50)','String','processCode','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(67,7,'process_name','工序名称','varchar(50)','String','processName','0','0','0','1','1','1','1','LIKE','input','',8,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:27'),(68,7,'dispatch_quantity','派单数量','int','Long','dispatchQuantity','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:27'),(69,7,'start_time','计划开始时间','datetime','Date','startTime','0','0','0','1','1','1','1','EQ','datetime','',10,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(70,7,'end_time','计划结束时间','datetime','Date','endTime','0','0','0','1','1','1','1','EQ','datetime','',11,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(71,7,'guide_map_url','引导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',12,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(72,7,'worker_code','装配工人编码','varchar(50)','String','workerCode','0','0','0','1','1','1','1','EQ','input','',13,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(73,7,'worker_name','装配工人姓名','varchar(50)','String','workerName','0','0','0','1','1','1','1','LIKE','input','',14,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(74,7,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',15,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(75,7,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',16,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(76,7,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',17,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(77,7,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',18,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(78,7,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',19,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(79,8,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(80,8,'name','算法名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',2,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(81,8,'version','算法版本','varchar(50)','String','version','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(82,8,'desc','算法描述','varchar(255)','String','desc','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(83,8,'object_name','minio上的算法objectName','varchar(100)','String','objectName','0','0','0','1','1','1','1','LIKE','input','',5,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(84,8,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',6,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(85,8,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(86,8,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(87,8,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(88,8,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(89,9,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(90,9,'work_order_code','工单编码','varchar(50)','String','workOrderCode','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(91,9,'step_id','工步id','int','Long','stepId','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(92,9,'step_status','工步状态','varchar(10)','String','stepStatus','0','0','0','1','1','1','1','EQ','radio','',4,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(93,9,'image_path','Minio Key','varchar(255)','String','imagePath','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(94,9,'data','拓展数据','text','String','data','0','0','0','1','1','1','1','EQ','textarea','',6,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(95,9,'submit_time','提交时间','datetime','Date','submitTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(96,9,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',8,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(97,9,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',9,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(98,9,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(99,9,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',11,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(100,9,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',12,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(101,10,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-01-20 03:24:09','','2026-01-20 03:25:25'),(102,10,'image_path','Minio Key','varchar(255)','String','imagePath','0','0','0','1','1','1','1','EQ','input','',2,'admin','2026-01-20 03:24:09','','2026-01-20 03:25:25'),(103,10,'data','数据','text','String','data','0','0','0','1','1','1','1','EQ','textarea','',3,'admin','2026-01-20 03:24:09','','2026-01-20 03:25:25');
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2025-12-18 08:50:48','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2025-12-18 08:50:48','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2025-12-18 08:50:48','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','true','Y','admin','2025-12-18 08:50:48','',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2025-12-18 08:50:48','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2025-12-18 08:50:48','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2025-12-18 08:50:48','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2025-12-18 08:50:48','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','XX科技',0,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2026-01-19 12:08:59'),(101,100,'0,100','装备制造中心',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:51:26'),(103,101,'0,100,101','装备制造中心',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:51:51'),(104,101,'0,100,101','精密制造专业部',2,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:52:09'),(105,101,'0,100,101','工艺技术部产品工艺主管室',3,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:52:29'),(106,101,'0,100,101','工艺技术总装调测工艺技术室',4,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:55:49'),(107,101,'0,100,101','工艺技术部工艺技术体系室',5,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:53:10');
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2025-12-18 08:50:46','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2025-12-18 08:50:46','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2025-12-18 08:50:46','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2025-12-18 08:50:46','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2025-12-18 08:50:47','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2025-12-18 08:50:47','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2025-12-18 08:50:47','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2025-12-18 08:50:47','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2025-12-18 08:50:47','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2025-12-18 08:50:47','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2025-12-18 08:50:47','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2025-12-18 08:50:47','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2025-12-18 08:50:47','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2025-12-18 08:50:47','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2025-12-18 08:50:47','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2025-12-18 08:50:47','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2025-12-18 08:50:47','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2025-12-18 08:50:47','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2025-12-18 08:50:47','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2025-12-18 08:50:47','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2025-12-18 08:50:47','',NULL,'停用状态');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2025-12-18 08:50:46','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2025-12-18 08:50:46','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2025-12-18 08:50:46','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2025-12-18 08:50:46','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2025-12-18 08:50:46','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2025-12-18 08:50:46','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2025-12-18 08:50:46','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2025-12-18 08:50:46','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2025-12-18 08:50:46','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2025-12-18 08:50:46','',NULL,'登录状态列表');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2025-12-18 08:50:49','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2025-12-18 08:50:49','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2025-12-18 08:50:49','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2014 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,6,'system',NULL,'','',1,0,'M','0','0','','system','admin','2025-12-18 08:50:37','admin','2026-01-20 05:53:13','系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','1','1','','monitor','admin','2025-12-18 08:50:37','admin','2025-12-22 12:15:52','系统监控目录'),(3,'系统工具',0,6,'tool',NULL,'','',1,0,'M','1','0','','tool','admin','2025-12-18 08:50:37','admin','2026-01-18 02:34:12','系统工具目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2025-12-18 08:50:37','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2025-12-18 08:50:37','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','1','0','system:menu:list','tree-table','admin','2025-12-18 08:50:37','admin','2025-12-28 11:11:58','菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2025-12-18 08:50:37','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','1','1','system:post:list','post','admin','2025-12-18 08:50:37','admin','2025-12-22 12:17:19','岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','1','1','system:dict:list','dict','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:52','字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','1','1','system:config:list','edit','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:40','参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','1','1','system:notice:list','message','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:30','通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2025-12-18 08:50:37','admin','2025-12-22 12:17:47','日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','1','monitor:online:list','online','admin','2025-12-18 08:50:37','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','1','monitor:job:list','job','admin','2025-12-18 08:50:37','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','1','monitor:druid:list','druid','admin','2025-12-18 08:50:37','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','1','monitor:server:list','server','admin','2025-12-18 08:50:37','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','1','monitor:cache:list','redis','admin','2025-12-18 08:50:37','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','1','monitor:cache:list','redis-list','admin','2025-12-18 08:50:37','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2025-12-18 08:50:37','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2025-12-18 08:50:37','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2025-12-18 08:50:37','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2025-12-18 08:50:37','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2025-12-18 08:50:37','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2025-12-18 08:50:38','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2025-12-18 08:50:38','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','1','system:post:query','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:39:58',''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','1','system:post:add','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:01',''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','1','system:post:edit','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:03',''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','1','system:post:remove','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:05',''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','1','system:post:export','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:08',''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','1','system:dict:query','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:14',''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','1','system:dict:add','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:17',''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','1','system:dict:edit','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:19',''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','1','system:dict:remove','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:21',''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','1','system:dict:export','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:24',''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','1','system:config:query','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:32',''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','1','system:config:add','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:35',''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','1','system:config:edit','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:37',''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','1','system:config:remove','#','admin','2025-12-18 08:50:38','admin','2026-01-19 10:40:40',''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','1','system:config:export','#','admin','2025-12-18 08:50:39','admin','2026-01-19 10:40:42',''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','1','system:notice:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','1','system:notice:add','#','admin','2025-12-18 08:50:39','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','1','system:notice:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','1','system:notice:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2025-12-18 08:50:39','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','1','monitor:online:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','1','monitor:online:batchLogout','#','admin','2025-12-18 08:50:39','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','1','monitor:online:forceLogout','#','admin','2025-12-18 08:50:39','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','1','monitor:job:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','1','monitor:job:add','#','admin','2025-12-18 08:50:39','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','1','monitor:job:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','1','monitor:job:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','1','monitor:job:changeStatus','#','admin','2025-12-18 08:50:39','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','1','monitor:job:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2025-12-18 08:50:39','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2025-12-18 08:50:39','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2025-12-18 08:50:39','',NULL,''),(2000,'工艺信息',0,1,'craft',NULL,NULL,'',1,0,'M','0','0',NULL,'clipboard','admin','2025-12-18 11:47:05','',NULL,''),(2001,'工艺信息管理',2000,0,'info_craft','craft/info_craft/index',NULL,'',1,0,'C','0','0','','build','admin','2025-12-18 11:49:01','admin','2026-01-19 10:26:18',''),(2004,'任务信息',0,2,'order',NULL,NULL,'',1,0,'M','0','0','','dashboard','admin','2025-12-18 11:56:35','admin','2026-01-18 03:52:28',''),(2005,'任务信息管理',2004,0,'info_order','order/info_order/index',NULL,'',1,0,'C','0','0','','chart','admin','2025-12-18 12:01:51','admin','2026-01-13 02:05:51',''),(2007,'算法管理',0,3,'algorithm',NULL,NULL,'',1,0,'M','0','0',NULL,'druid','admin','2025-12-22 10:12:47','',NULL,''),(2008,'算法信息管理',2007,0,'info_alg','algorithm/info_alg/index',NULL,'',1,0,'C','0','0','','enter','admin','2025-12-22 11:32:39','admin','2026-01-15 03:24:29',''),(2009,'过程记录',0,4,'process',NULL,NULL,'',1,0,'M','0','0','','component','admin','2025-12-22 11:48:30','admin','2026-01-18 03:52:36',''),(2010,'过程记录信息',2009,0,'record','process/record/index',NULL,'',1,0,'C','0','0',NULL,'color','admin','2025-12-22 11:49:13','',NULL,''),(2011,'数据采集',0,5,'collect',NULL,NULL,'',1,0,'M','0','0','','button','admin','2026-01-20 03:20:36','admin','2026-01-20 05:53:09',''),(2012,'数据采集管理',2011,0,'data','collection/data/index',NULL,'',1,0,'C','0','0','','list','admin','2026-01-20 03:22:53','admin','2026-01-20 03:40:02','');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',_binary '新版本内容','0','admin','2025-12-18 08:50:50','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',_binary '维护内容','0','admin','2025-12-18 08:50:50','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (1,'操作日志',9,'com.imustsz.web.controller.monitor.SysOperlogController.clean()','DELETE',1,'admin','装备制造中心','/monitor/operlog/clean','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-08 07:34:50',109),(2,'登录日志',9,'com.imustsz.web.controller.monitor.SysLogininforController.clean()','DELETE',1,'admin','装备制造中心','/monitor/logininfor/clean','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-08 07:34:53',109);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2025-12-18 08:50:35','',NULL,''),(2,'se','项目经理',2,'0','admin','2025-12-18 08:50:35','',NULL,''),(3,'hr','人力资源',3,'0','admin','2025-12-18 08:50:35','',NULL,''),(4,'user','普通员工',4,'0','admin','2025-12-18 08:50:35','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2025-12-18 08:50:36','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2025-12-18 08:50:36','admin','2026-01-19 11:32:53','普通角色'),(100,'demo','test',3,'1',1,1,'0','2','admin','2026-01-18 02:42:53','admin','2026-01-19 08:31:12','test'),(101,'崔','cui',3,'1',1,1,'0','2','admin','2026-01-19 12:06:31','',NULL,NULL);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,2000),(2,2001),(2,2004),(2,2005),(2,2007),(2,2008),(2,2009),(2,2010);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','管理员','00','ry@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-03-08 13:57:33','2025-12-18 08:50:35','admin','2025-12-18 08:50:35','','2025-12-28 03:54:18','管理员'),(2,105,'ry','用户','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-01-19 19:38:04','2025-12-18 08:50:35','admin','2025-12-18 08:50:35','admin','2025-12-28 03:55:16','测试员'),(100,200,'imustsz','imustsz','00','','15588888888','0','','$2a$10$Jp1B3h263pZeeQcRGyLZPOkhfgn8d6aurGMv4KpD3ZsUotDNQZoGO','0','2','',NULL,NULL,'admin','2025-12-28 03:50:15','',NULL,NULL),(101,104,'demo','test1','00','','','0','','$2a$10$By.md0vFCYENZptLQl201.CIl7O8z7I3URtCHT7bkR2a12.9YL1fa','0','0','127.0.0.1','2026-01-19 16:32:46',NULL,'admin','2026-01-18 02:43:38','admin','2026-01-19 08:32:33',NULL),(102,103,'崔均','cuijun','00','','','0','','$2a$10$p840iKpzu.B1KglZP8Bd2uTnz865LS2nXQNZ6vmdoL2QkUIeEJLBy','0','2','127.0.0.1','2026-01-19 20:07:17',NULL,'admin','2026-01-19 12:06:52','',NULL,NULL),(103,NULL,'1111','1111','00','2122@qq.com','112343211','0','','$2a$10$7X/AbWCt24H/tu9XMwDDLO/tu5Ky8cDpJVdgXJ7GYD5Z0tJcO1Hf2','0','2','',NULL,NULL,'admin','2026-01-24 09:40:53','',NULL,NULL),(104,NULL,'1111','1111','00','2122@qq.com','112343211','0','','$2a$10$roDmEMNHGZ5o8C/LgJylIeI7dcdvBewgMwXEMLVa.xB31pRhetDEa','0','2','',NULL,NULL,'admin','2026-01-24 09:41:27','',NULL,NULL),(105,101,'1111','1111','00','2122@qq.com','112343211','0','','$2a$10$HnR2dATS47yp6Or2W8K1qOaGRW8UCv9qgX5YqdDOnsgvpHjznxlLO','0','2','',NULL,NULL,'admin','2026-01-24 09:43:56','',NULL,NULL),(106,NULL,'1111','1111','00','2122@qq.com','112343211','0','','$2a$10$il7jCCQ.VzVpm3TkUFjy4uWwAVPtPR08BTDk.3fpZSSwnDIcc0SaO','0','2','',NULL,NULL,'admin','2026-01-24 09:58:29','',NULL,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(101,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-08 15:35:45
