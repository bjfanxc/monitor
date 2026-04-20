package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Google Play 搴旂敤鎵弿缁撴灉
 */
public class MonitorAppScanResultVo
{
    private Long id;
    private String productName;
    private String bundleId;
    private String region;
    private String scanMode;
    private Integer previousStatus;
    private Integer currentStatus;
    private boolean changed;
    private boolean reachable;
    private boolean notified;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBundleId() { return bundleId; }
    public void setBundleId(String bundleId) { this.bundleId = bundleId; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getScanMode() { return scanMode; }
    public void setScanMode(String scanMode) { this.scanMode = scanMode; }
    public Integer getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(Integer previousStatus) { this.previousStatus = previousStatus; }
    public Integer getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(Integer currentStatus) { this.currentStatus = currentStatus; }
    public boolean isChanged() { return changed; }
    public void setChanged(boolean changed) { this.changed = changed; }
    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }
    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getCheckedAt() { return checkedAt; }
    public void setCheckedAt(Date checkedAt) { this.checkedAt = checkedAt; }
}
