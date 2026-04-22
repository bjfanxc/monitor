ALTER TABLE sys_user
    ADD COLUMN plan_code varchar(32) DEFAULT NULL COMMENT '套餐编码' AFTER remark,
    ADD COLUMN plan_expire_time datetime DEFAULT NULL COMMENT '套餐到期时间' AFTER plan_code;

CREATE TABLE IF NOT EXISTS monitor_plan (
    id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plan_code varchar(32) NOT NULL COMMENT '套餐编码',
    plan_name varchar(64) NOT NULL COMMENT '套餐名称',
    max_alert_channels int(11) NOT NULL DEFAULT 0 COMMENT '群组数量上限',
    max_apps int(11) NOT NULL DEFAULT 0 COMMENT '应用数量上限',
    scan_interval_minutes int(11) NOT NULL DEFAULT 5 COMMENT '扫描间隔(分钟)',
    sort_order int(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    is_default tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认套餐',
    status char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime DEFAULT NULL COMMENT '创建时间',
    update_by varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_monitor_plan_code (plan_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控套餐配置表';

INSERT INTO monitor_plan(plan_code, plan_name, max_alert_channels, max_apps, scan_interval_minutes, sort_order, is_default, status, remark, create_by, create_time)
SELECT 'test', '测试版', 3, 20, 15, 1, 1, '0', '默认测试套餐', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_plan WHERE plan_code = 'test');

INSERT INTO monitor_plan(plan_code, plan_name, max_alert_channels, max_apps, scan_interval_minutes, sort_order, is_default, status, remark, create_by, create_time)
SELECT 'pro', '付费版', 20, 200, 5, 2, 0, '0', '付费用户套餐', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_plan WHERE plan_code = 'pro');

UPDATE sys_user
SET plan_code = 'test'
WHERE user_id <> 1
  AND (plan_code IS NULL OR plan_code = '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2200, '套餐管理', 2, 20, 'plan', 'monitor/plan/index', '', '', 1, 0, 'C', '0', '0', 'monitor:plan:list', 'clipboard', 'admin', NOW(), '', NULL, '套餐管理菜单'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2200);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2201, '套餐查询', 2200, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:plan:query', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2201);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2202, '套餐新增', 2200, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:plan:add', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2202);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2203, '套餐修改', 2200, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:plan:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2203);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2204, '套餐删除', 2200, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:plan:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2204);
