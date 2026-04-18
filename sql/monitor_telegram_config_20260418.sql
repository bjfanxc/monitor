-- 平台统一 Telegram 机器人配置
-- 请按实际值修改 config_value 后执行，或直接在系统参数页面维护这些 key。

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '平台 Telegram Bot Token', 'monitor.telegram.botToken', '', 'N', 'admin', NOW(), '平台统一 Telegram 机器人 Token'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'monitor.telegram.botToken'
);

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '平台 Telegram 机器人链接', 'monitor.telegram.botLink', '', 'N', 'admin', NOW(), '用于机器人绑定说明展示，例如 https://t.me/your_bot'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'monitor.telegram.botLink'
);

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '平台 Telegram 机器人用户名', 'monitor.telegram.botUsername', '', 'N', 'admin', NOW(), '用于机器人绑定说明展示，例如 your_bot'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'monitor.telegram.botUsername'
);

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Telegram 绑定指令示例', 'monitor.telegram.bindKeyword', '', 'N', 'admin', NOW(), '用于机器人绑定说明展示，例如 绑定机器人 your_account'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'monitor.telegram.bindKeyword'
);

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT 'Telegram Webhook Secret', 'monitor.telegram.webhookSecret', '', 'N', 'admin', NOW(), 'Telegram webhook 请求头 X-Telegram-Bot-Api-Secret-Token'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'monitor.telegram.webhookSecret'
);
