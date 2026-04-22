package com.ruoyi.system.domain.vo;

public class MonitorPlanQuotaVo
{
    private String planCode;

    private String planName;

    private Integer maxAlertChannels;

    private Integer maxApps;

    private Integer scanIntervalMinutes;

    private Long usedAlertChannels;

    private Long usedApps;

    public String getPlanCode()
    {
        return planCode;
    }

    public void setPlanCode(String planCode)
    {
        this.planCode = planCode;
    }

    public String getPlanName()
    {
        return planName;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    public Integer getMaxAlertChannels()
    {
        return maxAlertChannels;
    }

    public void setMaxAlertChannels(Integer maxAlertChannels)
    {
        this.maxAlertChannels = maxAlertChannels;
    }

    public Integer getMaxApps()
    {
        return maxApps;
    }

    public void setMaxApps(Integer maxApps)
    {
        this.maxApps = maxApps;
    }

    public Integer getScanIntervalMinutes()
    {
        return scanIntervalMinutes;
    }

    public void setScanIntervalMinutes(Integer scanIntervalMinutes)
    {
        this.scanIntervalMinutes = scanIntervalMinutes;
    }

    public Long getUsedAlertChannels()
    {
        return usedAlertChannels;
    }

    public void setUsedAlertChannels(Long usedAlertChannels)
    {
        this.usedAlertChannels = usedAlertChannels;
    }

    public Long getUsedApps()
    {
        return usedApps;
    }

    public void setUsedApps(Long usedApps)
    {
        this.usedApps = usedApps;
    }
}
