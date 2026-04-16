package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;

import java.util.List;

/**
 * 应用监控数据层
 */
public interface MonitorAppMapper
{
    /**
     * 获取应用监控概览
     *
     * @param monitorApp 查询条件
     * @return 应用概览
     */
    MonitorAppOverviewVo selectMonitorAppOverview(MonitorApp monitorApp);

    /**
     * 根据ID查询应用监控
     *
     * @param id 应用ID
     * @return 应用信息
     */
    MonitorApp selectMonitorAppById(Long id);

    /**
     * 获取应用监控列表
     *
     * @param monitorApp 查询条件
     * @return 应用监控列表
     */
    List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    /**
     * 根据唯一键查询应用
     *
     * @param monitorApp 应用信息
     * @return 应用信息
     */
    MonitorApp selectMonitorAppByUniqueKey(MonitorApp monitorApp);

    /**
     * 新增应用监控
     *
     * @param monitorApp 应用信息
     * @return 结果
     */
    int insertMonitorApp(MonitorApp monitorApp);

    /**
     * 修改应用监控
     *
     * @param monitorApp 应用信息
     * @return 结果
     */
    int updateMonitorApp(MonitorApp monitorApp);

    /**
     * 修改应用状态
     *
     * @param monitorApp 应用信息
     * @return 结果
     */
    int updateMonitorAppStatus(MonitorApp monitorApp);

    int updateMonitorAppScanInfo(MonitorApp monitorApp);

    /**
     * 删除应用监控
     *
     * @param id 应用ID
     * @return 结果
     */
    int deleteMonitorAppById(Long id);
}
