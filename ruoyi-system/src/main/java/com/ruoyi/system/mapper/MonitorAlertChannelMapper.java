package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorAlertChannel;

import java.util.List;

/**
 * Alert channel mapper.
 */
public interface MonitorAlertChannelMapper
{
    MonitorAlertChannel selectMonitorAlertChannelById(Long id);

    List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel);

    List<MonitorAlertChannel> selectAlertChannelsByCreateBy(String createBy);

    List<MonitorAlertChannel> selectEnabledTelegramChannelsByCreateBy(String createBy);

    List<MonitorAlertChannel> selectAssignedChannelsByAppId(Long appId);

    List<MonitorAlertChannel> selectEnabledChannelsByAppId(Long appId);

    MonitorAlertChannel selectTelegramChannelByChatId(String chatId);

    int insertMonitorAlertChannel(MonitorAlertChannel channel);

    int updateMonitorAlertChannel(MonitorAlertChannel channel);

    int deleteMonitorAlertChannelById(Long id);
}
