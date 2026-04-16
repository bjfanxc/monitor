package com.ruoyi.system.service.impl;

import com.monitor.common.exception.ServiceException;
import com.monitor.common.utils.StringUtils;
import com.monitor.system.domain.MonitorApp;
import com.monitor.system.domain.dto.MonitorAppStatusDto;
import com.monitor.system.domain.vo.MonitorAppOverviewVo;
import com.monitor.system.mapper.MonitorAppMapper;
import com.monitor.system.service.IMonitorAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搴旂敤鐩戞帶鏈嶅姟瀹炵幇
 * 
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
            throw new ServiceException("搴旂敤ID涓嶈兘涓虹┖");
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
            throw new ServiceException("瀵煎叆搴旂敤鏁版嵁涓嶈兘涓虹┖");
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
                    successMsg.append("<br/>").append(successNum).append("銆佸簲鐢?").append(monitorApp.getAppName()).append(" 瀵煎叆鎴愬姛");
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
                    successMsg.append("<br/>").append(successNum).append("銆佸簲鐢?").append(monitorApp.getAppName()).append(" 鏇存柊鎴愬姛");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("銆佸簲鐢?").append(monitorApp.getAppName())
                        .append(" 宸插瓨鍦紙bundleId + storePlatform + region锛夛紝濡傞渶瑕嗙洊璇峰嬀閫夋洿鏂?);
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String appName = StringUtils.isNotBlank(monitorApp.getAppName()) ? monitorApp.getAppName() : "鏈懡鍚嶅簲鐢?;
                failureMsg.append("<br/>").append(failureNum).append("銆佸簲鐢?").append(appName).append(" 瀵煎叆澶辫触锛?)
                    .append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "寰堟姳姝夛紝瀵煎叆澶辫触锛佸叡 " + failureNum + " 鏉℃暟鎹紓甯革紝閿欒濡備笅锛?);
            throw new ServiceException(failureMsg.toString());
        }
        return "鎭枩鎮紝鏁版嵁宸插叏閮ㄥ鍏ユ垚鍔燂紒鍏?" + successNum + " 鏉★紝璇︽儏濡備笅锛? + successMsg;
    }

    private void validateImportRow(MonitorApp monitorApp)
    {
        if (StringUtils.isBlank(monitorApp.getProductName()))
        {
            throw new ServiceException("浜у搧鍚嶇О涓嶈兘涓虹┖");
        }
        if (StringUtils.isBlank(monitorApp.getAppName()))
        {
            throw new ServiceException("搴旂敤鍚嶇О涓嶈兘涓虹┖");
        }
        if (StringUtils.isBlank(monitorApp.getBundleId()))
        {
            throw new ServiceException("鍖呭悕 / Bundle ID 涓嶈兘涓虹┖");
        }
        if (StringUtils.isBlank(monitorApp.getStorePlatform()))
        {
            throw new ServiceException("鍟嗗簵骞冲彴涓嶈兘涓虹┖");
        }
        if (StringUtils.isBlank(monitorApp.getRegion()))
        {
            throw new ServiceException("鍦板尯涓嶈兘涓虹┖");
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
            throw new ServiceException("搴旂敤鍞竴閿啿绐侊細bundleId + storePlatform + region 宸插瓨鍦?);
        }
    }

    private MonitorApp ensureMonitorAppExists(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("搴旂敤ID涓嶈兘涓虹┖");
        }
        MonitorApp monitorApp = monitorAppMapper.selectMonitorAppById(id);
        if (monitorApp == null)
        {
            throw new ServiceException("搴旂敤涓嶅瓨鍦ㄦ垨宸插垹闄?);
        }
        return monitorApp;
    }
}
