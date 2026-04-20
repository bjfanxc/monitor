ALTER TABLE monitor_alert_channel
    ADD COLUMN access_mode VARCHAR(32) NOT NULL DEFAULT 'webhook' COMMENT '接入方式：webhook/custom' AFTER channel_type;

UPDATE monitor_alert_channel
SET access_mode = 'webhook'
WHERE access_mode IS NULL OR access_mode = '';

CREATE TABLE IF NOT EXISTS monitor_app_alert_channel (
    app_id BIGINT NOT NULL COMMENT '产品ID',
    channel_id BIGINT NOT NULL COMMENT '告警群组ID',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (app_id, channel_id),
    KEY idx_monitor_app_alert_channel_channel_id (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品与告警群组关联表';
