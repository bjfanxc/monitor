package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppImportResultVo;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;
import com.ruoyi.system.domain.vo.MonitorAppScanResultVo;

import java.util.List;
import java.util.Map;

/**
 * App monitor service.
 */
public interface IMonitorAppService
{
    MonitorAppOverviewVo selectMonitorAppOverview();

    Map<String, Object> selectMonitorAppFormOptions();

    List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    int insertMonitorApp(MonitorApp monitorApp);

    int updateMonitorApp(MonitorApp monitorApp);

    int deleteMonitorAppById(Long id);

    int updateMonitorAppStatus(MonitorAppStatusDto statusDto, String updateBy);

    MonitorAppImportResultVo importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName);

    int assignAlertChannels(List<Long> appIds, List<Long> channelIds, String operator);

    MonitorAppScanResultVo scanGooglePlayApp(Long id, String operator);

    MonitorAppScanResultVo scanGooglePlayApp(Long id, String operator, String scanMode);

    List<MonitorAppScanResultVo> scanGooglePlayApps(String operator);

    List<MonitorAppScanResultVo> scanGooglePlayApps(String operator, String scanMode);
}
