package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;
import com.ruoyi.system.mapper.MonitorAppMapper;
import com.ruoyi.system.service.IMonitorAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用监控服务实现
 */
@Service
public class MonitorAppServiceImpl implements IMonitorAppService
{
    @Autowired
    private MonitorAppMapper monitorAppMapper;

    @Override
    public MonitorAppOverviewVo selectMonitorAppOverview()
    {
        MonitorAppOverviewVo overview = monitorAppMapper.selectMonitorAppOverview();
        if (overview == null)
        {
            overview = new MonitorAppOverviewVo();
            overview.setTotalApps(0L);
            overview.setOnlineApps(0L);
            overview.setOfflineApps(0L);
        }
        return overview;
    }

    @Override
    public List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp)
    {
        return monitorAppMapper.selectMonitorAppList(monitorApp);
    }

    @Override
    public int insertMonitorApp(MonitorApp monitorApp)
    {
        normalizeMonitorApp(monitorApp);
        checkMonitorAppUnique(monitorApp);
        return monitorAppMapper.insertMonitorApp(monitorApp);
    }

    @Override
    public int updateMonitorApp(MonitorApp monitorApp)
    {
        if (monitorApp.getId() == null)
        {
            throw new ServiceException("应用ID不能为空");
        }
        normalizeMonitorApp(monitorApp);
        ensureMonitorAppExists(monitorApp.getId());
        checkMonitorAppUnique(monitorApp);
        return monitorAppMapper.updateMonitorApp(monitorApp);
    }

    @Override
    public int deleteMonitorAppById(Long id)
    {
        ensureMonitorAppExists(id);
        return monitorAppMapper.deleteMonitorAppById(id);
    }

    @Override
    public int updateMonitorAppStatus(MonitorAppStatusDto statusDto, String updateBy)
    {
        MonitorApp monitorApp = ensureMonitorAppExists(statusDto.getId());
        monitorApp.setStatus(statusDto.getStatus());
        monitorApp.setUpdateBy(updateBy);
        return monitorAppMapper.updateMonitorAppStatus(monitorApp);
    }

    @Override
    public String importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(appList) || appList.isEmpty())
        {
            throw new ServiceException("导入应用数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (MonitorApp monitorApp : appList)
        {
            try
            {
                normalizeMonitorApp(monitorApp);
                validateImportRow(monitorApp);
                monitorApp.setCreateBy(operName);
                monitorApp.setUpdateBy(operName);
                MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
                if (StringUtils.isNull(exists))
                {
                    if (monitorApp.getStatus() == null)
                    {
                        monitorApp.setStatus(1);
                    }
                    if (StringUtils.isBlank(monitorApp.getOwnerType()))
                    {
                        monitorApp.setOwnerType("system");
                    }
                    monitorAppMapper.insertMonitorApp(monitorApp);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、应用 ").append(monitorApp.getAppName()).append(" 导入成功");
                }
                else if (updateSupport)
                {
                    monitorApp.setId(exists.getId());
                    if (monitorApp.getStatus() == null)
                    {
                        monitorApp.setStatus(exists.getStatus());
                    }
                    if (StringUtils.isBlank(monitorApp.getOwnerType()))
                    {
                        monitorApp.setOwnerType(exists.getOwnerType());
                    }
                    monitorAppMapper.updateMonitorApp(monitorApp);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、应用 ").append(monitorApp.getAppName()).append(" 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、应用 ").append(monitorApp.getAppName())
                        .append(" 已存在，唯一键 bundleId + storePlatform + region 冲突");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String appName = StringUtils.isNotBlank(monitorApp.getAppName()) ? monitorApp.getAppName() : "未命名应用";
                failureMsg.append("<br/>").append(failureNum).append("、应用 ").append(appName).append(" 导入失败：")
                    .append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据存在问题，详情如下：");
            throw new ServiceException(failureMsg.toString());
        }
        return "恭喜，数据已全部导入成功！共 " + successNum + " 条，详情如下：" + successMsg;
    }

    private void validateImportRow(MonitorApp monitorApp)
    {
        if (StringUtils.isBlank(monitorApp.getProductName()))
        {
            throw new ServiceException("产品名称不能为空");
        }
        if (StringUtils.isBlank(monitorApp.getAppName()))
        {
            throw new ServiceException("应用名称不能为空");
        }
        if (StringUtils.isBlank(monitorApp.getBundleId()))
        {
            throw new ServiceException("包名 / Bundle ID 不能为空");
        }
        if (StringUtils.isBlank(monitorApp.getStorePlatform()))
        {
            throw new ServiceException("商店平台不能为空");
        }
        if (StringUtils.isBlank(monitorApp.getRegion()))
        {
            throw new ServiceException("地区不能为空");
        }
    }

    private void normalizeMonitorApp(MonitorApp monitorApp)
    {
        if (monitorApp == null)
        {
            return;
        }
        monitorApp.setProductName(StringUtils.trim(monitorApp.getProductName()));
        monitorApp.setAppName(StringUtils.trim(monitorApp.getAppName()));
        monitorApp.setBundleId(StringUtils.trim(monitorApp.getBundleId()));
        monitorApp.setStorePlatform(StringUtils.trim(monitorApp.getStorePlatform()));
        monitorApp.setRegion(StringUtils.trim(monitorApp.getRegion()));
        monitorApp.setOwnerType(StringUtils.trim(monitorApp.getOwnerType()));
    }

    private void checkMonitorAppUnique(MonitorApp monitorApp)
    {
        MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
        if (exists != null && !exists.getId().equals(monitorApp.getId()))
        {
            throw new ServiceException("应用唯一键冲突，请检查 bundleId + storePlatform + region 是否重复");
        }
    }

    private MonitorApp ensureMonitorAppExists(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("应用ID不能为空");
        }
        MonitorApp monitorApp = monitorAppMapper.selectMonitorAppById(id);
        if (monitorApp == null)
        {
            throw new ServiceException("应用不存在或已被删除");
        }
        return monitorApp;
    }
}
