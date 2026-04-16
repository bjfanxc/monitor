package com.ruoyi.system.service.impl;

import com.monitor.system.domain.MonitorAlertRecord;
import com.monitor.system.mapper.MonitorAlertRecordMapper;
import com.monitor.system.service.IMonitorAlertRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 鍛婅璁板綍鏈嶅姟瀹炵幇
 * 
 */
@Service
public class MonitorAlertRecordServiceImpl implements IMonitorAlertRecordService
{
    @Autowired
    private MonitorAlertRecordMapper monitorAlertRecordMapper;

    @Override
    public List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord)
    {
        return monitorAlertRecordMapper.selectMonitorAlertRecordList(alertRecord);
    }
}
