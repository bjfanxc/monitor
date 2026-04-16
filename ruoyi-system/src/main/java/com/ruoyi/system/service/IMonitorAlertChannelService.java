package com.ruoyi.system.service;

import com.ruoyi.system.domain.MonitorAlertChannel;

import java.util.List;

/**
 * 告警渠道服务接口
 */
public interface IMonitorAlertChannelService
{
    /**
     * 获取 Telegram 渠道列表
     *
     * @param channel 查询条件
     * @return 渠道列表
     */
    List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel);

    /**
     * 新增告警渠道
     *
     * @param channel 渠道信息
     * @return 结果
     */
    int insertMonitorAlertChannel(MonitorAlertChannel channel);

    /**
     * 修改告警渠道
     *
     * @param channel 渠道信息
     * @return 结果
     */
    int updateMonitorAlertChannel(MonitorAlertChannel channel);

    /**
     * 删除告警渠道
     *
     * @param id 渠道ID
     * @return 结果
     */
    int deleteMonitorAlertChannelById(Long id);
}
