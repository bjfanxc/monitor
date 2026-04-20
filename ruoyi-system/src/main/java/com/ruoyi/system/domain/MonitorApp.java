package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * App monitor entity.
 */
public class MonitorApp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "产品名称")
    private String productName;

    @Excel(name = "应用链接")
    private String appLink;

    @Excel(name = "商店平台")
    private String storePlatform;

    /**
     * Legacy single-channel field, kept only for compatibility with historical rows.
     */
    private String ownerType;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastScanTime;

    private String delFlag;

    private String keyword;

    /**
     * Multiple alert channel assignments for add/edit/batch assign.
     */
    private List<Long> alertChannelIds = new ArrayList<>();

    /**
     * Display text for assigned channel names in the list page.
     */
    private String alertChannelNames;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100个字符")
    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    @NotBlank(message = "应用链接不能为空")
    @Size(max = 512, message = "应用链接长度不能超过512个字符")
    public String getAppLink()
    {
        return appLink;
    }

    public void setAppLink(String appLink)
    {
        this.appLink = appLink;
    }

    @NotBlank(message = "商店平台不能为空")
    @Size(max = 64, message = "商店平台长度不能超过64个字符")
    public String getStorePlatform()
    {
        return storePlatform;
    }

    public void setStorePlatform(String storePlatform)
    {
        this.storePlatform = storePlatform;
    }

    @Size(max = 32, message = "渠道来源长度不能超过32个字符")
    public String getOwnerType()
    {
        return ownerType;
    }

    public void setOwnerType(String ownerType)
    {
        this.ownerType = ownerType;
    }

    @Min(value = 0, message = "应用状态只能是0或1")
    @Max(value = 1, message = "应用状态只能是0或1")
    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Date getLastScanTime()
    {
        return lastScanTime;
    }

    public void setLastScanTime(Date lastScanTime)
    {
        this.lastScanTime = lastScanTime;
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

    public List<Long> getAlertChannelIds()
    {
        return alertChannelIds;
    }

    public void setAlertChannelIds(List<Long> alertChannelIds)
    {
        this.alertChannelIds = alertChannelIds;
    }

    public String getAlertChannelNames()
    {
        return alertChannelNames;
    }

    public void setAlertChannelNames(String alertChannelNames)
    {
        this.alertChannelNames = alertChannelNames;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productName", getProductName())
            .append("appLink", getAppLink())
            .append("storePlatform", getStorePlatform())
            .append("ownerType", getOwnerType())
            .append("status", getStatus())
            .append("lastScanTime", getLastScanTime())
            .append("delFlag", getDelFlag())
            .append("alertChannelIds", getAlertChannelIds())
            .append("alertChannelNames", getAlertChannelNames())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
