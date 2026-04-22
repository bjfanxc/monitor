package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Alert channel entity.
 */
public class MonitorAlertChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String channelType;

    private String accessMode;

    private String name;

    private String botToken;

    private String chatId;

    private Integer enabled;

    private String delFlag;

    /**
     * Only used for batch app-channel backfill.
     */
    private Long appId;

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

    @NotBlank(message = "添加方式不能为空")
    @Size(max = 32, message = "添加方式长度不能超过32个字符")
    public String getAccessMode()
    {
        return accessMode;
    }

    public void setAccessMode(String accessMode)
    {
        this.accessMode = accessMode;
    }

    @NotBlank(message = "渠道名称不能为空")
    @Size(max = 100, message = "渠道名称长度不能超过100个字符")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Size(max = 255, message = "机器人 Token 长度不能超过255个字符")
    public String getBotToken()
    {
        return botToken;
    }

    public void setBotToken(String botToken)
    {
        this.botToken = botToken;
    }

    @NotBlank(message = "群组 Chat ID 不能为空")
    @Size(max = 64, message = "群组 Chat ID 长度不能超过64个字符")
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

    public Long getAppId()
    {
        return appId;
    }

    public void setAppId(Long appId)
    {
        this.appId = appId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("channelType", getChannelType())
            .append("accessMode", getAccessMode())
            .append("name", getName())
            .append("botToken", getBotToken())
            .append("chatId", getChatId())
            .append("enabled", getEnabled())
            .append("delFlag", getDelFlag())
            .append("appId", getAppId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
