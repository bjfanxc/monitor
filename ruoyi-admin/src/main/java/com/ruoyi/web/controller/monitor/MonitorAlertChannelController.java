package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.service.IMonitorAlertChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Telegram 告警渠道管理
 */
@RestController
@RequestMapping("/monitor/alert/channel/telegram")
public class MonitorAlertChannelController extends BaseController
{
    @Autowired
    private IMonitorAlertChannelService monitorAlertChannelService;

    /**
     * 获取 Telegram 渠道列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorAlertChannel channel)
    {
        startPage();
        List<MonitorAlertChannel> list = monitorAlertChannelService.selectTelegramChannelList(channel);
        return getDataTable(list);
    }

    /**
     * 新增 Telegram 渠道
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:add')")
    @Log(title = "Telegram告警渠道", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setCreateBy(getUsername());
        return toAjax(monitorAlertChannelService.insertMonitorAlertChannel(channel));
    }

    /**
     * 修改 Telegram 渠道
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:edit')")
    @Log(title = "Telegram告警渠道", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setUpdateBy(getUsername());
        return toAjax(monitorAlertChannelService.updateMonitorAlertChannel(channel));
    }

    /**
     * 删除 Telegram 渠道
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:remove')")
    @Log(title = "Telegram告警渠道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAlertChannelService.deleteMonitorAlertChannelById(id));
    }
}
