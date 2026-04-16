package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.mapper.MonitorAlertChannelMapper;
import com.ruoyi.system.service.IMonitorAlertChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警渠道服务实现
 */
@Service
public class MonitorAlertChannelServiceImpl implements IMonitorAlertChannelService
{
    private static final String TELEGRAM = "telegram";

    @Autowired
    private MonitorAlertChannelMapper monitorAlertChannelMapper;

    @Override
    public List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel)
    {
        channel.setChannelType(TELEGRAM);
        return monitorAlertChannelMapper.selectTelegramChannelList(channel);
    }

    @Override
    public int insertMonitorAlertChannel(MonitorAlertChannel channel)
    {
        channel.setChannelType(TELEGRAM);
        return monitorAlertChannelMapper.insertMonitorAlertChannel(channel);
    }

    @Override
    public int updateMonitorAlertChannel(MonitorAlertChannel channel)
    {
        if (channel.getId() == null)
        {
            throw new ServiceException("渠道ID不能为空");
        }
        ensureMonitorAlertChannelExists(channel.getId());
        channel.setChannelType(TELEGRAM);
        return monitorAlertChannelMapper.updateMonitorAlertChannel(channel);
    }

    @Override
    public int deleteMonitorAlertChannelById(Long id)
    {
        ensureMonitorAlertChannelExists(id);
        return monitorAlertChannelMapper.deleteMonitorAlertChannelById(id);
    }

    private void ensureMonitorAlertChannelExists(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("渠道ID不能为空");
        }
        MonitorAlertChannel channel = monitorAlertChannelMapper.selectMonitorAlertChannelById(id);
        if (channel == null)
        {
            throw new ServiceException("Telegram 渠道不存在或已被删除");
        }
    }
}
