package com.ruoyi.system.domain;

import java.io.Serializable;

/**
 * App to alert-channel relation entity.
 */
public class MonitorAppAlertChannel implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long appId;

    private Long channelId;

    private String createBy;

    public Long getAppId()
    {
        return appId;
    }

    public void setAppId(Long appId)
    {
        this.appId = appId;
    }

    public Long getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }
}
