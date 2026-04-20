package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorAlertChannel;

import java.util.List;

/**
 * Alert channel service.
 */
public interface IMonitorAlertChannelService
{
    List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel);

    List<MonitorAlertChannel> selectAlertChannelsByCurrentUser();

    int insertMonitorAlertChannel(MonitorAlertChannel channel);

    int updateMonitorAlertChannel(MonitorAlertChannel channel);

    String handleTelegramWebhook(String payload);

    int deleteMonitorAlertChannelById(Long id);
}
