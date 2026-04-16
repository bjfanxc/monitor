package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
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
        applyDataPermission(channel);
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
        checkDataPermission(channel.getCreateBy());
    }

    private void applyDataPermission(MonitorAlertChannel channel)
    {
        if (channel == null || SecurityUtils.isAdmin())
        {
            return;
        }
        channel.getParams().put("currentUsername", SecurityUtils.getUsername());
    }

    private void checkDataPermission(String createBy)
    {
        if (!SecurityUtils.isAdmin() && !StringUtils.equals(SecurityUtils.getUsername(), createBy))
        {
            throw new ServiceException("无权操作其他用户的数据");
        }
    }
}
