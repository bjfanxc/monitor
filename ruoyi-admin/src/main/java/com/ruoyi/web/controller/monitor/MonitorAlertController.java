package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.MonitorAlertRecord;
import com.ruoyi.system.service.IMonitorAlertRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 告警记录管理
 */
@RestController
@RequestMapping("/monitor/alert")
public class MonitorAlertController extends BaseController
{
    @Autowired
    private IMonitorAlertRecordService monitorAlertRecordService;

    /**
     * 获取告警记录列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorAlertRecord alertRecord)
    {
        startPage();
        List<MonitorAlertRecord> list = monitorAlertRecordService.selectMonitorAlertRecordList(alertRecord);
        return getDataTable(list);
    }
}
