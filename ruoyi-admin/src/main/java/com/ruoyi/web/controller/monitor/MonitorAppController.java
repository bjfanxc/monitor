package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppImportResultVo;
import com.ruoyi.system.domain.vo.MonitorAppScanResultVo;
import com.ruoyi.system.service.IMonitorAppService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor/app")
public class MonitorAppController extends BaseController
{
    @Autowired
    private IMonitorAppService monitorAppService;

    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(monitorAppService.selectMonitorAppOverview());
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(monitorAppService.selectMonitorAppFormOptions());
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorApp monitorApp)
    {
        startPage();
        return getDataTable(monitorAppService.selectMonitorAppList(monitorApp));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:add')")
    @Log(title = "应用监控", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setCreateBy(getUsername());
        return toAjax(monitorAppService.insertMonitorApp(monitorApp));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:edit')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setUpdateBy(getUsername());
        return toAjax(monitorAppService.updateMonitorApp(monitorApp));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:edit')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping("/assignChannels")
    public AjaxResult assignChannels(@Validated @RequestBody AssignChannelsBody body)
    {
        int rows = monitorAppService.assignAlertChannels(body.getAppIds(), body.getChannelIds(), getUsername());
        return AjaxResult.success("Assigned alert groups for " + rows + " product(s)");
    }

    @Log(title = "应用监控", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<>(MonitorApp.class);
        List<MonitorApp> appList = util.importExcel(file.getInputStream());
        MonitorAppImportResultVo result = monitorAppService.importMonitorApp(appList, updateSupport, getUsername());
        return AjaxResult.success(result.getMessage(), result);
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<>(MonitorApp.class);
        util.importTemplateExcel(response, "应用监控导入模版");
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:remove')")
    @Log(title = "应用监控", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAppService.deleteMonitorAppById(id));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:status')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult changeStatus(@Validated @RequestBody MonitorAppStatusDto statusDto)
    {
        return toAjax(monitorAppService.updateMonitorAppStatus(statusDto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:status')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping("/scan/{id}")
    public AjaxResult scan(@PathVariable Long id, @RequestParam(value = "mode", required = false) String mode)
    {
        return success(monitorAppService.scanGooglePlayApp(id, getUsername(), mode));
    }

    @PreAuthorize("@ss.hasPermi('monitor:app:status')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping("/scanAll")
    public AjaxResult scanAll(@RequestParam(value = "mode", required = false) String mode)
    {
        List<MonitorAppScanResultVo> results = monitorAppService.scanGooglePlayApps(getUsername(), mode);
        long changedCount = results.stream().filter(MonitorAppScanResultVo::isChanged).count();
        Map<String, Object> data = new HashMap<>(4);
        data.put("total", results.size());
        data.put("changed", changedCount);
        data.put("mode", mode);
        data.put("results", results);
        return success(data);
    }

    public static class AssignChannelsBody
    {
        @NotEmpty(message = "请选择产品")
        private List<Long> appIds;

        private List<Long> channelIds;

        public List<Long> getAppIds()
        {
            return appIds;
        }

        public void setAppIds(List<Long> appIds)
        {
            this.appIds = appIds;
        }

        public List<Long> getChannelIds()
        {
            return channelIds;
        }

        public void setChannelIds(List<Long> channelIds)
        {
            this.channelIds = channelIds;
        }
    }
}
