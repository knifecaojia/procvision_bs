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
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='算法表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_algorithm`
--

LOCK TABLES `biz_algorithm` WRITE;
/*!40000 ALTER TABLE `biz_algorithm` DISABLE KEYS */;
INSERT INTO `biz_algorithm` VALUES (1,'z1','demo1','0.6','1.0','http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5','11111','Mamba.jpg',NULL,NULL,NULL,NULL,NULL),(2,'11','22','0.13','33',NULL,'44','2025-12-2593e15de2-889e-4d0e-8f7c-90184a0732a3',NULL,NULL,NULL,NULL,NULL),(7,'1','1','0.01','2',NULL,NULL,'2025-12-2804b5d632-2a9f-405f-9549-97bead708d8e',NULL,NULL,NULL,NULL,NULL),(8,'1','1','1.27','1',NULL,NULL,'2025-12-28c3108a58-a4da-43ea-b985-b37c5dfab904',NULL,NULL,NULL,NULL,NULL),(10,'2','2','4.74','2',NULL,NULL,'2025-12-28434b9a81-3714-410b-b985-d2ec341f2f20',NULL,NULL,NULL,NULL,NULL);
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
  `code` varchar(50) DEFAULT NULL COMMENT '编码',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `version` varchar(50) DEFAULT NULL COMMENT '版本',
  `desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '说明',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工艺信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_craft`
--

LOCK TABLES `biz_craft` WRITE;
/*!40000 ALTER TABLE `biz_craft` DISABLE KEYS */;
INSERT INTO `biz_craft` VALUES (1,'JZ2.940.10287GY-TX02','Ls波段上天线工艺','N.1','2020-2',NULL,NULL,NULL,NULL,NULL),(3,'JD3.850.10398BX-DL05','Ku波段下天线工艺','V.2','2024-1',NULL,NULL,NULL,NULL,NULL),(4,'test','test','1.0','111',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `biz_craft` ENABLE KEYS */;
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
  `algorithm_code` varchar(50) DEFAULT NULL COMMENT '检测算法编码',
  `desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '说明',
  `guide_granularity` tinyint DEFAULT NULL COMMENT '指导粒度(1为工序，2为工步)',
  `craft_id` int DEFAULT NULL COMMENT '工艺id',
  `craft_code` varchar(50) DEFAULT NULL COMMENT '所属工艺编码',
  `guide_map_url` varchar(255) DEFAULT NULL COMMENT '指导图url',
  `process_material_info` text COMMENT '工序材料信息(JSON形式)',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工序信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_process`
--

LOCK TABLES `biz_process` WRITE;
/*!40000 ALTER TABLE `biz_process` DISABLE KEYS */;
INSERT INTO `biz_process` VALUES (1,'30','装配','demo1',NULL,NULL,1,'JZ2.940.10287GY-TX02',NULL,'[{\"errorPreventionMark\":\"\",\"materialName\":\"平垫圈\",\"materialNo\":\"130000461\",\"materialQuantity\":4,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"弹垫\",\"materialNo\":\"130003195\",\"materialQuantity\":4,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"接地柱\",\"materialNo\":\"130026322\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"射频插座\",\"materialNo\":\"140053164\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]',NULL,NULL,NULL,NULL,NULL),(2,'210','装配','demo1',NULL,NULL,1,'JZ2.940.10287GY-TX02',NULL,'[{\"errorPreventionMark\":\"\",\"materialName\":\"防水透气阀\",\"materialNo\":\"130009362\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"航向铭牌\",\"materialNo\":\"JZ8.807.10323\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"铭牌\",\"materialNo\":\"JZ8.807.10324\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"铭牌\",\"materialNo\":\"JZ8.807.10328\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"室温固化硅橡胶\",\"materialNo\":\"130004665\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"螺纹紧固剂\",\"materialNo\":\"130014755\",\"materialQuantity\":1,\"materialUnit\":\"件\"}]',NULL,NULL,NULL,NULL,NULL),(3,'50','焊接','demo1','射频组件焊接',NULL,3,'JD3.850.10398BX-DL05',NULL,'[{\"errorPreventionMark\":\"\",\"materialName\":\"无铅焊锡丝\",\"materialNo\":\"120000578\",\"materialQuantity\":1,\"materialUnit\":\"卷\"},{\"errorPreventionMark\":\"\",\"materialName\":\"环保助焊剂\",\"materialNo\":\"120003269\",\"materialQuantity\":1,\"materialUnit\":\"瓶\"},{\"errorPreventionMark\":\"\",\"materialName\":\"Ku波段射频模块\",\"materialNo\":\"150064275\",\"materialQuantity\":1,\"materialUnit\":\"件\"},{\"errorPreventionMark\":\"\",\"materialName\":\"射频同轴电缆\",\"materialNo\":\"150075386\",\"materialQuantity\":2,\"materialUnit\":\"米\"}]',NULL,NULL,NULL,NULL,NULL),(4,'230','检测','demo1','性能检测与封装',NULL,3,'JD3.850.10398BX-DL05',NULL,'[{\"errorPreventionMark\":\"\",\"materialName\":\"防静电包装袋\",\"materialNo\":\"160008497\",\"materialQuantity\":1,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"\",\"materialName\":\"防震泡沫盒\",\"materialNo\":\"160019508\",\"materialQuantity\":1,\"materialUnit\":\"个\"},{\"errorPreventionMark\":\"\",\"materialName\":\"产品标识标签\",\"materialNo\":\"170020619\",\"materialQuantity\":3,\"materialUnit\":\"张\"},{\"errorPreventionMark\":\"\",\"materialName\":\"封条\",\"materialNo\":\"170031720\",\"materialQuantity\":1,\"materialUnit\":\"张\"}]',NULL,NULL,NULL,NULL,NULL),(5,'222','test',NULL,'111',NULL,4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
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
  `step_id` int DEFAULT NULL COMMENT '工步id',
  `step_name` varchar(50) DEFAULT NULL COMMENT '工步名称',
  `step_status` varchar(10) DEFAULT NULL COMMENT '工步状态',
  `image_path` varchar(255) DEFAULT NULL COMMENT 'Minio Key',
  `data` text COMMENT '拓展数据',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='过程记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_process_record`
--

LOCK TABLES `biz_process_record` WRITE;
/*!40000 ALTER TABLE `biz_process_record` DISABLE KEYS */;
INSERT INTO `biz_process_record` VALUES (1,'1000023452-01',1,'1','1','Mamba.jpg',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'1000023452-01',NULL,'1','2','Mamba.jpg',NULL,'2025-12-29 17:09:25',NULL,NULL,NULL,NULL,NULL);
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
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工步信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_step`
--

LOCK TABLES `biz_step` WRITE;
/*!40000 ALTER TABLE `biz_step` DISABLE KEYS */;
INSERT INTO `biz_step` VALUES (1,'1','1','按表格准备零部件和辅料。',NULL,1,NULL,NULL,NULL,NULL,NULL),(2,'2','2','检查连接器状态，确认一致后装配。',NULL,1,NULL,NULL,NULL,NULL,NULL),(3,'3','3','按工序简图所示将连接器装配到腔体上(注意连接器的安装方向)，螺钉紧固前在螺纹根部涂适量乐泰243，按对称交叉分步拧紧，在连接器与腔体接触出面涂抹一圈南大704硅胶。溢出的胶液用脱脂棉蘸无水乙醇清理干净。',NULL,1,NULL,NULL,NULL,NULL,NULL),(4,'4','4','按照简图所示安装接地柱，螺母螺纹涂乐泰272螺纹紧固剂。注意接地柱各个零部件安装位置。',NULL,1,NULL,NULL,NULL,NULL,NULL),(5,'1','1','天线返回后，检查天线外观，若无划伤、磨损等问题则转调试工序对天线进行参数复测。',NULL,2,NULL,NULL,NULL,NULL,NULL),(6,'2','2','按照工序简图所示安装防水透气阀，螺纹涂乐泰243螺纹紧固剂。',NULL,2,NULL,NULL,NULL,NULL,NULL),(7,'3','3','将铭牌粘接处的标签去除并做好标签转换成铭牌的编号记录。',NULL,2,NULL,NULL,NULL,NULL,NULL),(8,'4','4','用二维码扫描器检查铭牌上的二维码，二维码技术要求如简图所示。',NULL,2,NULL,NULL,NULL,NULL,NULL),(9,'5','5','用100#砂纸打磨天线腔体铭牌粘接处，打磨以基本去除表面氧化层为准，打磨完毕用无尘布蘸丙酮清洗干净天线腔体及铭牌上的浮灰，按1-2:1比例配置好3gDG-4胶，做好配胶记录，在铭牌粘接面均匀涂覆一层DG-4胶，对正腔体位置粘贴铭牌，注意铭牌粘贴方向。',NULL,2,NULL,NULL,NULL,NULL,NULL),(10,'6','6','固化：温度60℃±2℃，时间3小时，时间到后关闭烘箱电源，打开箱门随箱冷却至室温。',NULL,2,NULL,NULL,NULL,NULL,NULL),(11,'7','7','在天线铭牌与腔体粘接四周用704硅橡胶均匀涂覆一层，室温放置4小时以上。',NULL,2,NULL,NULL,NULL,NULL,NULL),(12,'8','8','按图示方向粘贴航向铭牌，压平，无翘曲。（安装于沉头螺钉位置正中心）',NULL,2,NULL,NULL,NULL,NULL,NULL),(13,'9','9','在连接器与腔体接触四周，接地柱安装凸台四周均匀涂抹一圈南大704硅胶，室温晾干。',NULL,2,NULL,NULL,NULL,NULL,NULL),(34,'1','test','1',NULL,5,NULL,NULL,NULL,NULL,NULL);
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
  `work_order_quantity` int DEFAULT NULL COMMENT '工单数量',
  `craft_code` varchar(50) DEFAULT NULL COMMENT '工艺编码',
  `craft_version` varchar(50) DEFAULT NULL COMMENT '工艺版本',
  `status` tinyint DEFAULT NULL COMMENT '工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)',
  `process_code` varchar(50) DEFAULT NULL COMMENT '工序编码',
  `process_name` varchar(50) DEFAULT NULL COMMENT '工序名称',
  `dispatch_quantity` int DEFAULT NULL COMMENT '派单数量',
  `start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '计划结束时间',
  `guide_map_url` varchar(255) DEFAULT NULL COMMENT '引导图url',
  `worker_code` varchar(50) DEFAULT NULL COMMENT '装配工人编码',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '装配工人姓名',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_work_order`
--

LOCK TABLES `biz_work_order` WRITE;
/*!40000 ALTER TABLE `biz_work_order` DISABLE KEYS */;
INSERT INTO `biz_work_order` VALUES (1,'1000023451-01',1,'JZ2.940.10287GY-TX02','N.1',2,'30','装配',1,NULL,NULL,NULL,'07488','张三',NULL,NULL,NULL,NULL,NULL),(2,'1000023451-02',1,'JZ2.940.10287GY-TX02','N.1',1,'210','装配',1,NULL,NULL,NULL,'07489','李四',NULL,NULL,NULL,NULL,NULL),(13,'1000023452-01',1,'JZ2.940.10287GY-TX02','N.1',3,'30','装配',1,'2025-12-09 08:30:00','2025-12-09 12:00:00','Mamba.jpg','07488','张三',NULL,NULL,NULL,NULL,NULL),(14,'1000023452-02',1,'JZ2.940.10287GY-TX02','N.1',1,'210','装配',1,'2025-12-09 13:00:00','2025-12-09 17:00:00',NULL,'07489','李四',NULL,NULL,NULL,NULL,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (1,'craft','工艺信息表',NULL,NULL,'Craft','crud','element-plus','com.ruoyi.imustsz','craft','craft','工艺信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-18 11:45:44','','2025-12-18 12:09:10',NULL),(2,'process','工序信息表',NULL,NULL,'Process','crud','element-plus','com.ruoyi.imustsz','process','process','工序信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-18 12:42:31','','2025-12-18 12:43:20',NULL),(3,'biz_step','工步信息表',NULL,NULL,'BizStep','crud','element-plus','com.imustsz.craft','craft','step','工步信息','imustsz','0','/','{\"parentMenuId\":2000}','admin','2025-12-19 07:20:44','','2025-12-19 07:22:32',NULL),(7,'biz_work_order','工单表',NULL,NULL,'BizWorkOrder','crud','element-plus','com.imustsz.order','wo','workOrder','工单','imustsz','0','/','{\"parentMenuId\":2004}','admin','2025-12-22 08:27:49','','2025-12-22 08:35:26',NULL),(8,'biz_algorithm','算法表',NULL,NULL,'BizAlgorithm','crud','element-plus','com.imustsz.algorithm','algorithm','algorithm','算法','imustsz','0','/','{\"parentMenuId\":2007}','admin','2025-12-22 09:36:57','','2025-12-22 10:51:10',NULL),(9,'biz_process_record','过程记录表',NULL,NULL,'BizProcessRecord','crud','element-plus','com.imustsz.process','process','record','过程记录','imustsz','0','/','{}','admin','2025-12-22 11:45:02','','2025-12-22 11:46:21',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (1,1,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(2,1,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(3,1,'version','版本','varchar(50)','String','version','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(4,1,'brief','说明','text','String','brief','0','0','0','1','1','1','1','EQ','textarea','',4,'admin','2025-12-18 11:45:44','','2025-12-18 12:09:10'),(5,2,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-18 12:42:31','','2025-12-18 12:43:20'),(6,2,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(7,2,'name','名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',3,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(8,2,'brief','说明','text','String','brief','0','0','0','1','1','1','1','EQ','textarea','',4,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:20'),(9,2,'guide_granularity','指导粒度(1为工序，2为工步)','tinyint','Long','guideGranularity','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(10,2,'craft_id','工艺id','int','Long','craftId','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(11,2,'craft_code','所属工艺编码','varchar(50)','String','craftCode','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(12,2,'guide_map_url','指导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(13,2,'process_material_info','工序材料信息(JSON形式)','text','String','processMaterialInfo','0','0','0','1','1','1','1','EQ','textarea','',9,'admin','2025-12-18 12:42:32','','2025-12-18 12:43:21'),(14,3,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(15,3,'code','编码','varchar(50)','String','code','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(16,3,'name','名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',3,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:32'),(17,3,'content','内容','varchar(255)','String','content','0','0','0','1','1','1','1','EQ','editor','',4,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(18,3,'guide_map_url','引导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(19,3,'process_id','工序id','int','Long','processId','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(20,3,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(21,3,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',8,'admin','2025-12-19 07:20:44','','2025-12-19 07:22:33'),(22,3,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(23,3,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(24,3,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',11,'admin','2025-12-19 07:20:45','','2025-12-19 07:22:33'),(60,7,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(61,7,'work_order_code','工单编码','varchar(50)','String','workOrderCode','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(62,7,'work_order_quantity','工单数量','int','Long','workOrderQuantity','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(63,7,'craft_code','工艺编码','varchar(50)','String','craftCode','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(64,7,'craft_version','工艺版本','varchar(50)','String','craftVersion','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(65,7,'status','工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)','tinyint','Long','status','0','0','0','1','1','1','1','EQ','radio','',6,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(66,7,'process_code','工序编码','varchar(50)','String','processCode','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:26'),(67,7,'process_name','工序名称','varchar(50)','String','processName','0','0','0','1','1','1','1','LIKE','input','',8,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:27'),(68,7,'dispatch_quantity','派单数量','int','Long','dispatchQuantity','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-22 08:27:49','','2025-12-22 08:35:27'),(69,7,'start_time','计划开始时间','datetime','Date','startTime','0','0','0','1','1','1','1','EQ','datetime','',10,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(70,7,'end_time','计划结束时间','datetime','Date','endTime','0','0','0','1','1','1','1','EQ','datetime','',11,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(71,7,'guide_map_url','引导图url','varchar(255)','String','guideMapUrl','0','0','0','1','1','1','1','EQ','input','',12,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(72,7,'worker_code','装配工人编码','varchar(50)','String','workerCode','0','0','0','1','1','1','1','EQ','input','',13,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(73,7,'worker_name','装配工人姓名','varchar(50)','String','workerName','0','0','0','1','1','1','1','LIKE','input','',14,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(74,7,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',15,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(75,7,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',16,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(76,7,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',17,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(77,7,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',18,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(78,7,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',19,'admin','2025-12-22 08:27:50','','2025-12-22 08:35:27'),(79,8,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(80,8,'name','算法名称','varchar(50)','String','name','0','0','0','1','1','1','1','LIKE','input','',2,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(81,8,'version','算法版本','varchar(50)','String','version','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:10'),(82,8,'desc','算法描述','varchar(255)','String','desc','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(83,8,'object_name','minio上的算法objectName','varchar(100)','String','objectName','0','0','0','1','1','1','1','LIKE','input','',5,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(84,8,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',6,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(85,8,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(86,8,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(87,8,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(88,8,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-22 09:36:58','','2025-12-22 10:51:11'),(89,9,'id','主键','int','Long','id','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(90,9,'work_order_code','工单编码','varchar(50)','String','workOrderCode','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(91,9,'step_id','工步id','int','Long','stepId','0','0','0','1','1','1','1','EQ','input','',3,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:21'),(92,9,'step_status','工步状态','varchar(10)','String','stepStatus','0','0','0','1','1','1','1','EQ','radio','',4,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(93,9,'image_path','Minio Key','varchar(255)','String','imagePath','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(94,9,'data','拓展数据','text','String','data','0','0','0','1','1','1','1','EQ','textarea','',6,'admin','2025-12-22 11:45:02','','2025-12-22 11:46:22'),(95,9,'submit_time','提交时间','datetime','Date','submitTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(96,9,'created_time','创建时间','datetime','Date','createdTime','0','0','0','1','1','1','1','EQ','datetime','',8,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(97,9,'updated_time','更新时间','datetime','Date','updatedTime','0','0','0','1','1','1','1','EQ','datetime','',9,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(98,9,'created_by','创建人','varchar(50)','String','createdBy','0','0','0','1','1','1','1','EQ','input','',10,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(99,9,'updated_by','更新人','varchar(50)','String','updatedBy','0','0','0','1','1','1','1','EQ','input','',11,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22'),(100,9,'remarks','备注','varchar(255)','String','remarks','0','0','0','1','1','1','1','EQ','input','',12,'admin','2025-12-22 11:45:03','','2025-12-22 11:46:22');
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
INSERT INTO `sys_dept` VALUES (100,0,'0','总公司',0,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:56:49'),(101,100,'0,100','装备制造中心',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:51:26'),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-12-18 08:50:34','',NULL),(103,101,'0,100,101','装备制造中心',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:51:51'),(104,101,'0,100,101','精密制造专业部',2,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:52:09'),(105,101,'0,100,101','工艺技术部产品工艺主管室',3,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:52:29'),(106,101,'0,100,101','工艺技术总装调测工艺技术室',4,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:55:49'),(107,101,'0,100,101','工艺技术部工艺技术体系室',5,'若依','15888888888','ry@qq.com','0','0','admin','2025-12-18 08:50:34','admin','2025-12-28 03:53:10'),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','1','2','admin','2025-12-18 08:50:34','admin','2025-12-28 03:53:22'),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','1','2','admin','2025-12-18 08:50:34','admin','2025-12-28 03:53:25'),(200,100,'0,100','装备制造中心',0,'王雷','15588888888',NULL,'0','2','admin','2025-12-28 03:46:18','',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-18 09:08:04'),(101,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-18 11:45:37'),(102,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-18 12:42:26'),(103,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码已失效','2025-12-19 01:25:17'),(104,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-19 01:25:23'),(105,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 01:25:29'),(106,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 02:42:44'),(107,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 03:19:41'),(108,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 07:12:56'),(109,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 08:00:24'),(110,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-19 09:44:53'),(111,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 09:44:57'),(112,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 12:11:57'),(113,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 12:46:20'),(114,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-19 13:18:58'),(115,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-20 07:51:00'),(116,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-20 07:57:55'),(117,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-20 07:57:58'),(118,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-20 09:08:42'),(119,'admin','127.0.0.1','内网IP','Unknown','Unknown','1','验证码已失效','2025-12-20 11:12:47'),(120,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:19:27'),(121,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:23:25'),(122,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:26:09'),(123,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:28:35'),(124,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:31:05'),(125,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-20 11:43:17'),(126,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-22 08:14:47'),(127,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-22 09:05:52'),(128,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-22 09:36:26'),(129,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-22 10:12:05'),(130,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-22 10:47:25'),(131,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-22 10:48:08'),(132,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-22 10:48:13'),(133,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-22 11:31:10'),(134,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-22 13:13:59'),(135,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-23 02:29:54'),(136,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-12-23 06:55:44'),(137,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-23 06:55:49'),(138,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-23 07:29:36'),(139,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-23 07:36:18'),(140,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-23 11:22:27'),(141,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','退出成功','2025-12-23 12:02:43'),(142,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-24 07:48:05'),(143,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-24 07:54:17'),(144,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-24 08:52:26'),(145,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-24 09:22:32'),(146,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-24 10:50:36'),(147,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-24 11:59:44'),(148,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-24 13:01:28'),(149,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 03:54:42'),(150,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 05:13:06'),(151,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 05:29:34'),(152,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 06:21:21'),(153,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','退出成功','2025-12-25 06:42:28'),(154,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 06:42:34'),(155,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 06:51:28'),(156,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','退出成功','2025-12-25 06:59:08'),(157,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 06:59:12'),(158,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 07:12:38'),(159,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 07:58:45'),(160,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-25 10:32:19'),(161,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 13:28:48'),(162,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-25 13:31:09'),(163,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-26 01:30:46'),(164,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-26 02:13:57'),(165,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-26 07:05:50'),(166,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-26 11:30:55'),(167,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-28 03:21:53'),(168,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-28 03:24:50'),(169,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-28 04:50:16'),(170,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-28 10:33:48'),(171,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-28 11:53:33'),(172,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','退出成功','2025-12-28 12:31:10'),(173,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码已失效','2025-12-28 12:56:02'),(174,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-28 12:56:05'),(175,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码已失效','2025-12-29 02:22:19'),(176,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 02:22:22'),(177,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 03:21:58'),(178,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 07:00:10'),(179,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-29 07:35:21'),(180,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 08:44:51'),(181,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 10:33:53'),(182,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-29 10:48:40'),(183,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-12-29 11:26:06'),(184,'admin','127.0.0.1','内网IP','Unknown','Unknown','0','登录成功','2025-12-29 11:28:46');
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
) ENGINE=InnoDB AUTO_INCREMENT=2011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,5,'system',NULL,'','',1,0,'M','0','0','','system','admin','2025-12-18 08:50:37','admin','2025-12-22 12:18:33','系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','1','1','','monitor','admin','2025-12-18 08:50:37','admin','2025-12-22 12:15:52','系统监控目录'),(3,'系统工具',0,6,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2025-12-18 08:50:37','admin','2025-12-22 12:18:46','系统工具目录'),(4,'若依官网',0,4,'http://ruoyi.vip',NULL,'','',1,0,'M','1','1','','guide','admin','2025-12-18 08:50:37','admin','2025-12-22 12:15:37','若依官网地址'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2025-12-18 08:50:37','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2025-12-18 08:50:37','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','1','0','system:menu:list','tree-table','admin','2025-12-18 08:50:37','admin','2025-12-28 11:11:58','菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2025-12-18 08:50:37','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','1','1','system:post:list','post','admin','2025-12-18 08:50:37','admin','2025-12-22 12:17:19','岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','1','1','system:dict:list','dict','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:52','字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','1','1','system:config:list','edit','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:40','参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','1','1','system:notice:list','message','admin','2025-12-18 08:50:37','admin','2025-12-22 12:16:30','通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2025-12-18 08:50:37','admin','2025-12-22 12:17:47','日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2025-12-18 08:50:37','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2025-12-18 08:50:37','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2025-12-18 08:50:37','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2025-12-18 08:50:37','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2025-12-18 08:50:37','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2025-12-18 08:50:37','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2025-12-18 08:50:37','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2025-12-18 08:50:37','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2025-12-18 08:50:37','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2025-12-18 08:50:37','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2025-12-18 08:50:37','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2025-12-18 08:50:38','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2025-12-18 08:50:38','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2025-12-18 08:50:38','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2025-12-18 08:50:38','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2025-12-18 08:50:38','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2025-12-18 08:50:38','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2025-12-18 08:50:38','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2025-12-18 08:50:39','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2025-12-18 08:50:39','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2025-12-18 08:50:39','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2025-12-18 08:50:39','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2025-12-18 08:50:39','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2025-12-18 08:50:39','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2025-12-18 08:50:39','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2025-12-18 08:50:39','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2025-12-18 08:50:39','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2025-12-18 08:50:39','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2025-12-18 08:50:39','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2025-12-18 08:50:39','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2025-12-18 08:50:39','',NULL,''),(2000,'工艺信息',0,1,'craft',NULL,NULL,'',1,0,'M','0','0',NULL,'clipboard','admin','2025-12-18 11:47:05','',NULL,''),(2001,'工艺信息管理',2000,0,'info_craft','craft/info_craft/index',NULL,'',1,0,'C','0','0','','#','admin','2025-12-18 11:49:01','admin','2025-12-19 01:42:28',''),(2002,'引导信息管理',2000,1,'guide','craft/guide/index',NULL,'',1,0,'C','0','1','','#','admin','2025-12-18 11:52:42','admin','2025-12-23 11:32:45',''),(2003,'工序检测管理',2000,2,'process','craft/process/index',NULL,'',1,0,'C','0','1','','#','admin','2025-12-18 11:53:32','admin','2025-12-23 11:32:48',''),(2004,'工单信息',0,2,'order',NULL,NULL,'',1,0,'M','0','0',NULL,'dashboard','admin','2025-12-18 11:56:35','',NULL,''),(2005,'工单信息管理',2004,0,'info_order','order/info_order/index',NULL,'',1,0,'C','0','0','','chart','admin','2025-12-18 12:01:51','admin','2025-12-19 01:42:14',''),(2006,'工单下发管理',2004,1,'delivery','order/delivery/index',NULL,'',1,0,'C','0','1','','dict','admin','2025-12-18 12:03:29','admin','2025-12-23 11:32:55',''),(2007,'算法管理',0,3,'algorithm',NULL,NULL,'',1,0,'M','0','0',NULL,'druid','admin','2025-12-22 10:12:47','',NULL,''),(2008,'算法信息管理',2007,0,'info_alg','algorithm/info_alg/index',NULL,'',1,0,'C','0','0',NULL,'#','admin','2025-12-22 11:32:39','',NULL,''),(2009,'过程记录管理',0,4,'process',NULL,NULL,'',1,0,'M','0','0',NULL,'component','admin','2025-12-22 11:48:30','',NULL,''),(2010,'过程记录信息',2009,0,'record','process/record/index',NULL,'',1,0,'C','0','0',NULL,'color','admin','2025-12-22 11:49:13','',NULL,'');
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
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table craft(\\n    id int primary key auto_increment comment \'主键\',\\n    code varchar(50) comment \'编码\',\\n    version varchar(50) comment \'版本\',\\n    brief text comment \'说明\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'工艺信息表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:45:44',816),(101,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"clipboard\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工艺信息\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"craft\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:47:05',118),(102,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"craft/\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:49:01',107),(103,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"info\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:50:12',83),(104,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/info/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"info\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:50:22',92),(105,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/guide/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"引导信息管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"guide\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:52:42',108),(106,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/process/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工序检测管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2000,\"path\":\"process\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:53:32',96),(107,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"dashboard\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工单信息\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"order\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 11:56:35',95),(108,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/info/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工单信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2004,\"path\":\"info\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:01:51',121),(109,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/info/index\",\"createTime\":\"2025-12-18 12:01:51\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"工单信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2004,\"path\":\"info\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:02:05',94),(110,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/delivery/index\",\"createBy\":\"admin\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"工单下发管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2004,\"path\":\"delivery\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:03:29',104),(111,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"craft\",\"className\":\"Craft\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":1,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Code\",\"columnComment\":\"编码\",\"columnId\":2,\"columnName\":\"code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"code\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Version\",\"columnComment\":\"版本\",\"columnId\":3,\"columnName\":\"version\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"version\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Brief\",\"columnComment\":\"说明\",\"columnId\":4,\"columnName\":\"brief\",\"columnType\":\"text\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"textarea\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"brief\",\"javaType\":\"String\",\"list\":true,\"pa','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:04:47',358),(112,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"craft\"}',NULL,0,NULL,'2025-12-18 12:04:49',237),(113,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"craft\",\"className\":\"Craft\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":1,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:04:46\",\"usableColumn\":false},{\"capJavaField\":\"Code\",\"columnComment\":\"编码\",\"columnId\":2,\"columnName\":\"code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"code\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:04:46\",\"usableColumn\":false},{\"capJavaField\":\"Version\",\"columnComment\":\"版本\",\"columnId\":3,\"columnName\":\"version\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"version\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:04:46\",\"usableColumn\":false},{\"capJavaField\":\"Brief\",\"columnComment\":\"说明\",\"columnId\":4,\"columnName\":\"brief\",\"columnType\":\"text\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 11:45:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"textarea\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isLi','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:09:10',396),(114,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table process(\\n    id int primary key auto_increment comment \'主键\',\\n    code varchar(50) comment \'编码\',\\n    name varchar(50) comment \'名称\',\\n    brief text comment \'说明\',\\n    guide_granularity tinyint comment \'指导粒度(1为工序，2为工步)\',\\n    craft_id int comment \'工艺id\',\\n    craft_code varchar(50) comment \'所属工艺编码\',\\n    guide_map_url varchar(255) comment \'指导图url\',\\n    process_material_info text comment \'工序材料信息(JSON形式)\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'工序信息表\'\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:42:32',744),(115,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"process\",\"className\":\"Process\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":5,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:31\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Code\",\"columnComment\":\"编码\",\"columnId\":6,\"columnName\":\"code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"code\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"名称\",\"columnId\":7,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Brief\",\"columnComment\":\"说明\",\"columnId\":8,\"columnName\":\"brief\",\"columnType\":\"text\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"textarea\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"brief\",\"javaType\":\"String\",\"list\":true,\"param','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:43:10',655),(116,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"process\",\"className\":\"Process\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":5,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:31\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:43:09\",\"usableColumn\":false},{\"capJavaField\":\"Code\",\"columnComment\":\"编码\",\"columnId\":6,\"columnName\":\"code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"code\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:43:09\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"名称\",\"columnId\":7,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"updateTime\":\"2025-12-18 12:43:09\",\"usableColumn\":false},{\"capJavaField\":\"Brief\",\"columnComment\":\"说明\",\"columnId\":8,\"columnName\":\"brief\",\"columnType\":\"text\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 12:42:32\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"textarea\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\"','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-18 12:43:21',595),(117,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"process\"}',NULL,0,NULL,'2025-12-18 12:43:23',118),(118,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/info/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"inf\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:35:47',102),(119,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/inf/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"inf\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:36:31',96),(120,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/info/index\",\"createTime\":\"2025-12-18 12:01:51\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"工单信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2004,\"path\":\"inf\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:39:00',103),(121,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/inf/index\",\"createTime\":\"2025-12-18 12:01:51\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"工单信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2004,\"path\":\"inf\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:39:23',95),(122,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/info/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"info\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:40:33',93),(123,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/info_order/index\",\"createTime\":\"2025-12-18 12:01:51\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"工单信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2004,\"path\":\"info_order\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:42:14',119),(124,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/info_craft/index\",\"createTime\":\"2025-12-18 11:49:01\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"工艺信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"info_craft\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 01:42:28',93),(125,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_step(\\n    id int primary key auto_increment comment \'主键\',\\n    code varchar(50) comment \'编码\',\\n    name varchar(50) comment \'名称\',\\n    content varchar(255) comment \'内容\',\\n    guide_map_url varchar(255) comment \'引导图url\',\\n    process_id int comment \'工序id\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'工步信息表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 07:20:45',998),(126,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"step\",\"className\":\"BizStep\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":14,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 07:20:44\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Code\",\"columnComment\":\"编码\",\"columnId\":15,\"columnName\":\"code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 07:20:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"code\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"名称\",\"columnId\":16,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 07:20:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Content\",\"columnComment\":\"内容\",\"columnId\":17,\"columnName\":\"content\",\"columnType\":\"varchar(255)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 07:20:44\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"editor\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"content\",\"javaType\":\"String\",\"list','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 07:22:33',707),(127,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_step\"}',NULL,0,NULL,'2025-12-19 07:22:36',288),(128,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_order(\\n    id int primary key auto_increment comment \'主键\',\\n    order_code varchar(50) comment \'生产订单编码\',\\n    product_batch varchar(50) comment \'产品批次\',\\n    material_code varchar(50) comment \'物料编码\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'订单表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 09:46:11',923),(129,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"BizOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":25,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 09:46:11\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"OrderCode\",\"columnComment\":\"生产订单编码\",\"columnId\":26,\"columnName\":\"order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 09:46:11\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"orderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"ProductBatch\",\"columnComment\":\"产品批次\",\"columnId\":27,\"columnName\":\"product_batch\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 09:46:11\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"productBatch\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"MaterialCode\",\"columnComment\":\"物料编码\",\"columnId\":28,\"columnName\":\"material_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 09:46:11\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isReq','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 09:48:04',609),(130,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_order\"}',NULL,0,NULL,'2025-12-19 09:48:24',221),(131,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_work_order(\\n    id int primary key auto_increment comment \'主键\',\\n    work_order_code varchar(50) comment \'工单编码\',\\n    work_order_quantity int comment \'工单数量\',\\n    craft_code varchar(50) comment \'工艺编码\',\\n    craft_version varchar(50) comment \'工艺版本\',\\n    status tinyint comment \'工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)\',\\n    order_id int comment \'订单id\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'工单表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 12:13:09',919),(132,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"info\",\"className\":\"BizWorkOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":34,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:13:08\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderCode\",\"columnComment\":\"工单编码\",\"columnId\":35,\"columnName\":\"work_order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:13:08\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderQuantity\",\"columnComment\":\"工单数量\",\"columnId\":36,\"columnName\":\"work_order_quantity\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:13:08\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderQuantity\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"CraftCode\",\"columnComment\":\"工艺编码\",\"columnId\":37,\"columnName\":\"craft_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:13:08\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQue','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 12:14:16',991),(133,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_work_order\"}',NULL,0,NULL,'2025-12-19 12:14:20',224),(134,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_dispatch_order(\\n    id int primary key auto_increment comment \'主键\',\\n    process_code varchar(50) comment \'工序编码\',\\n    process_name varchar(50) comment \'工序名称\',\\n    dispatch_quantity int comment \'派单数量\',\\n    start_time datetime comment \'计划开始时间\',\\n    end_time datetime comment \'计划结束时间\',\\n    worker_code varchar(50) comment \'装配工人编码\',\\n    worker_name varchar(50) comment \'装配工人姓名\',\\n    order_id int comment \'订单id\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'派单表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 12:21:05',1257),(135,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"info\",\"className\":\"BizDispatchOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":46,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:21:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"ProcessCode\",\"columnComment\":\"工序编码\",\"columnId\":47,\"columnName\":\"process_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:21:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"processCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"ProcessName\",\"columnComment\":\"工序名称\",\"columnId\":48,\"columnName\":\"process_name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:21:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"processName\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"DispatchQuantity\",\"columnComment\":\"派单数量\",\"columnId\":49,\"columnName\":\"dispatch_quantity\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-19 12:21:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-19 12:21:43',787),(136,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_dispatch_order\"}',NULL,0,NULL,'2025-12-19 12:21:46',111),(137,'代码生成',3,'com.imustsz.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/6','127.0.0.1','内网IP','[6]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:21:08',230),(138,'代码生成',3,'com.imustsz.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/5','127.0.0.1','内网IP','[5]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:21:34',216),(139,'代码生成',3,'com.imustsz.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/4','127.0.0.1','内网IP','[4]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:22:16',202),(140,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_work_order(\\n    id int primary key auto_increment comment \'主键\',\\n    work_order_code varchar(50) comment \'工单编码\',\\n    work_order_quantity int comment \'工单数量\',\\n    craft_code varchar(50) comment \'工艺编码\',\\n    craft_version varchar(50) comment \'工艺版本\',\\n    status tinyint comment \'工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)\',\\n    process_code varchar(50) comment \'工序编码\',\\n    process_name varchar(50) comment \'工序名称\',\\n    dispatch_quantity int comment \'派单数量\',\\n    start_time datetime comment \'计划开始时间\',\\n    end_time datetime comment \'计划结束时间\',\\n    guide_map_url varchar(255) comment \'引导图url\',\\n    worker_code varchar(50) comment \'装配工人编码\',\\n    worker_name varchar(50) comment \'装配工人姓名\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'工单表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:27:50',1447),(141,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"BizWorkOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":60,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderCode\",\"columnComment\":\"工单编码\",\"columnId\":61,\"columnName\":\"work_order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderQuantity\",\"columnComment\":\"工单数量\",\"columnId\":62,\"columnName\":\"work_order_quantity\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderQuantity\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"CraftCode\",\"columnComment\":\"工艺编码\",\"columnId\":63,\"columnName\":\"craft_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQu','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:28:35',1251),(142,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"BizWorkOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":60,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:34\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderCode\",\"columnComment\":\"工单编码\",\"columnId\":61,\"columnName\":\"work_order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:34\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderQuantity\",\"columnComment\":\"工单数量\",\"columnId\":62,\"columnName\":\"work_order_quantity\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderQuantity\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:34\",\"usableColumn\":false},{\"capJavaField\":\"CraftCode\",\"columnComment\":\"工艺编码\",\"columnId\":63,\"columnName\":\"craft_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"i','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:28:47',1246),(143,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_work_order\"}',NULL,0,NULL,'2025-12-22 08:28:50',259),(144,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"workOrder\",\"className\":\"BizWorkOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":60,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:46\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderCode\",\"columnComment\":\"工单编码\",\"columnId\":61,\"columnName\":\"work_order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:46\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderQuantity\",\"columnComment\":\"工单数量\",\"columnId\":62,\"columnName\":\"work_order_quantity\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderQuantity\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 08:28:46\",\"usableColumn\":false},{\"capJavaField\":\"CraftCode\",\"columnComment\":\"工艺编码\",\"columnId\":63,\"columnName\":\"craft_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 08:27:49\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 08:35:27',1224),(145,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_work_order\"}',NULL,0,NULL,'2025-12-22 08:35:30',134),(146,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_algorithm(\\n    id int primary key auto_increment comment \'主键\',\\n    name varchar(50) comment \'算法名称\',\\n    version varchar(50) comment \'算法版本\',\\n    `desc` varchar(255) comment \'算法描述\',\\n    object_name varchar(100) comment \'minio上的算法objectName\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'算法表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 09:36:58',905),(147,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"druid\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"算法管理\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"algorithm\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 10:12:47',113),(148,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"algorithm\",\"className\":\"BizAlgorithm\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":79,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"算法名称\",\"columnId\":80,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Version\",\"columnComment\":\"算法版本\",\"columnId\":81,\"columnName\":\"version\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"version\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Desc\",\"columnComment\":\"算法描述\",\"columnId\":82,\"columnName\":\"desc\",\"columnType\":\"varchar(255)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"desc\",\"javaType\"','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 10:49:01',828),(149,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_algorithm\"}',NULL,0,NULL,'2025-12-22 10:49:12',247),(150,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"algorithm\",\"className\":\"BizAlgorithm\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":79,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 10:49:00\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"算法名称\",\"columnId\":80,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 10:49:00\",\"usableColumn\":false},{\"capJavaField\":\"Version\",\"columnComment\":\"算法版本\",\"columnId\":81,\"columnName\":\"version\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"version\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":8,\"updateBy\":\"\",\"updateTime\":\"2025-12-22 10:49:00\",\"usableColumn\":false},{\"capJavaField\":\"Desc\",\"columnComment\":\"算法描述\",\"columnId\":82,\"columnName\":\"desc\",\"columnType\":\"varchar(255)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 09:36:58\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 10:51:11',619),(151,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_algorithm\"}',NULL,0,NULL,'2025-12-22 10:51:15',109),(152,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"algorithm/info_alg/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"算法信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2007,\"path\":\"info_alg\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 11:32:39',92),(153,'创建表',0,'com.imustsz.generator.controller.GenController.createTableSave()','POST',1,'admin','研发部门','/tool/gen/createTable','127.0.0.1','内网IP','{\"sql\":\"create table biz_process_record(\\n    id int primary key auto_increment comment \'主键\',\\n    work_order_code varchar(50) comment \'工单编码\',\\n    step_id int comment \'工步id\',\\n    step_status varchar(10) comment \'工步状态\',\\n    image_path varchar(255) comment \'Minio Key\',\\n    data text comment \'拓展数据\',\\n    submit_time datetime comment \'提交时间\',\\n    created_time datetime comment \'创建时间\',\\n    updated_time datetime comment \'更新时间\',\\n    created_by varchar(50) comment \'创建人\',\\n    updated_by varchar(50) comment \'更新人\',\\n    remarks varchar(255) comment \'备注\'\\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment \'过程记录表\';\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 11:45:03',941),(154,'代码生成',2,'com.imustsz.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"record\",\"className\":\"BizProcessRecord\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":89,\"columnName\":\"id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 11:45:02\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":9,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"WorkOrderCode\",\"columnComment\":\"工单编码\",\"columnId\":90,\"columnName\":\"work_order_code\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 11:45:02\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"workOrderCode\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":9,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"StepId\",\"columnComment\":\"工步id\",\"columnId\":91,\"columnName\":\"step_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 11:45:02\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"stepId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":9,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"StepStatus\",\"columnComment\":\"工步状态\",\"columnId\":92,\"columnName\":\"step_status\",\"columnType\":\"varchar(10)\",\"createBy\":\"admin\",\"createTime\":\"2025-12-22 11:45:02\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"radio\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 11:46:22',788),(155,'代码生成',8,'com.imustsz.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"biz_process_record\"}',NULL,0,NULL,'2025-12-22 11:46:25',231),(156,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"component\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"过程记录管理\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"process\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 11:48:30',85),(157,'菜单管理',1,'com.imustsz.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"process/record/index\",\"createBy\":\"admin\",\"icon\":\"color\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"过程记录信息\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2009,\"path\":\"record\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 11:49:13',81),(158,'菜单管理',3,'com.imustsz.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-12-22 12:15:04',58),(159,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"http://ruoyi.vip\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:15:13',102),(160,'菜单管理',3,'com.imustsz.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-12-22 12:15:16',65),(161,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"http://ruoyi.vip\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:15:24',92),(162,'菜单管理',3,'com.imustsz.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-12-22 12:15:28',58),(163,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"http://ruoyi.vip\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:15:37',106),(164,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2,\"menuName\":\"系统监控\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"monitor\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:15:52',95),(165,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/notice/index\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"message\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":107,\"menuName\":\"通知公告\",\"menuType\":\"C\",\"orderNum\":8,\"params\":{},\"parentId\":1,\"path\":\"notice\",\"perms\":\"system:notice:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:16:30',82),(166,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/config/index\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":106,\"menuName\":\"参数设置\",\"menuType\":\"C\",\"orderNum\":7,\"params\":{},\"parentId\":1,\"path\":\"config\",\"perms\":\"system:config:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:16:40',96),(167,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/dict/index\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":105,\"menuName\":\"字典管理\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":1,\"path\":\"dict\",\"perms\":\"system:dict:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:16:52',94),(168,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/post/index\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"post\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":104,\"menuName\":\"岗位管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":1,\"path\":\"post\",\"perms\":\"system:post:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:17:19',89),(169,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":108,\"menuName\":\"日志管理\",\"menuType\":\"M\",\"orderNum\":9,\"params\":{},\"parentId\":1,\"path\":\"log\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:17:33',90),(170,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":108,\"menuName\":\"日志管理\",\"menuType\":\"M\",\"orderNum\":9,\"params\":{},\"parentId\":1,\"path\":\"log\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:17:47',96),(171,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"系统管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:18:33',100),(172,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"tool\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3,\"menuName\":\"系统工具\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":0,\"path\":\"tool\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-22 12:18:46',92),(173,'工艺信息',1,'com.imustsz.craft.controller.CraftController.add()','POST',1,'admin','研发部门','/craft/info','127.0.0.1','内网IP','{\"code\":\"test\",\"desc\":\"111\",\"id\":4,\"name\":\"test\",\"params\":{},\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:23:37',98),(174,'工序信息',1,'com.imustsz.craft.controller.ProcessController.add()','POST',1,'admin','研发部门','/craft/process','127.0.0.1','内网IP','{\"code\":\"222\",\"craftId\":4,\"desc\":\"111\",\"id\":5,\"name\":\"test\",\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:23:48',74),(175,'工步信息',1,'com.imustsz.craft.controller.BizStepController.add()','POST',1,'admin','研发部门','/craft/step','127.0.0.1','内网IP','{\"code\":\"1\",\"content\":\"1\",\"id\":34,\"name\":\"test\",\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:25:15',105),(176,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/guide/index\",\"createTime\":\"2025-12-18 11:52:42\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2002,\"menuName\":\"引导信息管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"guide\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:32:45',112),(177,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"craft/process/index\",\"createTime\":\"2025-12-18 11:53:32\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"工序检测管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2000,\"path\":\"process\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:32:48',106),(178,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"order/delivery/index\",\"createTime\":\"2025-12-18 12:03:29\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2006,\"menuName\":\"工单下发管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2004,\"path\":\"delivery\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-23 11:32:55',105),(179,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"desc\":\"11111\",\"name\":\"demo1\",\"params\":{},\"version\":\"1.0\"}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'desc ) \n         values ( \'demo1\',\n            \'1.0\',\n            \'11111\' )\' at line 4\r\n### The error may exist in file [D:\\Project\\SpringBoot+VUE\\procvision_bs\\src\\backend\\imustsz-business\\target\\classes\\mapper\\algorithm\\BizAlgorithmMapper.xml]\r\n### The error may involve com.imustsz.algorithm.mapper.BizAlgorithmMapper.insertBizAlgorithm-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into biz_algorithm          ( name,             version,             desc )           values ( ?,             ?,             ? )\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'desc ) \n         values ( \'demo1\',\n            \'1.0\',\n            \'11111\' )\' at line 4\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'desc ) \n         values ( \'demo1\',\n            \'1.0\',\n            \'11111\' )\' at line 4','2025-12-24 09:22:45',158),(180,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-24 09:23:46',140),(181,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 07:33:00',10),(182,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 08:07:58',9),(183,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 08:08:31',10),(184,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"desc\":\"11\",\"name\":\"1\",\"params\":{},\"version\":\"11\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 08:09:26',1),(185,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 08:58:51',10),(186,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 09:00:07',15),(187,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}',NULL,1,'Cannot invoke \"org.springframework.web.multipart.MultipartFile.getName()\" because the return value of \"com.imustsz.algorithm.domain.BizAlgorithm.getFile()\" is null','2025-12-25 09:00:42',10),(188,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"params\":{}}',NULL,1,'Cannot invoke \"org.springframework.web.multipart.MultipartFile.getName()\" because the return value of \"com.imustsz.algorithm.domain.BizAlgorithm.getFile()\" is null','2025-12-25 09:01:25',12),(189,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"desc\":\"4\",\"name\":\"2\",\"params\":{},\"version\":\"3\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 09:35:19',10),(190,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"11\",\"desc\":\"44\",\"name\":\"22\",\"params\":{},\"version\":\"33\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 11:21:08',11),(191,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:20:46',65),(192,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:24:06',60),(193,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:25:19',58),(194,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z2\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:27:21',58),(195,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:29:27',56),(196,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"desc\":\"11111\",\"id\":1,\"name\":\"demo1\",\"params\":{},\"url\":\"http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi9NYW1iYS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0yQktVVTFHNlUyOTdGMFkzSDNFMyUyRjIwMjUxMjI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyNFQxMzA0MDNaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl5UWt0VlZURkhObFV5T1RkR01Ga3pTRE5GTXlJc0ltVjRjQ0k2TVRjMk5qWXlORFU0T0N3aWNHRnlaVzUwSWpvaVlXUnRhVzRpZlEuVHFEQUUweW54RzRBR3dhb3lRR1pPdHg5UHRPUjVIQ0x4Z3FLNHZxUlhJcUFpd1FUQ1dldlFEWkJGX2YxRGo2SEF3dlRWNG83YnF6Q2RZQ0Q1QjI4ZXcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0zZTQxY2ZkODQwZDI0ZDMwNTBjMGMwODY1NmYxMmI3OWFiMGY0M2MzMTMyYzVkMjQyMDMxOGUwYWI2YzUyMGQ5\",\"version\":\"1.0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:29:47',59),(197,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"2\",\"version\":\"2\",\"desc\":\"11\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 12:38:08',5),(198,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z2\",\"name\":\"test1\",\"version\":\"333\",\"desc\":\"222\"}',NULL,1,'Range [-1, 46) out of bounds for length 46','2025-12-25 13:25:36',67),(199,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"11\",\"name\":\"22\",\"version\":\"33\",\"desc\":\"44\"}',NULL,1,'algorithm','2025-12-25 13:47:36',7),(200,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"11\",\"name\":\"22\",\"version\":\"33\",\"desc\":\"44\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-25 13:49:08',27943),(201,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 01:36:29',128),(202,'算法',1,'com.imustsz.algorithm.controller.BizAlgorithmController.add()','POST',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"version\":\"1\"}',NULL,1,'Cannot invoke \"org.springframework.web.multipart.MultipartFile.getInputStream()\" because \"file\" is null','2025-12-26 03:02:41',8),(203,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"z1\",\"name\":\"demo1\",\"objectName\":\"Mamba.jpg\",\"id\":\"1\",\"version\":\"1.0\",\"desc\":\"11111\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:29:40',4),(204,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"11\",\"name\":\"22\",\"objectName\":\"2025-12-2593e15de2-889e-4d0e-8f7c-90184a0732a3\",\"id\":\"2\",\"version\":\"33\",\"desc\":\"44\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:33:21',0),(205,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"z1\",\"name\":\"demo1\",\"objectName\":\"Mamba.jpg\",\"id\":\"1\",\"version\":\"1.0\",\"desc\":\"11111\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:47:00',0),(206,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"11\",\"name\":\"22\",\"objectName\":\"2025-12-2593e15de2-889e-4d0e-8f7c-90184a0732a3\",\"id\":\"2\",\"version\":\"33\",\"desc\":\"44\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:47:22',0),(207,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084822Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=8ab4ec56e9054cfd5b0a29b3d26ca993fcab20db940c383505ac18c54fc8e2c0\"}',0,NULL,'2025-12-26 08:48:21',97),(208,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084855Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=b9a2b103ff03bf75fdebbedb92dec7612f8095da62c927b635612c207588f78e\"}',0,NULL,'2025-12-26 08:48:54',18391),(209,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084902Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=2ff3fc0ee85f0c32724aaf5173a6b5803f5079356338907d0491b693f872e5b1\"}',0,NULL,'2025-12-26 08:49:01',2347),(210,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084912Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f39fde0e3cfb9335820f33437ef3a6ad8d2852f3fcf9b49f6b78ceda7d07b048\"}',0,NULL,'2025-12-26 08:49:11',65),(211,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:49:33',33),(212,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084939Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=964dcf0d26f9762c5827eb892f0e2f3fa2cd84e0304b233026415225cb70ed73\"}',0,NULL,'2025-12-26 08:49:38',62),(213,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T084943Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=4f57cf4f16065af9934bd917477bee4e0e8a97e4f05ebbed15ac39f0843a26d0\"}',0,NULL,'2025-12-26 08:49:43',60),(214,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:50:13',63),(215,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T085019Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=fac28274b8f81044c7a6de11eb7503b595b7d685b65336d1a08327c57f32037b\"}',0,NULL,'2025-12-26 08:50:18',79),(216,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T085022Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f5fad1ef307eae020836447bbf0c9157cdcf275bc57a9ab824ff8c99a612cabd\"}',0,NULL,'2025-12-26 08:50:21',56),(217,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:50:51',30),(218,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T085056Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=9dd7b95f8567e85651a148c4a6c57b5f4b118e3fa3e18589c060b67bc85ec9f5\"}',0,NULL,'2025-12-26 08:50:55',102),(219,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:51:00',30),(220,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:51:38',26),(221,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DGA - 副本.jpg\",\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"修改成功\",\"code\":200,\"data\":\"http://39.104.202.123:9000/dev/2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20251226%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251226T085144Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e23d49050f6c2bc12d24bb721dac0bb14ac24d92b29a56980f4d8f5827d7e34e\"}',0,NULL,'2025-12-26 08:51:43',88),(222,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','研发部门','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-26182a1f41-a79e-49fd-8619-b7a252ac733a\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-26 08:51:47',27),(223,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"装备制造中心\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:44:56',152),(224,'部门管理',1,'com.imustsz.web.controller.system.SysDeptController.add()','POST',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"createBy\":\"admin\",\"deptName\":\"装备制造中心\",\"leader\":\"王雷\",\"orderNum\":0,\"params\":{},\"parentId\":100,\"phone\":\"15588888888\",\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:46:18',147),(225,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"深圳总公司\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"1\"}','{\"msg\":\"该部门包含未停用的子部门！\",\"code\":500}',0,NULL,'2025-12-28 03:49:29',73),(226,'用户管理',1,'com.imustsz.web.controller.system.SysUserController.add()','POST',1,'admin','研发部门','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptId\":200,\"nickName\":\"imustsz\",\"params\":{},\"phonenumber\":\"15588888888\",\"postIds\":[],\"roleIds\":[2],\"sex\":\"0\",\"status\":\"0\",\"userId\":100,\"userName\":\"imustsz\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:50:15',385),(227,'部门管理',3,'com.imustsz.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/200','127.0.0.1','内网IP','200','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2025-12-28 03:50:54',63),(228,'用户管理',3,'com.imustsz.web.controller.system.SysUserController.remove()','DELETE',1,'admin','研发部门','/system/user/100','127.0.0.1','内网IP','[100]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:51:05',249),(229,'部门管理',3,'com.imustsz.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/200','127.0.0.1','内网IP','200','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:51:13',170),(230,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"装备制造中心\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:51:26',407),(231,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"装备制造中心\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:51:52',296),(232,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":104,\"deptName\":\"精密制造专业部\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":2,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:52:09',278),(233,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":105,\"deptName\":\"工艺技术部产品工艺主管室\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":3,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:52:29',284),(234,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":106,\"deptName\":\"工艺技术部总装调测工艺技术室\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":4,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:52:41',285),(235,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":107,\"deptName\":\"工艺技术部工艺技术体系室\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":5,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:10',295),(236,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":102,\"deptName\":\"长沙分公司\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":2,\"params\":{},\"parentId\":100,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"1\"}','{\"msg\":\"该部门包含未停用的子部门！\",\"code\":500}',0,NULL,'2025-12-28 03:53:16',74),(237,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,102\",\"children\":[],\"deptId\":108,\"deptName\":\"市场部门\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":102,\"parentName\":\"长沙分公司\",\"phone\":\"15888888888\",\"status\":\"1\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:22',250),(238,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,102\",\"children\":[],\"deptId\":109,\"deptName\":\"财务部门\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":2,\"params\":{},\"parentId\":102,\"parentName\":\"长沙分公司\",\"phone\":\"15888888888\",\"status\":\"1\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:25',253),(239,'部门管理',3,'com.imustsz.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/109','127.0.0.1','内网IP','109','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:30',148),(240,'部门管理',3,'com.imustsz.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/108','127.0.0.1','内网IP','108','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:33',145),(241,'部门管理',3,'com.imustsz.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/102','127.0.0.1','内网IP','102','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:53:34',147),(242,'个人信息',2,'com.imustsz.web.controller.system.SysProfileController.updateProfile()','PUT',1,'admin','研发部门','/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"管理员\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:54:18',159),(243,'用户管理',2,'com.imustsz.web.controller.system.SysUserController.edit()','PUT',1,'admin','研发部门','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"admin\",\"createTime\":\"2025-12-18 08:50:35\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":105,\"deptName\":\"工艺技术部产品工艺主管室\",\"leader\":\"若依\",\"orderNum\":3,\"params\":{},\"parentId\":101,\"status\":\"0\"},\"deptId\":105,\"email\":\"ry@qq.com\",\"loginDate\":\"2025-12-18 08:50:35\",\"loginIp\":\"127.0.0.1\",\"nickName\":\"用户\",\"params\":{},\"phonenumber\":\"15666666666\",\"postIds\":[2],\"pwdUpdateDate\":\"2025-12-18 08:50:35\",\"remark\":\"测试员\",\"roleIds\":[2],\"roles\":[{\"admin\":false,\"dataScope\":\"2\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\"}],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":2,\"userName\":\"ry\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:55:16',442),(244,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":106,\"deptName\":\"工艺技术总装调测工艺技术室\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":4,\"params\":{},\"parentId\":101,\"parentName\":\"装备制造中心\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:55:49',283),(245,'部门管理',2,'com.imustsz.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"总公司\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 03:56:49',185),(246,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','装备制造中心','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DragonKMS_WD16.rar\",\"code\":\"1\",\"file\":\"DragonKMS_WD16.rar\",\"name\":\"1\",\"objectName\":\"2025-12-28688f463a-8a7d-4775-a4d1-c3b3baebfb99\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:27:19',5),(247,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','装备制造中心','/algorithm','127.0.0.1','内网IP','{\"fileName\":\"DragonKMS_WD16.rar\",\"code\":\"1\",\"file\":\"DragonKMS_WD16.rar\",\"name\":\"1\",\"objectName\":\"2025-12-28466a4f6f-538a-4735-bf40-d838f371b3a4\",\"id\":\"3\",\"version\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:28:06',15765),(248,'算法',3,'com.imustsz.algorithm.controller.BizAlgorithmController.remove()','DELETE',1,'admin','装备制造中心','/algorithm/4','127.0.0.1','内网IP','[4]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:40:21',153),(249,'算法',3,'com.imustsz.algorithm.controller.BizAlgorithmController.remove()','DELETE',1,'admin','装备制造中心','/algorithm/5','127.0.0.1','内网IP','[5]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:48:54',186),(250,'算法',3,'com.imustsz.algorithm.controller.BizAlgorithmController.remove()','DELETE',1,'admin','装备制造中心','/algorithm/3','127.0.0.1','内网IP','[3]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:48:56',159),(251,'算法',3,'com.imustsz.algorithm.controller.BizAlgorithmController.remove()','DELETE',1,'admin','装备制造中心','/algorithm/6','127.0.0.1','内网IP','[6]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:51:20',122),(252,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','装备制造中心','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-28f5537354-cabc-444d-bf6b-4382d63785e4\",\"id\":\"7\",\"version\":\"2\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:51:58',90),(253,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','装备制造中心','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"name\":\"1\",\"objectName\":\"2025-12-28522a6100-b72c-4dbc-895b-3ad6eb3f07c1\",\"id\":\"7\",\"version\":\"2\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 06:52:23',138),(254,'算法',2,'com.imustsz.algorithm.controller.BizAlgorithmController.edit()','PUT',1,'admin','装备制造中心','/algorithm','127.0.0.1','内网IP','{\"code\":\"1\",\"size\":\"0.01\",\"name\":\"1\",\"objectName\":\"2025-12-2804b5d632-2a9f-405f-9549-97bead708d8e\",\"id\":\"7\",\"version\":\"2\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 07:07:26',155),(255,'算法',3,'com.imustsz.algorithm.controller.BizAlgorithmController.remove()','DELETE',1,'admin','装备制造中心','/algorithm/9','127.0.0.1','内网IP','[9]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 10:44:13',171),(256,'菜单管理',3,'com.imustsz.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','装备制造中心','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-12-28 11:10:18',60),(257,'菜单管理',3,'com.imustsz.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','装备制造中心','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-12-28 11:10:37',51),(258,'菜单管理',2,'com.imustsz.web.controller.system.SysMenuController.edit()','PUT',1,'admin','装备制造中心','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/menu/index\",\"createTime\":\"2025-12-18 08:50:37\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":102,\"menuName\":\"菜单管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":1,\"path\":\"menu\",\"perms\":\"system:menu:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-28 11:11:58',99),(259,'过程记录',3,'com.imustsz.process.controller.BizProcessRecordController.remove()','DELETE',1,'admin','装备制造中心','/process/record/2','127.0.0.1','内网IP','[2]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-29 08:44:57',77),(260,'过程记录',3,'com.imustsz.process.controller.BizProcessRecordController.remove()','DELETE',1,'admin','装备制造中心','/process/record/3','127.0.0.1','内网IP','[3]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-12-29 08:45:23',53);
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
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2025-12-18 08:50:36','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2025-12-18 08:50:36','',NULL,'普通角色');
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
INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,4),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,117),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060);
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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','管理员','00','ry@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2025-12-29 19:28:46','2025-12-18 08:50:35','admin','2025-12-18 08:50:35','','2025-12-28 03:54:18','管理员'),(2,105,'ry','用户','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2025-12-18 08:50:35','2025-12-18 08:50:35','admin','2025-12-18 08:50:35','admin','2025-12-28 03:55:16','测试员'),(100,200,'imustsz','imustsz','00','','15588888888','0','','$2a$10$Jp1B3h263pZeeQcRGyLZPOkhfgn8d6aurGMv4KpD3ZsUotDNQZoGO','0','2','',NULL,NULL,'admin','2025-12-28 03:50:15','',NULL,NULL);
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
INSERT INTO `sys_user_role` VALUES (1,1),(2,2);
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

-- Dump completed on 2025-12-29 19:55:58
