package com.ruoyi.system.domain.vo;

/**
 * 应用监控概览
 * 
 */
public class MonitorAppOverviewVo
{
    /** 应用总数 */
    private Long totalApps;

    /** 上架应用�?*/
    private Long onlineApps;

    /** 下架应用�?*/
    private Long offlineApps;

    public Long getTotalApps()
    {
        return totalApps;
    }

    public void setTotalApps(Long totalApps)
    {
        this.totalApps = totalApps;
    }

    public Long getOnlineApps()
    {
        return onlineApps;
    }

    public void setOnlineApps(Long onlineApps)
    {
        this.onlineApps = onlineApps;
    }

    public Long getOfflineApps()
    {
        return offlineApps;
    }

    public void setOfflineApps(Long offlineApps)
    {
        this.offlineApps = offlineApps;
    }
}
