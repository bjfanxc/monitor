package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 告警渠道对象 monitor_alert_channel
 */
public class MonitorAlertChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 渠道类型 */
    private String channelType;

    /** 渠道名称 */
    private String name;

    /** Telegram Bot Token */
    private String botToken;

    /** Telegram Chat ID */
    private String chatId;

    /** 启用状态：1启用 0禁用 */
    private Integer enabled;

    /** 删除标记 */
    private String delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getChannelType()
    {
        return channelType;
    }

    public void setChannelType(String channelType)
    {
        this.channelType = channelType;
    }

    @NotBlank(message = "渠道名称不能为空")
    @Size(min = 0, max = 100, message = "渠道名称长度不能超过100个字符")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBotToken()
    {
        return botToken;
    }

    public void setBotToken(String botToken)
    {
        this.botToken = botToken;
    }

    @NotBlank(message = "Chat ID不能为空")
    @Size(min = 0, max = 64, message = "Chat ID长度不能超过64个字符")
    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    @Min(value = 0, message = "启用状态只能为0或1")
    @Max(value = 1, message = "启用状态只能为0或1")
    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("channelType", getChannelType())
            .append("name", getName())
            .append("botToken", getBotToken())
            .append("chatId", getChatId())
            .append("enabled", getEnabled())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
