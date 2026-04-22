package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.MonitorPlan;
import com.ruoyi.system.service.IMonitorPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor/plan")
public class MonitorPlanController extends BaseController {
    @Autowired
    private IMonitorPlanService monitorPlanService;

    @PreAuthorize("@ss.hasPermi('monitor:plan:list')")
    @GetMapping("/list")
    public TableDataInfo list(MonitorPlan monitorPlan) {
        startPage();
        return getDataTable(monitorPlanService.selectMonitorPlanList(monitorPlan));
    }

    @PreAuthorize("@ss.hasPermi('monitor:plan:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(monitorPlanService.selectMonitorPlanById(id));
    }

    @PreAuthorize("@ss.hasPermi('monitor:plan:query')")
    @GetMapping("/options")
    public AjaxResult options() {
        return success(monitorPlanService.selectEnabledPlanOptions());
    }

    @PreAuthorize("@ss.hasPermi('monitor:plan:add')")
    @Log(title = "套餐管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody MonitorPlan monitorPlan) {
        monitorPlan.setCreateBy(getUsername());
        return toAjax(monitorPlanService.insertMonitorPlan(monitorPlan));
    }

    @PreAuthorize("@ss.hasPermi('monitor:plan:edit')")
    @Log(title = "套餐管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody MonitorPlan monitorPlan) {
        monitorPlan.setUpdateBy(getUsername());
        return toAjax(monitorPlanService.updateMonitorPlan(monitorPlan));
    }

    @PreAuthorize("@ss.hasPermi('monitor:plan:remove')")
    @Log(title = "套餐管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(monitorPlanService.deleteMonitorPlanByIds(ids));
    }

    @PreAuthorize("@ss.hasAnyPermi('monitor:app:list,monitor:alert:list,monitor:alert:channel:list')")
    @GetMapping("/currentQuota")
    public AjaxResult currentQuota() {
        return success(monitorPlanService.buildCurrentUserQuota());
    }
}
