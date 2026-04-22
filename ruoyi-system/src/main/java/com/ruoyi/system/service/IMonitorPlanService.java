package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.MonitorPlan;
import com.ruoyi.system.domain.vo.MonitorPlanQuotaVo;

import java.util.List;
import java.util.Map;

public interface IMonitorPlanService
{
    MonitorPlan selectMonitorPlanById(Long id);

    List<MonitorPlan> selectMonitorPlanList(MonitorPlan monitorPlan);

    int insertMonitorPlan(MonitorPlan monitorPlan);

    int updateMonitorPlan(MonitorPlan monitorPlan);

    int deleteMonitorPlanByIds(Long[] ids);

    List<Map<String, String>> selectEnabledPlanOptions();

    MonitorPlan resolveEffectivePlan(SysUser user);

    MonitorPlan resolveEffectivePlanByUsername(String username);

    MonitorPlanQuotaVo buildCurrentUserQuota();

    void checkAlertChannelQuota(String username, int increment);

    void checkAppQuota(String username, int increment);

    int resolveScanIntervalMinutes(String username);
}
