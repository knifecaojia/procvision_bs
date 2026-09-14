-- 在现有业务数据库中执行；先备份。可重复执行，不删除现有数据。

SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='product_time'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD COLUMN product_time DATETIME NULL COMMENT ''生产日期''');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;

SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='product_model'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD COLUMN product_model VARCHAR(100) NULL COMMENT ''产品型号''');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;

SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='product_batch'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD COLUMN product_batch VARCHAR(100) NULL COMMENT ''产品批次''');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;

SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='process_num'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD COLUMN process_num VARCHAR(100) NULL COMMENT ''工序代号''');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;

SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='other_info'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD COLUMN other_info VARCHAR(500) NULL COMMENT ''其他信息''');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;

-- 兼容曾手动添加为VARCHAR的product_time；非标准日期须先清理后执行此转换。
SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND column_name='product_time' AND data_type <> 'datetime'), 'ALTER TABLE biz_data_collection MODIFY COLUMN product_time DATETIME NULL COMMENT ''生产日期''', 'SELECT 1');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;
SET @collection_ddl = IF(EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='biz_data_collection' AND index_name='idx_collection_type_product_time'), 'SELECT 1', 'ALTER TABLE biz_data_collection ADD INDEX idx_collection_type_product_time (type, product_time, id)');
PREPARE collection_stmt FROM @collection_ddl;
EXECUTE collection_stmt;
DEALLOCATE PREPARE collection_stmt;
