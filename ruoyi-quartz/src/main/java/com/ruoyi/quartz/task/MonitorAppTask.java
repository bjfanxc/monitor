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
        monitorAppService.scanGooglePlayApps("system");
    }

    public void scanGooglePlayAppsByHttp()
    {
        monitorAppService.scanGooglePlayApps("system", "http");
    }

    public void scanGooglePlayAppsByPlaywright()
    {
        monitorAppService.scanGooglePlayApps("system", "playwright");
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
