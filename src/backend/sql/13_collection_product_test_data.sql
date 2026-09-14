-- 先执行12_collection_product_fields.sql，再在测试环境执行本文件。
-- 5条type=1测试记录，无需指定主键；重复执行不重复插入。图片对象须已经存在。
START TRANSACTION;
INSERT INTO biz_data_collection (data,image_path,type,product_time,product_model,product_batch,process_num,other_info,create_time,create_by,remark)
SELECT '生产导出测试_20260914_01','["collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/original.jpg","collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/thumb.jpg"]',1,'2026-09-10 08:00:00','连接器-A','B20260910','P01','测试样本1',NOW(),'admin','collection_product_demo_v1'
WHERE NOT EXISTS (SELECT 1 FROM biz_data_collection WHERE type=1 AND data='生产导出测试_20260914_01');
INSERT INTO biz_data_collection (data,image_path,type,product_time,product_model,product_batch,process_num,other_info,create_time,create_by,remark)
SELECT '生产导出测试_20260914_02','["collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/original.jpg","collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/thumb.jpg"]',1,'2026-09-10 15:30:00','连接器-A','B20260910','P02','测试样本2',NOW(),'admin','collection_product_demo_v1'
WHERE NOT EXISTS (SELECT 1 FROM biz_data_collection WHERE type=1 AND data='生产导出测试_20260914_02');
INSERT INTO biz_data_collection (data,image_path,type,product_time,product_model,product_batch,process_num,other_info,create_time,create_by,remark)
SELECT '生产导出测试_20260914_03','["collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/original.jpg","collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/thumb.jpg"]',1,'2026-09-11 09:20:00','连接器-A','B20260911','P01','测试样本3',NOW(),'admin','collection_product_demo_v1'
WHERE NOT EXISTS (SELECT 1 FROM biz_data_collection WHERE type=1 AND data='生产导出测试_20260914_03');
INSERT INTO biz_data_collection (data,image_path,type,product_time,product_model,product_batch,process_num,other_info,create_time,create_by,remark)
SELECT '生产导出测试_20260914_04','["collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/original.jpg","collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/thumb.jpg"]',1,'2026-09-12 10:00:00','电机-B','B20260912','P01','测试样本4',NOW(),'admin','collection_product_demo_v1'
WHERE NOT EXISTS (SELECT 1 FROM biz_data_collection WHERE type=1 AND data='生产导出测试_20260914_04');
INSERT INTO biz_data_collection (data,image_path,type,product_time,product_model,product_batch,process_num,other_info,create_time,create_by,remark)
SELECT '生产导出测试_20260914_05','["collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/original.jpg","collection/type1/17/b76b49b8-c2b0-4083-a3f5-decc408ccd83/thumb.jpg"]',1,'2026-09-13 23:59:59','电机-B','B20260912','P03','测试样本5',NOW(),'admin','collection_product_demo_v1'
WHERE NOT EXISTS (SELECT 1 FROM biz_data_collection WHERE type=1 AND data='生产导出测试_20260914_05');
COMMIT;
