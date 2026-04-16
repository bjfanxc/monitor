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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 搴旂敤鐩戞帶绠＄悊
 * 
 */
@RestController
@RequestMapping("/monitor/app")
public class MonitorAppController extends BaseController
{
    @Autowired
    private IMonitorAppService monitorAppService;

    /**
     * 鑾峰彇搴旂敤鐩戞帶姒傝
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(monitorAppService.selectMonitorAppOverview());
    }

    /**
     * 鑾峰彇搴旂敤鍒楄〃
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
     * 鏂板搴旂敤
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:add')")
    @Log(title = "搴旂敤鐩戞帶", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setCreateBy(getUsername());
        return toAjax(monitorAppService.insertMonitorApp(monitorApp));
    }

    /**
     * 淇敼搴旂敤
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:edit')")
    @Log(title = "搴旂敤鐩戞帶", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MonitorApp monitorApp)
    {
        monitorApp.setUpdateBy(getUsername());
        return toAjax(monitorAppService.updateMonitorApp(monitorApp));
    }

    /**
     * 鎵归噺瀵煎叆搴旂敤
     */
    @Log(title = "搴旂敤鐩戞帶", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<MonitorApp>(MonitorApp.class);
        List<MonitorApp> appList = util.importExcel(file.getInputStream());
        String message = monitorAppService.importMonitorApp(appList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 涓嬭浇瀵煎叆妯℃澘
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<MonitorApp> util = new ExcelUtil<MonitorApp>(MonitorApp.class);
        util.importTemplateExcel(response, "搴旂敤鐩戞帶鏁版嵁");
    }

    /**
     * 鍒犻櫎搴旂敤
     */
    @PreAuthorize("@ss.hasPermi('monitor:app:remove')")
    @Log(title = "搴旂敤鐩戞帶", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(monitorAppService.deleteMonitorAppById(id));
    }

    /**
     * 淇敼搴旂敤鐘舵€?     */
    @PreAuthorize("@ss.hasPermi('monitor:app:status')")
    @Log(title = "搴旂敤鐩戞帶", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult changeStatus(@Validated @RequestBody MonitorAppStatusDto statusDto)
    {
        return toAjax(monitorAppService.updateMonitorAppStatus(statusDto, getUsername()));
    }
}
