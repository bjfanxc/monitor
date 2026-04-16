package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorAlertRecord;

import java.util.List;

/**
 * 告警记录数据层
 */
public interface MonitorAlertRecordMapper
{
    /**
     * 获取告警记录列表
     *
     * @param alertRecord 查询条件
     * @return 告警记录列表
     */
    List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord);

    int insertMonitorAlertRecord(MonitorAlertRecord alertRecord);
}
