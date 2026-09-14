-- 装配结果组合查询：可选性能索引（MySQL 8）。
-- 先选择现有业务数据库，再执行本文件；不建库、不删除数据、不修改字段。
-- 适用于当前代码配套数据库，须已有 submit_time/create_time 等字段。
-- 相同名称索引存在时跳过；上线前可用 SHOW INDEX 检查是否已有等价索引。
-- 无需新增业务字段或数据迁移。脚本可以重复执行。

SET @result_index_sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'biz_process_record' AND index_name = 'idx_result_submit_id'),
  'SELECT 1',
  'ALTER TABLE biz_process_record ADD INDEX idx_result_submit_id (submit_time, id)');
PREPARE result_index_stmt FROM @result_index_sql;
EXECUTE result_index_stmt;
DEALLOCATE PREPARE result_index_stmt;

SET @result_index_sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'biz_process_record' AND index_name = 'idx_result_create_id'),
  'SELECT 1',
  'ALTER TABLE biz_process_record ADD INDEX idx_result_create_id (create_time, id)');
PREPARE result_index_stmt FROM @result_index_sql;
EXECUTE result_index_stmt;
DEALLOCATE PREPARE result_index_stmt;

SET @result_index_sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'biz_work_order' AND index_name = 'idx_result_order_process'),
  'SELECT 1',
  'ALTER TABLE biz_work_order ADD INDEX idx_result_order_process (work_order_code, process_code, id)');
PREPARE result_index_stmt FROM @result_index_sql;
EXECUTE result_index_stmt;
DEALLOCATE PREPARE result_index_stmt;
