package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MonitorPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String planCode;

    private String planName;

    private Integer maxAlertChannels;

    private Integer maxApps;

    private Integer scanIntervalMinutes;

    private Integer sortOrder;

    private Integer isDefault;

    private String status;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotBlank(message = "套餐编码不能为空")
    @Size(max = 32, message = "套餐编码长度不能超过32个字符")
    public String getPlanCode()
    {
        return planCode;
    }

    public void setPlanCode(String planCode)
    {
        this.planCode = planCode;
    }

    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 64, message = "套餐名称长度不能超过64个字符")
    public String getPlanName()
    {
        return planName;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    @NotNull(message = "群组上限不能为空")
    @Min(value = 0, message = "群组上限不能小于0")
    public Integer getMaxAlertChannels()
    {
        return maxAlertChannels;
    }

    public void setMaxAlertChannels(Integer maxAlertChannels)
    {
        this.maxAlertChannels = maxAlertChannels;
    }

    @NotNull(message = "应用上限不能为空")
    @Min(value = 0, message = "应用上限不能小于0")
    public Integer getMaxApps()
    {
        return maxApps;
    }

    public void setMaxApps(Integer maxApps)
    {
        this.maxApps = maxApps;
    }

    @NotNull(message = "扫描间隔不能为空")
    @Min(value = 1, message = "扫描间隔不能小于1分钟")
    public Integer getScanIntervalMinutes()
    {
        return scanIntervalMinutes;
    }

    public void setScanIntervalMinutes(Integer scanIntervalMinutes)
    {
        this.scanIntervalMinutes = scanIntervalMinutes;
    }

    @NotNull(message = "排序号不能为空")
    @Min(value = 0, message = "排序号不能小于0")
    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault)
    {
        this.isDefault = isDefault;
    }

    @NotBlank(message = "状态不能为空")
    @Size(max = 1, message = "状态长度不能超过1个字符")
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("planCode", getPlanCode())
            .append("planName", getPlanName())
            .append("maxAlertChannels", getMaxAlertChannels())
            .append("maxApps", getMaxApps())
            .append("scanIntervalMinutes", getScanIntervalMinutes())
            .append("sortOrder", getSortOrder())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
