package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.service.IMonitorAppService;
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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 应用监控管理
 */
@RestController
@RequestMapping("/monitor/app")
public class MonitorAppController extends BaseController
{
    @Autowired
    private IMonitorAppService monitorAppService;

    /**
     * 获取应用监控概览
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(monitorAppService.selectMonitorAppOverview());
    }

    /**
     * 获取应用监控列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorApp monitorApp)
    {
        startPage();
        List<MonitorApp> list = monitorAppService.selectMonitorAppList(monitorApp);
        return getDataTable(list);
    }

    /**
     * 新增应用监控
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:add')")
    @Log(title = "应用监控", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setCreateBy(getUsername());
        return toAjax(monitorAppService.insertMonitorApp(monitorApp));
    }

    /**
     * 修改应用监控
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:edit')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setUpdateBy(getUsername());
        return toAjax(monitorAppService.updateMonitorApp(monitorApp));
    }

    /**
     * 导入应用监控数据
     */
    @Log(title = "应用监控", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<>(MonitorApp.class);
        List<MonitorApp> appList = util.importExcel(file.getInputStream());
        String message = monitorAppService.importMonitorApp(appList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<>(MonitorApp.class);
        util.importTemplateExcel(response, "应用监控导入模板");
    }

    /**
     * 删除应用监控
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:remove')")
    @Log(title = "应用监控", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAppService.deleteMonitorAppById(id));
    }

    /**
     * 修改应用状态
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:status')")
    @Log(title = "应用监控", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult changeStatus(@Validated @RequestBody MonitorAppStatusDto statusDto)
    {
        return toAjax(monitorAppService.updateMonitorAppStatus(statusDto, getUsername()));
    }
}
