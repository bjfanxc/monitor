package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 告警记录对象 monitor_alert_record
 */
public class MonitorAlertRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 应用ID */
    private Long appId;

    /** 产品名称 */
    private String productName;

    /** 应用名称 */
    private String appName;

    /** 包名 */
    private String bundleId;

    /** 商店平台 */
    private String storePlatform;

    /** 地区 */
    private String region;

    /** 告警渠道 */
    private String channelType;

    /** 告警类型 */
    private String alertType;

    /** 告警内容 */
    private String alertMessage;

    /** 告警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alertTime;

    /** 扩展信息 */
    private String extJson;

    /** 删除标记 */
    private String delFlag;

    /** 关键字 */
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

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getBundleId()
    {
        return bundleId;
    }

    public void setBundleId(String bundleId)
    {
        this.bundleId = bundleId;
    }

    public String getStorePlatform()
    {
        return storePlatform;
    }

    public void setStorePlatform(String storePlatform)
    {
        this.storePlatform = storePlatform;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
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

    public String getExtJson()
    {
        return extJson;
    }

    public void setExtJson(String extJson)
    {
        this.extJson = extJson;
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
            .append("appName", getAppName())
            .append("bundleId", getBundleId())
            .append("storePlatform", getStorePlatform())
            .append("region", getRegion())
            .append("channelType", getChannelType())
            .append("alertType", getAlertType())
            .append("alertMessage", getAlertMessage())
            .append("alertTime", getAlertTime())
            .append("extJson", getExtJson())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
