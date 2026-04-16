package com.ruoyi.system.service.impl;

import com.monitor.common.exception.ServiceException;
import com.monitor.system.domain.MonitorAlertChannel;
import com.monitor.system.mapper.MonitorAlertChannelMapper;
import com.monitor.system.service.IMonitorAlertChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 鍛婅娓犻亾鏈嶅姟瀹炵幇
 * 
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
            throw new ServiceException("娓犻亾ID涓嶈兘涓虹┖");
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
            throw new ServiceException("娓犻亾ID涓嶈兘涓虹┖");
        }
        MonitorAlertChannel channel = monitorAlertChannelMapper.selectMonitorAlertChannelById(id);
        if (channel == null)
        {
            throw new ServiceException("Telegram 娓犻亾涓嶅瓨鍦ㄦ垨宸插垹闄?);
        }
    }
}
