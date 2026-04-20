package com.ruoyi.system.domain.vo;

/**
 * Telegram chat option.
 */
public class MonitorTelegramChatOptionVo
{
    private String chatId;

    private String name;

    private String chatType;

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getChatType()
    {
        return chatType;
    }

    public void setChatType(String chatType)
    {
        this.chatType = chatType;
    }
}
