package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.MonitorPlan;
import com.ruoyi.system.domain.vo.MonitorPlanQuotaVo;
import com.ruoyi.system.mapper.MonitorAlertChannelMapper;
import com.ruoyi.system.mapper.MonitorAppMapper;
import com.ruoyi.system.mapper.MonitorPlanMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.IMonitorPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitorPlanServiceImpl implements IMonitorPlanService {
    @Autowired
    private MonitorPlanMapper monitorPlanMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MonitorAppMapper monitorAppMapper;

    @Autowired
    private MonitorAlertChannelMapper monitorAlertChannelMapper;

    @Override
    public MonitorPlan selectMonitorPlanById(Long id) {
        return monitorPlanMapper.selectMonitorPlanById(id);
    }

    @Override
    public List<MonitorPlan> selectMonitorPlanList(MonitorPlan monitorPlan) {
        return monitorPlanMapper.selectMonitorPlanList(monitorPlan);
    }

    @Override
    @Transactional
    public int insertMonitorPlan(MonitorPlan monitorPlan) {
        normalizePlan(monitorPlan);
        validatePlan(monitorPlan);
        if (monitorPlan.getIsDefault() != null && monitorPlan.getIsDefault() == 1) {
            monitorPlanMapper.clearDefaultPlan(null);
        }
        return monitorPlanMapper.insertMonitorPlan(monitorPlan);
    }

    @Override
    @Transactional
    public int updateMonitorPlan(MonitorPlan monitorPlan) {
        if (monitorPlan.getId() == null) {
            throw new ServiceException("套餐ID不能为空");
        }
        MonitorPlan exists = monitorPlanMapper.selectMonitorPlanById(monitorPlan.getId());
        if (exists == null) {
            throw new ServiceException("套餐不存在");
        }
        normalizePlan(monitorPlan);
        validatePlan(monitorPlan);
        if (monitorPlan.getIsDefault() != null && monitorPlan.getIsDefault() == 1) {
            monitorPlanMapper.clearDefaultPlan(monitorPlan.getId());
        }
        return monitorPlanMapper.updateMonitorPlan(monitorPlan);
    }

    @Override
    public int deleteMonitorPlanByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        for (Long id : ids) {
            MonitorPlan plan = monitorPlanMapper.selectMonitorPlanById(id);
            if (plan == null) {
                continue;
            }
            if (plan.getIsDefault() != null && plan.getIsDefault() == 1) {
                throw new ServiceException("默认套餐不允许删除");
            }
            long bindCount = sysUserMapper.countUserByPlanCode(plan.getPlanCode());
            if (bindCount > 0) {
                throw new ServiceException("套餐【" + plan.getPlanName() + "】仍有用户绑定，不能删除");
            }
        }
        return monitorPlanMapper.deleteMonitorPlanByIds(ids);
    }

    @Override
    public List<Map<String, String>> selectEnabledPlanOptions() {
        MonitorPlan query = new MonitorPlan();
        query.setStatus("0");
        List<MonitorPlan> plans = monitorPlanMapper.selectMonitorPlanList(query);
        List<Map<String, String>> options = new ArrayList<>();
        for (MonitorPlan plan : plans) {
            Map<String, String> option = new LinkedHashMap<>(2);
            option.put("label", plan.getPlanName());
            option.put("value", plan.getPlanCode());
            options.add(option);
        }
        return options;
    }

    @Override
    public MonitorPlan resolveEffectivePlan(SysUser user) {
        if (user == null) {
            return resolveDefaultPlan();
        }
        if (StringUtils.isNotBlank(user.getPlanCode()) && !isExpired(user.getPlanExpireTime())) {
            MonitorPlan assignedPlan = monitorPlanMapper.selectMonitorPlanByCode(user.getPlanCode());
            if (assignedPlan != null && "0".equals(assignedPlan.getStatus())) {
                return assignedPlan;
            }
        }
        return resolveDefaultPlan();
    }

    @Override
    public MonitorPlan resolveEffectivePlanByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return resolveDefaultPlan();
        }
        SysUser user = sysUserMapper.selectUserByUserName(username);
        return resolveEffectivePlan(user);
    }

    @Override
    public MonitorPlanQuotaVo buildCurrentUserQuota() {
        String username = SecurityUtils.getUsername();
        MonitorPlan plan = resolveEffectivePlanByUsername(username);
        MonitorPlanQuotaVo quota = new MonitorPlanQuotaVo();
        quota.setPlanCode(plan.getPlanCode());
        quota.setPlanName(plan.getPlanName());
        quota.setMaxAlertChannels(plan.getMaxAlertChannels());
        quota.setMaxApps(plan.getMaxApps());
        quota.setScanIntervalMinutes(plan.getScanIntervalMinutes());
        quota.setUsedApps(monitorAppMapper.countMonitorAppByCreateBy(username));
        quota.setUsedAlertChannels(monitorAlertChannelMapper.countAlertChannelByCreateBy(username));
        return quota;
    }

    @Override
    public void checkAlertChannelQuota(String username, int increment) {
        if (SecurityUtils.isAdmin() || increment <= 0) {
            return;
        }
        MonitorPlan plan = resolveEffectivePlanByUsername(username);
        long used = monitorAlertChannelMapper.countAlertChannelByCreateBy(username);
        long next = used + increment;
        if (plan.getMaxAlertChannels() != null && next > plan.getMaxAlertChannels()) {
            throw new ServiceException("当前套餐【" + plan.getPlanName() + "】最多允许创建 " + plan.getMaxAlertChannels() + " 个通知群组");
        }
    }

    @Override
    public void checkAppQuota(String username, int increment) {
        if (SecurityUtils.isAdmin() || increment <= 0) {
            return;
        }
        MonitorPlan plan = resolveEffectivePlanByUsername(username);
        long used = monitorAppMapper.countMonitorAppByCreateBy(username);
        long next = used + increment;
        if (plan.getMaxApps() != null && next > plan.getMaxApps()) {
            throw new ServiceException("当前套餐【" + plan.getPlanName() + "】最多允许添加 " + plan.getMaxApps() + " 个应用");
        }
    }

    @Override
    public int resolveScanIntervalMinutes(String username) {
        MonitorPlan plan = resolveEffectivePlanByUsername(username);
        return plan.getScanIntervalMinutes() == null || plan.getScanIntervalMinutes() <= 0 ? 5 : plan.getScanIntervalMinutes();
    }

    private void normalizePlan(MonitorPlan monitorPlan) {
        String planCode = StringUtils.trim(monitorPlan.getPlanCode());
        monitorPlan.setPlanCode(StringUtils.isBlank(planCode) ? planCode : planCode.toLowerCase());
        monitorPlan.setPlanName(StringUtils.trim(monitorPlan.getPlanName()));
        monitorPlan.setStatus(StringUtils.defaultIfBlank(StringUtils.trim(monitorPlan.getStatus()), "0"));
        if (monitorPlan.getIsDefault() == null) {
            monitorPlan.setIsDefault(0);
        }
        if (monitorPlan.getSortOrder() == null) {
            monitorPlan.setSortOrder(0);
        }
    }

    private void validatePlan(MonitorPlan monitorPlan) {
        MonitorPlan codeExists = monitorPlanMapper.selectMonitorPlanByCode(monitorPlan.getPlanCode());
        if (codeExists != null && !codeExists.getId().equals(monitorPlan.getId())) {
            throw new ServiceException("套餐编码已存在");
        }
    }

    private MonitorPlan resolveDefaultPlan() {
        MonitorPlan defaultPlan = monitorPlanMapper.selectDefaultMonitorPlan();
        if (defaultPlan != null) {
            return defaultPlan;
        }
        throw new ServiceException("未配置默认套餐，请先在套餐管理中配置一条默认套餐");
    }

    private boolean isExpired(Date expireTime) {
        return expireTime != null && expireTime.before(new Date());
    }
}
