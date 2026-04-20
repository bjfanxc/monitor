package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * monitor_alert_record
 */
public class MonitorAlertRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long appId;

    private String productName;

    private String appLink;

    private String storePlatform;

    private String channelType;

    private String alertType;

    private String alertMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alertTime;

    private String delFlag;

    private String keyword;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getAppId()
    {
        return appId;
    }

    public void setAppId(Long appId)
    {
        this.appId = appId;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getAppLink()
    {
        return appLink;
    }

    public void setAppLink(String appLink)
    {
        this.appLink = appLink;
    }

    public String getStorePlatform()
    {
        return storePlatform;
    }

    public void setStorePlatform(String storePlatform)
    {
        this.storePlatform = storePlatform;
    }

    public String getChannelType()
    {
        return channelType;
    }

    public void setChannelType(String channelType)
    {
        this.channelType = channelType;
    }

    public String getAlertType()
    {
        return alertType;
    }

    public void setAlertType(String alertType)
    {
        this.alertType = alertType;
    }

    public String getAlertMessage()
    {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage)
    {
        this.alertMessage = alertMessage;
    }

    public Date getAlertTime()
    {
        return alertTime;
    }

    public void setAlertTime(Date alertTime)
    {
        this.alertTime = alertTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("appId", getAppId())
            .append("productName", getProductName())
            .append("appLink", getAppLink())
            .append("storePlatform", getStorePlatform())
            .append("channelType", getChannelType())
            .append("alertType", getAlertType())
            .append("alertMessage", getAlertMessage())
            .append("alertTime", getAlertTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
