package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.MonitorAlertChannel;
import com.ruoyi.system.domain.MonitorAlertRecord;
import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;
import com.ruoyi.system.domain.vo.MonitorAppScanResultVo;
import com.ruoyi.system.mapper.MonitorAlertChannelMapper;
import com.ruoyi.system.mapper.MonitorAlertRecordMapper;
import com.ruoyi.system.mapper.MonitorAppMapper;
import com.ruoyi.system.service.IMonitorAppService;
import com.ruoyi.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * App monitor service implementation.
 */
@Service
public class MonitorAppServiceImpl implements IMonitorAppService
{
    private static final String SYSTEM_OPERATOR = "system";
    private static final String GOOGLE_PLAY_STORE = "google_play";
    private static final String ALERT_TYPE_OFFLINE = "APP_OFFLINE";
    private static final String ALERT_TYPE_ONLINE = "APP_ONLINE";
    private static final String ALERT_CHANNEL_SYSTEM = "system";
    private static final String ALERT_CHANNEL_TELEGRAM = "telegram";
    private static final String SCAN_MODE_HTTP = "http";
    private static final String SCAN_MODE_PLAYWRIGHT = "playwright";
    private static final String DEFAULT_REGION = "US";
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36";

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    @Autowired
    private MonitorAppMapper monitorAppMapper;

    @Autowired
    private MonitorAlertRecordMapper monitorAlertRecordMapper;

    @Autowired
    private MonitorAlertChannelMapper monitorAlertChannelMapper;

    @Autowired
    private ISysDictDataService sysDictDataService;

    @Override
    public MonitorAppOverviewVo selectMonitorAppOverview()
    {
        MonitorAppOverviewVo overview = monitorAppMapper.selectMonitorAppOverview(buildDataPermissionQuery());
        if (overview == null)
        {
            overview = new MonitorAppOverviewVo();
            overview.setTotalApps(0L);
            overview.setOnlineApps(0L);
            overview.setOfflineApps(0L);
        }
        return overview;
    }

    @Override
    public Map<String, Object> selectMonitorAppFormOptions()
    {
        Map<String, Object> result = new LinkedHashMap<>(3);
        result.put("storePlatforms", buildStorePlatformOptions());
        result.put("regions", buildRegionOptions());
        result.put("ownerTypes", buildOwnerTypeOptions());
        return result;
    }

    @Override
    public List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp)
    {
        applyDataPermission(monitorApp);
        return monitorAppMapper.selectMonitorAppList(monitorApp);
    }

    @Override
    public int insertMonitorApp(MonitorApp monitorApp)
    {
        normalizeMonitorApp(monitorApp);
        validateOwnerType(monitorApp, monitorApp.getCreateBy());
        checkMonitorAppUnique(monitorApp);
        return monitorAppMapper.insertMonitorApp(monitorApp);
    }

    @Override
    public int updateMonitorApp(MonitorApp monitorApp)
    {
        if (monitorApp.getId() == null)
        {
            throw new ServiceException("App ID cannot be null");
        }
        MonitorApp exists = ensureMonitorAppExists(monitorApp.getId());
        normalizeMonitorApp(monitorApp);
        validateOwnerType(monitorApp, exists.getCreateBy());
        checkMonitorAppUnique(monitorApp);
        return monitorAppMapper.updateMonitorApp(monitorApp);
    }

    @Override
    public int deleteMonitorAppById(Long id)
    {
        ensureMonitorAppExists(id);
        return monitorAppMapper.deleteMonitorAppById(id);
    }

    @Override
    public int updateMonitorAppStatus(MonitorAppStatusDto statusDto, String updateBy)
    {
        MonitorApp monitorApp = ensureMonitorAppExists(statusDto.getId());
        monitorApp.setStatus(statusDto.getStatus());
        monitorApp.setUpdateBy(updateBy);
        return monitorAppMapper.updateMonitorAppStatus(monitorApp);
    }

    @Override
    public String importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(appList) || appList.isEmpty())
        {
            throw new ServiceException("Import data cannot be empty");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (MonitorApp monitorApp : appList)
        {
            try
            {
                normalizeMonitorApp(monitorApp);
                validateImportRow(monitorApp);
                monitorApp.setCreateBy(operName);
                monitorApp.setUpdateBy(operName);
                MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
                if (StringUtils.isNull(exists))
                {
                    if (monitorApp.getStatus() == null)
                    {
                        monitorApp.setStatus(1);
                    }
                    if (StringUtils.isBlank(monitorApp.getOwnerType()))
                    {
                        monitorApp.setOwnerType("system");
                    }
                    monitorAppMapper.insertMonitorApp(monitorApp);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append(". App ")
                        .append(monitorApp.getAppName()).append(" imported successfully");
                }
                else if (updateSupport)
                {
                    checkDataPermission(exists.getCreateBy());
                    monitorApp.setId(exists.getId());
                    if (monitorApp.getStatus() == null)
                    {
                        monitorApp.setStatus(exists.getStatus());
                    }
                    if (StringUtils.isBlank(monitorApp.getOwnerType()))
                    {
                        monitorApp.setOwnerType(exists.getOwnerType());
                    }
                    monitorAppMapper.updateMonitorApp(monitorApp);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append(". App ")
                        .append(monitorApp.getAppName()).append(" updated successfully");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append(". App ")
                        .append(monitorApp.getAppName())
                        .append(" already exists, unique key bundleId + storePlatform + region conflicts");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String appName = StringUtils.isNotBlank(monitorApp.getAppName()) ? monitorApp.getAppName() : "Unnamed app";
                failureMsg.append("<br/>").append(failureNum).append(". App ")
                    .append(appName).append(" import failed: ").append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "Import failed, " + failureNum + " rows contain errors:");
            throw new ServiceException(failureMsg.toString());
        }
        return "Import succeeded, total " + successNum + " rows:" + successMsg;
    }

    @Override
    public MonitorAppScanResultVo scanGooglePlayApp(Long id, String operator)
    {
        return scanGooglePlayApp(id, operator, SCAN_MODE_HTTP);
    }

    @Override
    public MonitorAppScanResultVo scanGooglePlayApp(Long id, String operator, String scanMode)
    {
        MonitorApp monitorApp = ensureMonitorAppExists(id);
        return scanGooglePlayAppInternal(monitorApp, resolveOperator(operator), resolveScanMode(scanMode));
    }

    @Override
    public List<MonitorAppScanResultVo> scanGooglePlayApps(String operator)
    {
        return scanGooglePlayApps(operator, SCAN_MODE_HTTP);
    }

    @Override
    public List<MonitorAppScanResultVo> scanGooglePlayApps(String operator, String scanMode)
    {
        List<MonitorAppScanResultVo> results = new ArrayList<>();
        MonitorApp query = new MonitorApp();
        applyDataPermission(query);
        List<MonitorApp> apps = monitorAppMapper.selectMonitorAppList(query);
        String resolvedOperator = resolveOperator(operator);
        String resolvedScanMode = resolveScanMode(scanMode);
        for (MonitorApp monitorApp : apps)
        {
            if (!isGooglePlayApp(monitorApp))
            {
                continue;
            }
            results.add(scanGooglePlayAppInternal(monitorApp, resolvedOperator, resolvedScanMode));
        }
        return results;
    }

    private MonitorAppScanResultVo scanGooglePlayAppInternal(MonitorApp monitorApp, String operator, String scanMode)
    {
        if (!isGooglePlayApp(monitorApp))
        {
            throw new ServiceException("Only Google Play apps can be scanned");
        }

        ScanOutcome outcome = fetchGooglePlayStatus(monitorApp, scanMode);
        Date now = new Date();
        Integer previousStatus = monitorApp.getStatus();
        Integer currentStatus = previousStatus;
        boolean changed = false;
        boolean notified = false;

        if (outcome.isDefinitive())
        {
            currentStatus = outcome.isOnline() ? 1 : 0;
            changed = !Objects.equals(previousStatus, currentStatus);
        }

        MonitorApp updateEntity = new MonitorApp();
        updateEntity.setId(monitorApp.getId());
        updateEntity.setLastScanTime(now);
        updateEntity.setUpdateBy(operator);
        if (outcome.isDefinitive())
        {
            updateEntity.setStatus(currentStatus);
        }
        monitorAppMapper.updateMonitorAppScanInfo(updateEntity);

        if (changed)
        {
            notified = notifyStatusChanged(monitorApp, currentStatus, outcome.getMessage(), now);
        }

        MonitorAppScanResultVo result = new MonitorAppScanResultVo();
        result.setId(monitorApp.getId());
        result.setAppName(monitorApp.getAppName());
        result.setBundleId(monitorApp.getBundleId());
        result.setRegion(monitorApp.getRegion());
        result.setScanMode(scanMode);
        result.setPreviousStatus(previousStatus);
        result.setCurrentStatus(currentStatus);
        result.setChanged(changed);
        result.setReachable(outcome.isOnline());
        result.setNotified(notified);
        result.setMessage(outcome.getMessage());
        result.setCheckedAt(now);
        return result;
    }

    private ScanOutcome fetchGooglePlayStatus(MonitorApp monitorApp, String scanMode)
    {
        if (SCAN_MODE_PLAYWRIGHT.equals(scanMode))
        {
            return fetchGooglePlayStatusByPlaywright(monitorApp);
        }
        return fetchGooglePlayStatusByHttp(monitorApp);
    }

    private ScanOutcome fetchGooglePlayStatusByHttp(MonitorApp monitorApp)
    {
        String region = resolveRegion(monitorApp.getRegion());
        String detailUrl = buildGooglePlayUrl(monitorApp.getBundleId(), region);
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(detailUrl))
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", DESKTOP_USER_AGENT)
                .header("Accept-Language", "en-US,en;q=0.9")
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400)
            {
                return ScanOutcome.offline("HTTP mode: Google Play returned status code " + response.statusCode());
            }
            return evaluateGooglePlayDocument(response.body(), monitorApp.getBundleId(), SCAN_MODE_HTTP);
        }
        catch (Exception e)
        {
            return ScanOutcome.unknown("HTTP mode request failed: " + e.getMessage());
        }
    }

    private ScanOutcome fetchGooglePlayStatusByPlaywright(MonitorApp monitorApp)
    {
        String region = resolveRegion(monitorApp.getRegion());
        String detailUrl = buildGooglePlayUrl(monitorApp.getBundleId(), region);
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true)))
        {
            Page page = browser.newPage(new Browser.NewPageOptions()
                .setUserAgent(DESKTOP_USER_AGENT)
                .setLocale("en-US"));
            Page.NavigateOptions options = new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(20000);
            page.navigate(detailUrl, options);
            page.waitForTimeout(1500);
            String html = page.content();
            return evaluateGooglePlayDocument(html, monitorApp.getBundleId(), SCAN_MODE_PLAYWRIGHT);
        }
        catch (Exception e)
        {
            return ScanOutcome.unknown("Playwright mode request failed: " + e.getMessage());
        }
    }

    private ScanOutcome evaluateGooglePlayDocument(String body, String bundleId, String scanMode)
    {
        String content = StringUtils.defaultString(body);
        String modePrefix = SCAN_MODE_PLAYWRIGHT.equals(scanMode) ? "Playwright mode" : "HTTP mode";
        if (containsOfflineMarker(content))
        {
            return ScanOutcome.offline(modePrefix + ": app not found or not visible in this region");
        }
        if (containsOnlineMarker(content, bundleId))
        {
            return ScanOutcome.online(modePrefix + ": app detail page is reachable");
        }
        return ScanOutcome.unknown(modePrefix + ": page loaded but status could not be determined");
    }

    private boolean notifyStatusChanged(MonitorApp monitorApp, Integer currentStatus, String detailMessage, Date alertTime)
    {
        String alertType = currentStatus != null && currentStatus == 1 ? ALERT_TYPE_ONLINE : ALERT_TYPE_OFFLINE;
        String statusText = currentStatus != null && currentStatus == 1 ? "ONLINE" : "OFFLINE";
        String detailUrl = buildGooglePlayUrl(monitorApp.getBundleId(), monitorApp.getRegion());
        String content = String.format("[Google Play App Status Changed]%nProduct: %s%nApp: %s%nBundle: %s%nRegion: %s%nStatus: %s%nURL: %s%nDetail: %s",
            monitorApp.getProductName() + " / " + monitorApp.getAppName(),
            monitorApp.getAppName(),
            monitorApp.getBundleId(),
            monitorApp.getRegion(),
            statusText,
            detailUrl,
            detailMessage);

        List<MonitorAlertChannel> channels = resolveAlertChannels(monitorApp);
        if (channels == null || channels.isEmpty())
        {
            insertAlertRecord(monitorApp, ALERT_CHANNEL_SYSTEM, alertType, content,
                buildExtJson(detailUrl, true, "No matched enabled Telegram channel configured"), alertTime);
            return false;
        }

        boolean anyNotified = false;
        for (MonitorAlertChannel channel : channels)
        {
            TelegramSendResult sendResult = sendTelegramMessage(channel, content);
            insertAlertRecord(monitorApp, ALERT_CHANNEL_TELEGRAM, alertType, content,
                buildExtJson(detailUrl, sendResult.isSuccess(), sendResult.getMessage() + ", channel: " + channel.getName()), alertTime);
            anyNotified = anyNotified || sendResult.isSuccess();
        }
        return anyNotified;
    }

    private List<MonitorAlertChannel> resolveAlertChannels(MonitorApp monitorApp)
    {
        List<MonitorAlertChannel> channels = monitorAlertChannelMapper.selectEnabledTelegramChannelsByCreateBy(monitorApp.getCreateBy());
        if (channels == null || channels.isEmpty())
        {
            return channels;
        }
        String ownerType = StringUtils.trim(monitorApp.getOwnerType());
        if (StringUtils.isBlank(ownerType) || StringUtils.equalsIgnoreCase(ownerType, SYSTEM_OPERATOR))
        {
            return channels;
        }
        List<MonitorAlertChannel> matched = new ArrayList<>();
        for (MonitorAlertChannel channel : channels)
        {
            if (StringUtils.equals(ownerType, String.valueOf(channel.getId()))
                || StringUtils.equals(ownerType, channel.getName()))
            {
                matched.add(channel);
            }
        }
        return matched;
    }

    private void insertAlertRecord(MonitorApp monitorApp, String channelType, String alertType, String message, String extJson, Date alertTime)
    {
        MonitorAlertRecord alertRecord = new MonitorAlertRecord();
        alertRecord.setAppId(monitorApp.getId());
        alertRecord.setChannelType(channelType);
        alertRecord.setAlertType(alertType);
        alertRecord.setAlertMessage(message);
        alertRecord.setAlertTime(alertTime);
        alertRecord.setExtJson(extJson);
        alertRecord.setCreateBy(monitorApp.getCreateBy());
        monitorAlertRecordMapper.insertMonitorAlertRecord(alertRecord);
    }

    private TelegramSendResult sendTelegramMessage(MonitorAlertChannel channel, String message)
    {
        try
        {
            String body = "chat_id=" + urlEncode(channel.getChatId())
                + "&text=" + urlEncode(message)
                + "&disable_web_page_preview=true";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + channel.getBotToken() + "/sendMessage"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300 && StringUtils.contains(response.body(), "\"ok\":true"))
            {
                return TelegramSendResult.success("Telegram send succeeded");
            }
            return TelegramSendResult.failure("Telegram send failed, HTTP " + response.statusCode());
        }
        catch (Exception e)
        {
            return TelegramSendResult.failure("Telegram send exception: " + e.getMessage());
        }
    }

    private String buildExtJson(String detailUrl, boolean success, String message)
    {
        Map<String, Object> ext = new LinkedHashMap<>();
        ext.put("detailUrl", detailUrl);
        ext.put("success", success);
        ext.put("message", message);
        return JSON.toJSONString(ext);
    }

    private boolean isGooglePlayApp(MonitorApp monitorApp)
    {
        return monitorApp != null && StringUtils.equalsAnyIgnoreCase(
            StringUtils.trim(monitorApp.getStorePlatform()),
            GOOGLE_PLAY_STORE, "google-play", "googleplay", "Google Play");
    }

    private String buildGooglePlayUrl(String bundleId, String region)
    {
        return "https://play.google.com/store/apps/details?id=" + urlEncode(bundleId)
            + "&hl=en_US&gl=" + urlEncode(resolveRegion(region));
    }

    private String resolveRegion(String region)
    {
        return StringUtils.isNotBlank(region) ? region.trim().toUpperCase() : DEFAULT_REGION;
    }

    private String urlEncode(String value)
    {
        return URLEncoder.encode(StringUtils.defaultString(value), StandardCharsets.UTF_8);
    }

    private boolean containsOfflineMarker(String body)
    {
        return StringUtils.containsAnyIgnoreCase(body,
            "item not found",
            "requested url was not found",
            "we're sorry, the requested url was not found",
            "not available for your device",
            "error while retrieving information from server",
            "url was not found on this server");
    }

    private boolean containsOnlineMarker(String body, String bundleId)
    {
        return StringUtils.containsAnyIgnoreCase(body,
            "\"appId\":\"" + bundleId + "\"",
            "\"androidAppPackageName\":\"" + bundleId + "\"",
            "play.google.com/store/apps/details?id=" + bundleId,
            "\"name\":\"Install\"",
            "\"name\":\"Update\"",
            "\"install\"",
            "\"download\"");
    }

    private String resolveOperator(String operator)
    {
        return StringUtils.isNotBlank(operator) ? operator : SYSTEM_OPERATOR;
    }

    private String resolveScanMode(String scanMode)
    {
        if (SCAN_MODE_PLAYWRIGHT.equalsIgnoreCase(StringUtils.trim(scanMode)))
        {
            return SCAN_MODE_PLAYWRIGHT;
        }
        return SCAN_MODE_HTTP;
    }

    private void validateImportRow(MonitorApp monitorApp)
    {
        if (StringUtils.isBlank(monitorApp.getProductName()))
        {
            throw new ServiceException("Product name cannot be empty");
        }
        if (StringUtils.isBlank(monitorApp.getAppName()))
        {
            throw new ServiceException("App name cannot be empty");
        }
        if (StringUtils.isBlank(monitorApp.getBundleId()))
        {
            throw new ServiceException("Bundle ID cannot be empty");
        }
        if (StringUtils.isBlank(monitorApp.getStorePlatform()))
        {
            throw new ServiceException("Store platform cannot be empty");
        }
        if (StringUtils.isBlank(monitorApp.getRegion()))
        {
            throw new ServiceException("Region cannot be empty");
        }
    }

    private void normalizeMonitorApp(MonitorApp monitorApp)
    {
        if (monitorApp == null)
        {
            return;
        }
        monitorApp.setProductName(StringUtils.trim(monitorApp.getProductName()));
        monitorApp.setAppName(StringUtils.trim(monitorApp.getAppName()));
        monitorApp.setBundleId(StringUtils.trim(monitorApp.getBundleId()));
        monitorApp.setStorePlatform(StringUtils.trim(monitorApp.getStorePlatform()));
        monitorApp.setRegion(StringUtils.trim(monitorApp.getRegion()));
        if (StringUtils.isNotBlank(monitorApp.getRegion()))
        {
            monitorApp.setRegion(monitorApp.getRegion().toUpperCase());
        }
        monitorApp.setOwnerType(StringUtils.trim(monitorApp.getOwnerType()));
    }

    private void validateOwnerType(MonitorApp monitorApp, String createBy)
    {
        if (monitorApp == null || StringUtils.isBlank(monitorApp.getOwnerType()))
        {
            throw new ServiceException("Owner type cannot be empty");
        }
        String ownerType = StringUtils.trim(monitorApp.getOwnerType());
        if (StringUtils.equalsIgnoreCase(ownerType, SYSTEM_OPERATOR))
        {
            return;
        }
        List<MonitorAlertChannel> channels = monitorAlertChannelMapper.selectEnabledTelegramChannelsByCreateBy(createBy);
        if (channels == null || channels.isEmpty())
        {
            throw new ServiceException("No enabled Telegram channel available for the current user");
        }
        for (MonitorAlertChannel channel : channels)
        {
            if (StringUtils.equals(ownerType, String.valueOf(channel.getId()))
                || StringUtils.equals(ownerType, channel.getName()))
            {
                monitorApp.setOwnerType(String.valueOf(channel.getId()));
                return;
            }
        }
        throw new ServiceException("Selected owner type is invalid or unavailable");
    }

    private List<Map<String, String>> buildStorePlatformOptions()
    {
        List<Map<String, String>> options = new ArrayList<>();
        SysDictData query = new SysDictData();
        query.setDictType("monitor_store_type");
        query.setStatus("0");
        List<SysDictData> dictDataList = sysDictDataService.selectDictDataList(query);
        if (dictDataList == null)
        {
            return options;
        }
        for (SysDictData dictData : dictDataList)
        {
            options.add(buildOption(dictData.getDictLabel(), dictData.getDictValue()));
        }
        return options;
    }

    private List<Map<String, String>> buildRegionOptions()
    {
        Set<String> regions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        regions.add("CN");
        regions.add("US");
        MonitorApp query = buildDataPermissionQuery();
        List<MonitorApp> apps = monitorAppMapper.selectMonitorAppList(query);
        if (apps != null)
        {
            for (MonitorApp app : apps)
            {
                if (StringUtils.isNotBlank(app.getRegion()))
                {
                    regions.add(app.getRegion().trim().toUpperCase());
                }
            }
        }
        List<Map<String, String>> options = new ArrayList<>();
        for (String region : regions)
        {
            options.add(buildOption(region, region));
        }
        return options;
    }

    private List<Map<String, String>> buildOwnerTypeOptions()
    {
        List<Map<String, String>> options = new ArrayList<>();
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username))
        {
            return options;
        }
        List<MonitorAlertChannel> channels = monitorAlertChannelMapper.selectEnabledTelegramChannelsByCreateBy(username);
        if (channels == null)
        {
            return options;
        }
        for (MonitorAlertChannel channel : channels)
        {
            options.add(buildOption(channel.getName(), String.valueOf(channel.getId())));
        }
        return options;
    }

    private Map<String, String> buildOption(String label, String value)
    {
        Map<String, String> option = new LinkedHashMap<>(2);
        option.put("label", label);
        option.put("value", value);
        return option;
    }

    private void checkMonitorAppUnique(MonitorApp monitorApp)
    {
        MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
        if (exists != null && !exists.getId().equals(monitorApp.getId()))
        {
            throw new ServiceException("App unique key conflict, please check whether bundleId + storePlatform + region is duplicated");
        }
    }

    private MonitorApp ensureMonitorAppExists(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("App ID cannot be null");
        }
        MonitorApp monitorApp = monitorAppMapper.selectMonitorAppById(id);
        if (monitorApp == null)
        {
            throw new ServiceException("App does not exist or has been deleted");
        }
        checkDataPermission(monitorApp.getCreateBy());
        return monitorApp;
    }

    private MonitorApp buildDataPermissionQuery()
    {
        MonitorApp monitorApp = new MonitorApp();
        applyDataPermission(monitorApp);
        return monitorApp;
    }

    private void applyDataPermission(MonitorApp monitorApp)
    {
        if (monitorApp == null || SecurityUtils.isAdmin())
        {
            return;
        }
        monitorApp.getParams().put("currentUsername", SecurityUtils.getUsername());
    }

    private void checkDataPermission(String createBy)
    {
        if (!SecurityUtils.isAdmin() && !StringUtils.equals(SecurityUtils.getUsername(), createBy))
        {
            throw new ServiceException("No permission to operate other users' data");
        }
    }

    private static final class ScanOutcome
    {
        private final boolean definitive;
        private final boolean online;
        private final String message;

        private ScanOutcome(boolean definitive, boolean online, String message)
        {
            this.definitive = definitive;
            this.online = online;
            this.message = message;
        }

        private static ScanOutcome online(String message)
        {
            return new ScanOutcome(true, true, message);
        }

        private static ScanOutcome offline(String message)
        {
            return new ScanOutcome(true, false, message);
        }

        private static ScanOutcome unknown(String message)
        {
            return new ScanOutcome(false, false, message);
        }

        private boolean isDefinitive()
        {
            return definitive;
        }

        private boolean isOnline()
        {
            return online;
        }

        private String getMessage()
        {
            return message;
        }
    }

    private static final class TelegramSendResult
    {
        private final boolean success;
        private final String message;

        private TelegramSendResult(boolean success, String message)
        {
            this.success = success;
            this.message = message;
        }

        private static TelegramSendResult success(String message)
        {
            return new TelegramSendResult(true, message);
        }

        private static TelegramSendResult failure(String message)
        {
            return new TelegramSendResult(false, message);
        }

        private boolean isSuccess()
        {
            return success;
        }

        private String getMessage()
        {
            return message;
        }
    }
}
