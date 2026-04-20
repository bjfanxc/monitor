package com.ruoyi.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Telegram chat discovery request.
 */
public class MonitorTelegramChatDiscoverDto
{
    @NotBlank(message = "机器人Token不能为空")
    @Size(max = 255, message = "机器人Token长度不能超过255个字符")
    private String botToken;

    public String getBotToken()
    {
        return botToken;
    }

    public void setBotToken(String botToken)
    {
        this.botToken = botToken;
    }
}
