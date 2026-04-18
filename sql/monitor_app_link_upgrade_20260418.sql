ALTER TABLE monitor_app
    ADD COLUMN app_link VARCHAR(512) NULL COMMENT '应用链接' AFTER app_name;

UPDATE monitor_app
SET app_link = CONCAT('https://play.google.com/store/apps/details?id=', bundle_id, '&hl=en_US&gl=', IFNULL(NULLIF(region, ''), 'US'))
WHERE (app_link IS NULL OR app_link = '')
  AND bundle_id IS NOT NULL
  AND bundle_id <> '';

ALTER TABLE monitor_app
    MODIFY COLUMN bundle_id VARCHAR(255) NULL COMMENT '包名',
    MODIFY COLUMN region VARCHAR(16) NULL COMMENT '地区';
