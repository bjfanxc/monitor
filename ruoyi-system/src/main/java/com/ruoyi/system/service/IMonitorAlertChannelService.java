package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.domain.vo.MonitorTelegramChatOptionVo;

import java.util.List;

/**
 * Alert channel service.
 */
public interface IMonitorAlertChannelService
{
    List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel);

    List<MonitorAlertChannel> selectAlertChannelsByCurrentUser();

    List<MonitorTelegramChatOptionVo> discoverTelegramChats(String botToken);

    int insertMonitorAlertChannel(MonitorAlertChannel channel);

    int updateMonitorAlertChannel(MonitorAlertChannel channel);

    String handleTelegramWebhook(String payload);

    int deleteMonitorAlertChannelById(Long id);
}
