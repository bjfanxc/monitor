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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Telegram 鍛婅娓犻亾绠＄悊
 * 
 */
@RestController
@RequestMapping("/monitor/alert/channel/telegram")
public class MonitorAlertChannelController extends BaseController
{
    @Autowired
    private IMonitorAlertChannelService monitorAlertChannelService;

    /**
     * 鑾峰彇 Telegram 娓犻亾鍒楄〃
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
     * 鏂板 Telegram 娓犻亾
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:add')")
    @Log(title = "Telegram鍛婅娓犻亾", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setCreateBy(getUsername());
        return toAjax(monitorAlertChannelService.insertMonitorAlertChannel(channel));
    }

    /**
     * 淇敼 Telegram 娓犻亾
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:edit')")
    @Log(title = "Telegram鍛婅娓犻亾", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorAlertChannel channel)
    {
        channel.setUpdateBy(getUsername());
        return toAjax(monitorAlertChannelService.updateMonitorAlertChannel(channel));
    }

    /**
     * 鍒犻櫎 Telegram 娓犻亾
     */
    @PreAuthorize("@ss.hasPermi('monitor:alert:channel:remove')")
    @Log(title = "Telegram鍛婅娓犻亾", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAlertChannelService.deleteMonitorAlertChannelById(id));
    }
}
