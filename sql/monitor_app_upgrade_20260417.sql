ALTER TABLE monitor_app
    ADD COLUMN product_name VARCHAR(100) NULL COMMENT '产品名称' AFTER id;

UPDATE monitor_app
SET product_name = app_name
WHERE (product_name IS NULL OR product_name = '');
