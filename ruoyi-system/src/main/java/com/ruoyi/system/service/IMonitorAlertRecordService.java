package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorAlertRecord;

import java.util.List;

/**
 * 告警记录服务接口
 */
public interface IMonitorAlertRecordService
{
    /**
     * 获取告警记录列表
     *
     * @param alertRecord 查询条件
     * @return 告警记录列表
     */
    List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord);
}
