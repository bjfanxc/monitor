package com.ruoyi.quartz.task;

import com.ruoyi.system.service.IMonitorAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 应用监控任务
 */
@Component("monitorAppTask")
public class MonitorAppTask
{
    @Autowired
    private IMonitorAppService monitorAppService;

    public void scanGooglePlayApps()
    {
        monitorAppService.scanDueGooglePlayApps("system", "http");
    }

    public void scanGooglePlayAppsByHttp()
    {
        monitorAppService.scanDueGooglePlayApps("system", "http");
    }

    public void scanGooglePlayAppsByPlaywright()
    {
        monitorAppService.scanDueGooglePlayApps("system", "playwright");
    }

    public void scanGooglePlayApp(Long appId)
    {
        monitorAppService.scanGooglePlayApp(appId, "system");
    }

    public void scanGooglePlayAppByHttp(Long appId)
    {
        monitorAppService.scanGooglePlayApp(appId, "system", "http");
    }

    public void scanGooglePlayAppByPlaywright(Long appId)
    {
        monitorAppService.scanGooglePlayApp(appId, "system", "playwright");
    }
}
