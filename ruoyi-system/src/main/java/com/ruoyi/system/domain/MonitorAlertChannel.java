package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 鍛婅娓犻亾瀵硅薄 monitor_alert_channel
 * 
 */
public class MonitorAlertChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 涓婚敭 */
    private Long id;

    /** 娓犻亾绫诲瀷 */
    private String channelType;

    /** 娓犻亾鍚嶇О */
    private String name;

    /** Telegram Bot Token */
    private String botToken;

    /** Telegram Chat ID */
    private String chatId;

    /** 鏄惁鍚敤锛?鍚敤 0绂佺敤 */
    private Integer enabled;

    /** 鍒犻櫎鏍囪 */
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

    @NotBlank(message = "娓犻亾鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "娓犻亾鍚嶇О闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @NotBlank(message = "Bot Token涓嶈兘涓虹┖")
    @Size(min = 0, max = 255, message = "Bot Token闀垮害涓嶈兘瓒呰繃255涓瓧绗?)
    public String getBotToken()
    {
        return botToken;
    }

    public void setBotToken(String botToken)
    {
        this.botToken = botToken;
    }

    @NotBlank(message = "Chat ID涓嶈兘涓虹┖")
    @Size(min = 0, max = 64, message = "Chat ID闀垮害涓嶈兘瓒呰繃64涓瓧绗?)
    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    @Min(value = 0, message = "鍚敤鐘舵€佸彧鑳戒负0鎴?")
    @Max(value = 1, message = "鍚敤鐘舵€佸彧鑳戒负0鎴?")
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
