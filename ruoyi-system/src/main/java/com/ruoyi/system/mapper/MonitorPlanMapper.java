package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MonitorPlanMapper
{
    MonitorPlan selectMonitorPlanById(Long id);

    MonitorPlan selectMonitorPlanByCode(String planCode);

    MonitorPlan selectDefaultMonitorPlan();

    List<MonitorPlan> selectMonitorPlanList(MonitorPlan monitorPlan);

    int insertMonitorPlan(MonitorPlan monitorPlan);

    int updateMonitorPlan(MonitorPlan monitorPlan);

    int clearDefaultPlan(@Param("excludeId") Long excludeId);

    int deleteMonitorPlanByIds(Long[] ids);
}
