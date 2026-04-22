package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.MonitorAlertRecord;
import com.ruoyi.system.mapper.MonitorAlertRecordMapper;
import com.ruoyi.system.service.IMonitorAlertRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警记录服务实现
 */
@Service
public class MonitorAlertRecordServiceImpl implements IMonitorAlertRecordService {
    @Autowired
    private MonitorAlertRecordMapper monitorAlertRecordMapper;

    @Override
    public List<MonitorAlertRecord> selectMonitorAlertRecordList(MonitorAlertRecord alertRecord) {
        if (!SecurityUtils.isAdmin()) {
            alertRecord.getParams().put("currentUsername", SecurityUtils.getUsername());
        }
        return monitorAlertRecordMapper.selectMonitorAlertRecordList(alertRecord);
    }
}
