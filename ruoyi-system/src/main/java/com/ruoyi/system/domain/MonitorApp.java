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

import java.util.Date;

/**
 * 应用监控对象 monitor_app
 */
public class MonitorApp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 应用名称 */
    @Excel(name = "应用名称")
    private String appName;

    /** 应用链接 */
    @Excel(name = "应用链接")
    private String appLink;

    /** 商店平台 */
    // @Excel(name = "商店平台") // Import template intentionally hides this field.
    private String storePlatform;

    /** 告警配置 */
    // @Excel(name = "告警配置") // Import template intentionally hides this field.
    private String ownerType;

    /** 状态：1上架 0下架 */
    // @Excel(name = "状态", readConverterExp = "1=上架,0=下架") // Import template intentionally hides this field.
    private Integer status;

    /** 最近扫描时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastScanTime;

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

    @NotBlank(message = "产品名称不能为空")
    @Size(min = 0, max = 100, message = "产品名称长度不能超过100个字符")
    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    @NotBlank(message = "应用名称不能为空")
    @Size(min = 0, max = 100, message = "应用名称长度不能超过100个字符")
    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    @NotBlank(message = "应用链接不能为空")
    @Size(min = 0, max = 512, message = "应用链接长度不能超过512个字符")
    public String getAppLink()
    {
        return appLink;
    }

    public void setAppLink(String appLink)
    {
        this.appLink = appLink;
    }

    @NotBlank(message = "商店平台不能为空")
    @Size(min = 0, max = 512, message = "商店平台长度不能超过512个字符")
    public String getStorePlatform()
    {
        return storePlatform;
    }

    public void setStorePlatform(String storePlatform)
    {
        this.storePlatform = storePlatform;
    }

    @NotBlank(message = "告警配置不能为空")
    @Size(min = 0, max = 32, message = "告警配置长度不能超过32个字符")
    public String getOwnerType()
    {
        return ownerType;
    }

    public void setOwnerType(String ownerType)
    {
        this.ownerType = ownerType;
    }

    @Min(value = 0, message = "应用状态只能为0或1")
    @Max(value = 1, message = "应用状态只能为0或1")
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productName", getProductName())
            .append("appName", getAppName())
            .append("appLink", getAppLink())
            .append("storePlatform", getStorePlatform())
            .append("ownerType", getOwnerType())
            .append("status", getStatus())
            .append("lastScanTime", getLastScanTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
