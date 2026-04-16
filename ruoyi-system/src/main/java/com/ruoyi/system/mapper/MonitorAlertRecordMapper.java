package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.MonitorAlertRecord;

import java.util.List;

/**
 * 鍛婅璁板綍鏁版嵁灞? * 
 */
public interface MonitorAlertRecordMapper
{
    /**
     * 鏌ヨ鍛婅璁板綍鍒楄〃
     * 
     * @param alertRecord 鏌ヨ鏉′欢
     * @return 鍛婅璁板綍鍒楄〃
     */
    public List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord);
}
