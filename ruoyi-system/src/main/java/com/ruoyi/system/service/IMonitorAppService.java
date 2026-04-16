package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;

import java.util.List;

/**
 * 应用监控服务接口
 */
public interface IMonitorAppService
{
    /**
     * 获取应用监控概览
     *
     * @return 应用概览
     */
    MonitorAppOverviewVo selectMonitorAppOverview();

    /**
     * 获取应用监控列表
     *
     * @param monitorApp 查询条件
     * @return 应用监控列表
     */
    List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    /**
     * 新增应用监控
     *
     * @param monitorApp 应用监控信息
     * @return 结果
     */
    int insertMonitorApp(MonitorApp monitorApp);

    /**
     * 修改应用监控
     *
     * @param monitorApp 应用监控信息
     * @return 结果
     */
    int updateMonitorApp(MonitorApp monitorApp);

    /**
     * 删除应用监控
     *
     * @param id 应用ID
     * @return 结果
     */
    int deleteMonitorAppById(Long id);

    /**
     * 修改应用状态
     *
     * @param statusDto 状态参数
     * @param updateBy 更新人
     * @return 结果
     */
    int updateMonitorAppStatus(MonitorAppStatusDto statusDto, String updateBy);

    /**
     * 导入应用监控数据
     *
     * @param appList 应用列表
     * @param updateSupport 是否更新已存在数据
     * @param operName 操作人
     * @return 导入结果
     */
    String importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName);
}
