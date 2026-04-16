package com.ruoyi.system.domain.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 应用状态修改参�? * 
 */
public class MonitorAppStatusDto
{
    /** 应用ID */
    @NotNull(message = "应用ID不能为空")
    private Long id;

    /** 状态：1上架 0下架 */
    @NotNull(message = "应用状态不能为空")
    @Min(value = 0, message = "应用状态只能为0�?")
    @Max(value = 1, message = "应用状态只能为0�?")
    private Integer status;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }
}
