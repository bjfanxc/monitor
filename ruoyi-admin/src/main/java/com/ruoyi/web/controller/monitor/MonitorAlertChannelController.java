package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.service.IMonitorAlertChannelService;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor/alert/channel/telegram")
public class MonitorAlertChannelController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(MonitorAlertChannelController.class);

    @Autowired
    private IMonitorAlertChannelService monitorAlertChannelService;

    @Autowired
    private ISysConfigService sysConfigService;

    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorAlertChannel channel)
    {
        startPage();
        List<MonitorAlertChannel> list = monitorAlertChannelService.selectTelegramChannelList(channel);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:list')")
    @GetMapping("/bindingInfo")
    public AjaxResult bindingInfo()
    {
        Map<String, Object> data = new LinkedHashMap<>(5);
        data.put("botLink", sysConfigService.selectConfigByKey("monitor.telegram.botLink"));
        data.put("botUsername", sysConfigService.selectConfigByKey("monitor.telegram.botUsername"));
        data.put("bindKeyword", sysConfigService.selectConfigByKey("monitor.telegram.bindKeyword"));
        data.put("tokenConfigured", StringUtils.isNotBlank(sysConfigService.selectConfigByKey("monitor.telegram.botToken")));
        data.put("channels", monitorAlertChannelService.selectAlertChannelsByCurrentUser());
        return success(data);
    }

    @Anonymous
    @PostMapping("/webhook")
    public ResponseEntity<AjaxResult> webhook(@RequestBody(required = false) String payload,
                                              @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken)
    {
        log.info("webhook payload={}, secret token={}", payload, secretToken);
        String configuredSecret = sysConfigService.selectConfigByKey("monitor.telegram.webhookSecret");
        if (StringUtils.isNotBlank(configuredSecret) && !configuredSecret.equals(secretToken))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AjaxResult.error("invalid webhook secret"));
        }
        try
        {
            return ResponseEntity.ok(AjaxResult.success(monitorAlertChannelService.handleTelegramWebhook(payload)));
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(AjaxResult.error(e.getMessage()));
        }
    }

    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:add')")
    @Log(title = "告警群组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setCreateBy(getUsername());
        return toAjax(monitorAlertChannelService.insertMonitorAlertChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:edit')")
    @Log(title = "告警群组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setUpdateBy(getUsername());
        return toAjax(monitorAlertChannelService.updateMonitorAlertChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:remove')")
    @Log(title = "告警群组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAlertChannelService.deleteMonitorAlertChannelById(id));
    }
}
