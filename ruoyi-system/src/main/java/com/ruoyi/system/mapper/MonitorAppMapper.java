package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;

import java.util.List;

/**
 * App monitor mapper.
 */
public interface MonitorAppMapper
{
    MonitorAppOverviewVo selectMonitorAppOverview(MonitorApp monitorApp);

    MonitorApp selectMonitorAppById(Long id);

    List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    Long countMonitorAppByCreateBy(String createBy);

    MonitorApp selectMonitorAppByUniqueKey(MonitorApp monitorApp);

    List<MonitorApp> selectAppsForScan(Integer status);

    int insertMonitorApp(MonitorApp monitorApp);

    int updateMonitorApp(MonitorApp monitorApp);

    int updateMonitorAppStatus(MonitorApp monitorApp);

    int updateMonitorAppScanInfo(MonitorApp monitorApp);

    int deleteMonitorAppById(Long id);
}
