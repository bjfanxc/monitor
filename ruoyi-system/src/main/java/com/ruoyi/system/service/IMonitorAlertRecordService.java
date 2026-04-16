package com.ruoyi.system.service;

import com.monitor.system.domain.MonitorAlertRecord;

import java.util.List;

/**
 * 鍛婅璁板綍鏈嶅姟灞? * 
 */
public interface IMonitorAlertRecordService
{
    /**
     * 鏌ヨ鍛婅璁板綍鍒楄〃
     * 
     * @param alertRecord 鏌ヨ鏉′欢
     * @return 鍛婅璁板綍鍒楄〃
     */
    public List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord);
}
