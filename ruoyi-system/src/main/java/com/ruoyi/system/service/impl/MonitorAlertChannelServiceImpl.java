package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.mapper.MonitorAlertChannelMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.IMonitorAlertChannelService;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitorAlertChannelServiceImpl implements IMonitorAlertChannelService
{
    private static final String TELEGRAM = "telegram";
    private static final String ACCESS_MODE_WEBHOOK = "webhook";
    private static final String ACCESS_MODE_CUSTOM = "custom";
    private static final String TELEGRAM_BOT_TOKEN_CONFIG_KEY = "monitor.telegram.botToken";
    private static final String TELEGRAM_BIND_KEYWORD_CONFIG_KEY = "monitor.telegram.bindKeyword";
    private static final String WEBHOOK_OPERATOR = "telegram-webhook";

    @Autowired
    private MonitorAlertChannelMapper monitorAlertChannelMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel)
    {
        channel.setChannelType(TELEGRAM);
        applyDataPermission(channel);
        return monitorAlertChannelMapper.selectTelegramChannelList(channel);
    }

    @Override
    public List<MonitorAlertChannel> selectAlertChannelsByCurrentUser()
    {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username))
        {
            return new ArrayList<>();
        }
        if (SecurityUtils.isAdmin())
        {
            MonitorAlertChannel query = new MonitorAlertChannel();
            query.setChannelType(TELEGRAM);
            return monitorAlertChannelMapper.selectTelegramChannelList(query);
        }
        return monitorAlertChannelMapper.selectAlertChannelsByCreateBy(username);
    }

    @Override
    public int insertMonitorAlertChannel(MonitorAlertChannel channel)
    {
        channel.setChannelType(TELEGRAM);
        normalizeChannel(channel, true);
        return monitorAlertChannelMapper.insertMonitorAlertChannel(channel);
    }

    @Override
    public int updateMonitorAlertChannel(MonitorAlertChannel channel)
    {
        if (channel.getId() == null)
        {
            throw new ServiceException("Channel ID cannot be empty");
        }
        MonitorAlertChannel existing = ensureMonitorAlertChannelExists(channel.getId());
        channel.setChannelType(TELEGRAM);
        if (StringUtils.isBlank(channel.getCreateBy()))
        {
            channel.setCreateBy(existing.getCreateBy());
        }
        normalizeChannel(channel, false);
        return monitorAlertChannelMapper.updateMonitorAlertChannel(channel);
    }

    @Override
    public String handleTelegramWebhook(String payload)
    {
        JSONObject root = JSON.parseObject(payload);
        if (root == null)
        {
            return "ignored: empty payload";
        }
        JSONObject message = firstMessageNode(root);
        if (message == null)
        {
            return "ignored: no message node";
        }

        String text = StringUtils.trim(message.getString("text"));
        if (StringUtils.isBlank(text))
        {
            text = StringUtils.trim(message.getString("caption"));
        }
        if (StringUtils.isBlank(text))
        {
            return "ignored: no text";
        }

        String bindTarget = extractBindTarget(text);
        if (StringUtils.isBlank(bindTarget))
        {
            return "ignored: no bind command";
        }

        JSONObject chat = message.getJSONObject("chat");
        if (chat == null || chat.get("id") == null)
        {
            throw new ServiceException("Telegram chatId missing");
        }

        String chatId = String.valueOf(chat.get("id"));
        String chatName = resolveChatName(chat);
        SysUser bindUser = resolveBindUser(bindTarget);
        if (bindUser == null)
        {
            throw new ServiceException("No matching user found: " + bindTarget);
        }
        if (!"0".equals(bindUser.getStatus()))
        {
            throw new ServiceException("The target user is disabled and cannot be bound: " + bindTarget);
        }

        bindTelegramChat(bindUser, chatId, chatName);
        return "success: " + bindUser.getUserName() + " <= " + chatId;
    }

    @Override
    public int deleteMonitorAlertChannelById(Long id)
    {
        ensureMonitorAlertChannelExists(id);
        return monitorAlertChannelMapper.deleteMonitorAlertChannelById(id);
    }

    private void normalizeChannel(MonitorAlertChannel channel, boolean isCreate)
    {
        if (channel == null)
        {
            return;
        }
        channel.setName(StringUtils.trim(channel.getName()));
        channel.setChatId(StringUtils.trim(channel.getChatId()));
        channel.setBotToken(StringUtils.trim(channel.getBotToken()));
        channel.setAccessMode(StringUtils.defaultIfBlank(StringUtils.trim(channel.getAccessMode()), ACCESS_MODE_CUSTOM));
        if (channel.getEnabled() == null)
        {
            channel.setEnabled(1);
        }
        if (!ACCESS_MODE_WEBHOOK.equals(channel.getAccessMode()) && !ACCESS_MODE_CUSTOM.equals(channel.getAccessMode()))
        {
            throw new ServiceException("Unsupported channel access mode");
        }
        if (ACCESS_MODE_WEBHOOK.equals(channel.getAccessMode()))
        {
            channel.setBotToken(StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_BOT_TOKEN_CONFIG_KEY)));
            if (StringUtils.isBlank(channel.getRemark()))
            {
                channel.setRemark("Platform bot webhook binding");
            }
        }
        else
        {
            if (StringUtils.isBlank(channel.getBotToken()))
            {
                throw new ServiceException("Custom bot token cannot be empty");
            }
            if (StringUtils.isBlank(channel.getRemark()))
            {
                channel.setRemark("Custom manual channel");
            }
        }
        if (StringUtils.isBlank(channel.getName()))
        {
            throw new ServiceException("Channel name cannot be empty");
        }
        if (StringUtils.isBlank(channel.getChatId()))
        {
            throw new ServiceException("Group Chat ID cannot be empty");
        }
        if (isCreate && StringUtils.isBlank(channel.getCreateBy()))
        {
            channel.setCreateBy(SecurityUtils.getUsername());
        }
    }

    private JSONObject firstMessageNode(JSONObject root)
    {
        JSONObject message = root.getJSONObject("message");
        if (message != null)
        {
            return message;
        }
        message = root.getJSONObject("channel_post");
        if (message != null)
        {
            return message;
        }
        message = root.getJSONObject("edited_message");
        if (message != null)
        {
            return message;
        }
        return root.getJSONObject("edited_channel_post");
    }

/*    private String extractBindTarget(String text)
    {
        String content = StringUtils.trim(text);
        if (StringUtils.startsWithIgnoreCase(content, "/start"))
        {
            content = StringUtils.trim(content.substring(6));
        }
        String bindKeyword = StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_BIND_KEYWORD_CONFIG_KEY));
        if (StringUtils.isBlank(bindKeyword))
        {
            bindKeyword = "绑定机器人";
        }
        if (!StringUtils.startsWith(content, bindKeyword))
        {
            return null;
        }
        String target = StringUtils.substringAfter(content, bindKeyword);
        target = StringUtils.removeStart(target, ":");
        target = StringUtils.removeStart(target, "：");
        return StringUtils.trim(target);
    }

*/
    private String extractBindTarget(String text)
    {
        String content = StringUtils.trim(text);
        if (StringUtils.startsWithIgnoreCase(content, "/start"))
        {
            content = StringUtils.trim(content.substring(6));
        }
        String bindKeyword = StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_BIND_KEYWORD_CONFIG_KEY));
        if (StringUtils.isBlank(bindKeyword))
        {
            bindKeyword = "绑定机器人";
        }
        if (!StringUtils.startsWith(content, bindKeyword))
        {
            return null;
        }
        String target = StringUtils.substringAfter(content, bindKeyword);
        target = StringUtils.removeStart(target, ":");
        target = StringUtils.removeStart(target, "：");
        return StringUtils.trim(target);
    }

    private SysUser resolveBindUser(String bindTarget)
    {
        if (StringUtils.isBlank(bindTarget))
        {
            return null;
        }
        if (StringUtils.contains(bindTarget, "@"))
        {
            return sysUserMapper.checkEmailUnique(bindTarget);
        }
        return sysUserMapper.selectUserByUserName(bindTarget);
    }

    private String resolveChatName(JSONObject chat)
    {
        String title = StringUtils.trim(chat.getString("title"));
        if (StringUtils.isNotBlank(title))
        {
            return title;
        }
        String username = StringUtils.trim(chat.getString("username"));
        if (StringUtils.isNotBlank(username))
        {
            return username;
        }
        String firstName = StringUtils.trim(chat.getString("first_name"));
        String lastName = StringUtils.trim(chat.getString("last_name"));
        return StringUtils.trim((StringUtils.defaultString(firstName) + " " + StringUtils.defaultString(lastName)).trim());
    }

    private void bindTelegramChat(SysUser bindUser, String chatId, String chatName)
    {
        MonitorAlertChannel channel = new MonitorAlertChannel();
        channel.setChannelType(TELEGRAM);
        channel.setAccessMode(ACCESS_MODE_WEBHOOK);
        channel.setName(StringUtils.isNotBlank(chatName) ? chatName : bindUser.getNickName());
        channel.setChatId(chatId);
        channel.setEnabled(1);
        channel.setBotToken(StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_BOT_TOKEN_CONFIG_KEY)));
        channel.setRemark("Webhook auto binding, user: " + bindUser.getUserName());
        channel.setCreateBy(bindUser.getUserName());
        channel.setUpdateBy(WEBHOOK_OPERATOR);

        MonitorAlertChannel existing = monitorAlertChannelMapper.selectTelegramChannelByChatId(chatId);
        if (existing == null)
        {
            monitorAlertChannelMapper.insertMonitorAlertChannel(channel);
            return;
        }
        channel.setId(existing.getId());
        if (StringUtils.isBlank(channel.getName()))
        {
            channel.setName(existing.getName());
        }
        monitorAlertChannelMapper.updateMonitorAlertChannel(channel);
    }

    private MonitorAlertChannel ensureMonitorAlertChannelExists(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("Channel ID cannot be empty");
        }
        MonitorAlertChannel channel = monitorAlertChannelMapper.selectMonitorAlertChannelById(id);
        if (channel == null)
        {
            throw new ServiceException("Alert channel does not exist or has been deleted");
        }
        checkDataPermission(channel.getCreateBy());
        return channel;
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
            throw new ServiceException("No permission to operate other users' data");
        }
    }
}
