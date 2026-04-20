package com.ruoyi.system.service.impl;

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
import com.ruoyi.system.domain.MonitorAppAlertChannel;
import com.ruoyi.system.domain.dto.MonitorAppStatusDto;
import com.ruoyi.system.domain.vo.MonitorAppImportResultVo;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;
import com.ruoyi.system.domain.vo.MonitorAppScanResultVo;
import com.ruoyi.system.mapper.MonitorAlertChannelMapper;
import com.ruoyi.system.mapper.MonitorAlertRecordMapper;
import com.ruoyi.system.mapper.MonitorAppAlertChannelMapper;
import com.ruoyi.system.mapper.MonitorAppMapper;
import com.ruoyi.system.service.IMonitorAppService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDictDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

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
    private static final String TELEGRAM_BOT_TOKEN_CONFIG_KEY = "monitor.telegram.botToken";
    private static final String TELEGRAM_ALERT_TEMPLATE_CONFIG_KEY = "monitor.telegram.alertTemplate";
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36";
    private static final Logger log = LoggerFactory.getLogger(MonitorAppServiceImpl.class);

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
    private MonitorAppAlertChannelMapper monitorAppAlertChannelMapper;

    @Autowired
    private ISysDictDataService sysDictDataService;

    @Autowired
    private ISysConfigService sysConfigService;

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
        Map<String, Object> result = new LinkedHashMap<>(2);
        result.put("storePlatforms", buildStorePlatformOptions());
        result.put("alertChannels", buildAlertChannelOptions());
        return result;
    }

    @Override
    public List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp)
    {
        applyDataPermission(monitorApp);
        List<MonitorApp> list = monitorAppMapper.selectMonitorAppList(monitorApp);
        fillAlertChannelData(list);
        return list;
    }

    @Override
    public int insertMonitorApp(MonitorApp monitorApp)
    {
        normalizeMonitorApp(monitorApp);
        checkMonitorAppUnique(monitorApp);
        int rows = monitorAppMapper.insertMonitorApp(monitorApp);
        saveAlertChannels(monitorApp.getId(), monitorApp.getAlertChannelIds(), monitorApp.getCreateBy());
        return rows;
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
        checkMonitorAppUnique(monitorApp);
        int rows = monitorAppMapper.updateMonitorApp(monitorApp);
        saveAlertChannels(monitorApp.getId(), monitorApp.getAlertChannelIds(), exists.getCreateBy());
        return rows;
    }

    @Override
    public int deleteMonitorAppById(Long id)
    {
        ensureMonitorAppExists(id);
        monitorAppAlertChannelMapper.deleteByAppId(id);
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
    public MonitorAppImportResultVo importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(appList) || appList.isEmpty())
        {
            throw new ServiceException("Import data cannot be empty");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        MonitorAppImportResultVo result = new MonitorAppImportResultVo();
        for (MonitorApp monitorApp : appList)
        {
            try
            {
                normalizeMonitorApp(monitorApp);
                applyImportDefaults(monitorApp);
                validateImportRow(monitorApp);
                monitorApp.setCreateBy(operName);
                monitorApp.setUpdateBy(operName);
                MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
                if (StringUtils.isNull(exists))
                {
                    monitorAppMapper.insertMonitorApp(monitorApp);
                    result.getImportedIds().add(monitorApp.getId());
                    result.getHandledIds().add(monitorApp.getId());
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append(". App ")
                        .append(monitorApp.getProductName()).append(" imported successfully");
                }
                else if (updateSupport)
                {
                    checkDataPermission(exists.getCreateBy());
                    monitorApp.setId(exists.getId());
                    monitorApp.setStatus(exists.getStatus());
                    monitorAppMapper.updateMonitorApp(monitorApp);
                    result.getUpdatedIds().add(exists.getId());
                    result.getHandledIds().add(exists.getId());
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append(". App ")
                        .append(monitorApp.getProductName()).append(" updated successfully");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append(". App ")
                        .append(monitorApp.getProductName())
                        .append(" already exists, unique key appLink + storePlatform conflicts");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String productName = StringUtils.isNotBlank(monitorApp.getProductName()) ? monitorApp.getProductName() : "Unnamed product";
                failureMsg.append("<br/>").append(failureNum).append(". App ")
                    .append(productName).append(" import failed: ").append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "Import failed, " + failureNum + " rows contain errors:");
            throw new ServiceException(failureMsg.toString());
        }
        result.setMessage("Import succeeded, total " + successNum + " rows:" + successMsg);
        return result;
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
    public int assignAlertChannels(List<Long> appIds, List<Long> channelIds, String operator)
    {
        if (appIds == null || appIds.isEmpty())
        {
            throw new ServiceException("Please select at least one product");
        }
        List<Long> distinctAppIds = appIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> distinctChannelIds = channelIds == null
            ? new ArrayList<>()
            : channelIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        validateAssignableChannels(distinctChannelIds);
        for (Long appId : distinctAppIds)
        {
            ensureMonitorAppExists(appId);
        }
        monitorAppAlertChannelMapper.deleteByAppIds(distinctAppIds);
        if (!distinctChannelIds.isEmpty())
        {
            List<MonitorAppAlertChannel> relations = new ArrayList<>();
            for (Long appId : distinctAppIds)
            {
                for (Long channelId : distinctChannelIds)
                {
                    MonitorAppAlertChannel relation = new MonitorAppAlertChannel();
                    relation.setAppId(appId);
                    relation.setChannelId(channelId);
                    relation.setCreateBy(operator);
                    relations.add(relation);
                }
            }
            monitorAppAlertChannelMapper.batchInsert(relations);
        }
        return distinctAppIds.size();
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
        result.setProductName(monitorApp.getProductName());
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
        String detailUrl = normalizeGooglePlayDetailLink(monitorApp.getAppLink());
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
                return ScanOutcome.offline("HTTP模式：Google Play返回" + response.statusCode());
            }
            return evaluateGooglePlayDocument(response.body(), detailUrl, SCAN_MODE_HTTP);
        }
        catch (Exception e)
        {
            return ScanOutcome.unknown("HTTP模式：请求失败 - " + e.getMessage());
        }
    }

    private ScanOutcome fetchGooglePlayStatusByPlaywright(MonitorApp monitorApp)
    {
        String detailUrl = normalizeGooglePlayDetailLink(monitorApp.getAppLink());
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
            return evaluateGooglePlayDocument(html, detailUrl, SCAN_MODE_PLAYWRIGHT);
        }
        catch (Exception e)
        {
            return ScanOutcome.unknown("Playwright模式：请求失败 - " + e.getMessage());
        }
    }

    private ScanOutcome evaluateGooglePlayDocument(String body, String detailUrl, String scanMode)
    {
        String content = StringUtils.defaultString(body);
        String modePrefix = SCAN_MODE_PLAYWRIGHT.equals(scanMode) ? "Playwright模式" : "HTTP模式";
        if (containsOfflineMarker(content))
        {
            return ScanOutcome.offline(modePrefix + "：Google Play页面不存在或当前区域不可用");
        }
        if (containsOnlineMarker(content, detailUrl))
        {
            return ScanOutcome.online(modePrefix + "：Google Play页面可访问");
        }
        return ScanOutcome.unknown(modePrefix + "：页面已加载但无法确定状态");
    }

    private boolean notifyStatusChanged(MonitorApp monitorApp, Integer currentStatus, String detailMessage, Date alertTime)
    {
        String alertType = currentStatus != null && currentStatus == 1 ? ALERT_TYPE_ONLINE : ALERT_TYPE_OFFLINE;
        String statusText = currentStatus != null && currentStatus == 1 ? "ONLINE" : "OFFLINE";
        String detailUrl = monitorApp.getAppLink();
        String alertMessage = detailMessage;
        String logMessage = String.format("[MonitorAlert] product=%s, appId=%s, alertType=%s, status=%s, url=%s, detail=%s",
            monitorApp.getProductName(),
            monitorApp.getId(),
            alertType,
            statusText,
            detailUrl,
            detailMessage);
        String telegramContent = buildTelegramAlertMessage(monitorApp, statusText, detailUrl, detailMessage);

        List<MonitorAlertChannel> channels = resolveAlertChannels(monitorApp);
        if (channels == null || channels.isEmpty())
        {
            log.info("{}, telegram=no matched enabled channel", logMessage);
            insertAlertRecord(monitorApp, ALERT_CHANNEL_SYSTEM, alertType, alertMessage, alertTime);
            return false;
        }

        boolean anyNotified = false;
        for (MonitorAlertChannel channel : channels)
        {
            TelegramSendResult sendResult = sendTelegramMessage(channel, telegramContent);
            log.info("{}, telegramChannel={}, telegramResult={}, telegramSuccess={}",
                logMessage, channel.getName(), sendResult.getMessage(), sendResult.isSuccess());
            insertAlertRecord(monitorApp, ALERT_CHANNEL_TELEGRAM, alertType, alertMessage, alertTime);
            anyNotified = anyNotified || sendResult.isSuccess();
        }
        return anyNotified;
    }

    private List<MonitorAlertChannel> resolveAlertChannels(MonitorApp monitorApp)
    {
        return monitorAlertChannelMapper.selectEnabledChannelsByAppId(monitorApp.getId());
    }

    private void insertAlertRecord(MonitorApp monitorApp, String channelType, String alertType, String message, Date alertTime)
    {
        MonitorAlertRecord alertRecord = new MonitorAlertRecord();
        alertRecord.setAppId(monitorApp.getId());
        alertRecord.setChannelType(channelType);
        alertRecord.setAlertType(alertType);
        alertRecord.setAlertMessage(message);
        alertRecord.setAlertTime(alertTime);
        alertRecord.setCreateBy(monitorApp.getCreateBy());
        monitorAlertRecordMapper.insertMonitorAlertRecord(alertRecord);
    }

    private TelegramSendResult sendTelegramMessage(MonitorAlertChannel channel, String message)
    {
        try
        {
            String botToken = resolveTelegramBotToken(channel);
            if (StringUtils.isBlank(botToken))
            {
                return TelegramSendResult.failure("Telegram bot token is not configured");
            }
            String body = "chat_id=" + urlEncode(channel.getChatId())
                + "&text=" + urlEncode(message)
                + "&parse_mode=HTML"
                + "&disable_web_page_preview=true";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/sendMessage"))
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

    private String resolveTelegramBotToken(MonitorAlertChannel channel)
    {
        String globalToken = StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_BOT_TOKEN_CONFIG_KEY));
        if (StringUtils.isNotBlank(globalToken))
        {
            return globalToken;
        }
        return channel == null ? StringUtils.EMPTY : StringUtils.trim(channel.getBotToken());
    }

    private String buildTelegramAlertMessage(MonitorApp monitorApp, String statusText, String detailUrl, String detailMessage)
    {
        String alertTitle = "OFFLINE".equals(statusText) ? "Google Play应用状态变更" : "Google Play应用状态恢复";
        String statusLabel = "OFFLINE".equals(statusText) ? "离线 / 应用下架" : "在线 / 应用恢复";
        String productName = monitorApp == null ? StringUtils.EMPTY : StringUtils.defaultString(monitorApp.getProductName());
        String template = StringUtils.trim(sysConfigService.selectConfigByKey(TELEGRAM_ALERT_TEMPLATE_CONFIG_KEY));
        if (StringUtils.isBlank(template))
        {
            template = "<b>[${alertTitle}]</b>\n"
                + "<b>状态</b><code>${statusLabel}</code>\n"
                + "<b>产品</b>${productName}\n"
                + "<b>链接</b><a href=\"${detailUrl}\">查看详情页</a>\n"
                + "<b>结果</b>${detailMessage}";
        }
        return template
            .replace("${alertTitle}", escapeHtml(alertTitle))
            .replace("${status}", escapeHtml(statusText))
            .replace("${statusLabel}", escapeHtml(statusLabel))
            .replace("${productName}", escapeHtml(productName))
            .replace("${detailUrl}", escapeHtmlAttribute(detailUrl))
            .replace("${detailMessage}", escapeHtml(detailMessage));
    }

    private String escapeHtml(String text)
    {
        if (text == null)
        {
            return StringUtils.EMPTY;
        }
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private String escapeHtmlAttribute(String text)
    {
        return escapeHtml(text).replace("\"", "&quot;");
    }

    private boolean isGooglePlayApp(MonitorApp monitorApp)
    {
        return monitorApp != null && StringUtils.equalsAnyIgnoreCase(
            StringUtils.trim(monitorApp.getStorePlatform()),
            GOOGLE_PLAY_STORE, "google-play", "googleplay", "Google Play");
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

    private boolean containsOnlineMarker(String body, String detailUrl)
    {
        String normalizedUrl = normalizeGooglePlayDetailLink(detailUrl);
        String plainUrl = normalizeGooglePlayDetailLinkWithoutLocale(normalizedUrl);
        String escapedUrl = escapeGooglePlayUrl(normalizedUrl);
        String escapedPlainUrl = escapeGooglePlayUrl(plainUrl);
        return StringUtils.containsAnyIgnoreCase(body,
            normalizedUrl,
            plainUrl,
            escapedUrl,
            escapedPlainUrl,
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
        if (StringUtils.isBlank(monitorApp.getAppLink()))
        {
            throw new ServiceException("App link cannot be empty");
        }
    }

    private void normalizeMonitorApp(MonitorApp monitorApp)
    {
        if (monitorApp == null)
        {
            return;
        }
        monitorApp.setProductName(StringUtils.trim(monitorApp.getProductName()));
        monitorApp.setAppLink(StringUtils.trim(monitorApp.getAppLink()));
        monitorApp.setStorePlatform(StringUtils.trim(monitorApp.getStorePlatform()));
        if (StringUtils.isNotBlank(monitorApp.getAppLink()) && isGooglePlayApp(monitorApp))
        {
            monitorApp.setAppLink(normalizeGooglePlayDetailLink(monitorApp.getAppLink()));
        }
        monitorApp.setOwnerType(StringUtils.trim(monitorApp.getOwnerType()));
    }

    private void applyImportDefaults(MonitorApp monitorApp)
    {
        if (monitorApp == null)
        {
            return;
        }
        if (StringUtils.isBlank(monitorApp.getStorePlatform()))
        {
            monitorApp.setStorePlatform(GOOGLE_PLAY_STORE);
        }
        if (monitorApp.getStatus() == null)
        {
            monitorApp.setStatus(1);
        }
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

    private List<Map<String, String>> buildAlertChannelOptions()
    {
        List<Map<String, String>> options = new ArrayList<>();
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username))
        {
            return options;
        }
        List<MonitorAlertChannel> channels = monitorAlertChannelMapper.selectAlertChannelsByCreateBy(username);
        if (channels == null)
        {
            return options;
        }
        for (MonitorAlertChannel channel : channels)
        {
            String label = channel.getName();
            if (channel.getEnabled() != null && channel.getEnabled() == 0)
            {
                label = label + " (disabled)";
            }
            options.add(buildOption(label, String.valueOf(channel.getId())));
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

    private void fillAlertChannelData(List<MonitorApp> list)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }
        for (MonitorApp app : list)
        {
            List<MonitorAlertChannel> channels = monitorAlertChannelMapper.selectAssignedChannelsByAppId(app.getId());
            if (channels == null || channels.isEmpty())
            {
                app.setAlertChannelIds(new ArrayList<>());
                app.setAlertChannelNames(StringUtils.EMPTY);
                continue;
            }
            app.setAlertChannelIds(channels.stream().map(MonitorAlertChannel::getId).collect(Collectors.toList()));
            app.setAlertChannelNames(channels.stream().map(MonitorAlertChannel::getName).collect(Collectors.joining(", ")));
        }
    }

    private void saveAlertChannels(Long appId, List<Long> channelIds, String createBy)
    {
        if (appId == null)
        {
            return;
        }
        monitorAppAlertChannelMapper.deleteByAppId(appId);
        List<Long> distinctChannelIds = channelIds == null
            ? new ArrayList<>()
            : channelIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        validateAssignableChannels(distinctChannelIds);
        if (distinctChannelIds.isEmpty())
        {
            return;
        }
        List<MonitorAppAlertChannel> relations = new ArrayList<>();
        for (Long channelId : distinctChannelIds)
        {
            MonitorAppAlertChannel relation = new MonitorAppAlertChannel();
            relation.setAppId(appId);
            relation.setChannelId(channelId);
            relation.setCreateBy(createBy);
            relations.add(relation);
        }
        monitorAppAlertChannelMapper.batchInsert(relations);
    }

    private void validateAssignableChannels(List<Long> channelIds)
    {
        if (channelIds == null || channelIds.isEmpty())
        {
            return;
        }
        List<Long> availableIds;
        if (SecurityUtils.isAdmin())
        {
            availableIds = channelIds.stream()
                .map(monitorAlertChannelMapper::selectMonitorAlertChannelById)
                .filter(Objects::nonNull)
                .map(MonitorAlertChannel::getId)
                .collect(Collectors.toList());
        }
        else
        {
            availableIds = monitorAlertChannelMapper.selectAlertChannelsByCreateBy(SecurityUtils.getUsername()).stream()
                .map(MonitorAlertChannel::getId)
                .collect(Collectors.toList());
        }
        for (Long channelId : channelIds)
        {
            if (!availableIds.contains(channelId))
            {
                throw new ServiceException("Selected alert group is invalid or unavailable");
            }
        }
    }

    private void checkMonitorAppUnique(MonitorApp monitorApp)
    {
        MonitorApp exists = monitorAppMapper.selectMonitorAppByUniqueKey(monitorApp);
        if (exists != null && !exists.getId().equals(monitorApp.getId()))
        {
            throw new ServiceException("App unique key conflict, please check whether appLink + storePlatform is duplicated");
        }
    }

    private String normalizeGooglePlayDetailLink(String appLink)
    {
        try
        {
            URI uri = new URI(StringUtils.trim(appLink));
            String host = StringUtils.defaultString(uri.getHost()).toLowerCase();
            String path = StringUtils.defaultString(uri.getPath());
            if (!host.contains("play.google.com") || !"/store/apps/details".equals(path))
            {
                throw new ServiceException("Google Play应用链接格式不正确，请使用应用详情页链接");
            }

            String query = uri.getRawQuery();
            String appId = null;
            String language = null;
            String country = null;
            if (StringUtils.isNotBlank(query))
            {
                for (String pair : query.split("&"))
                {
                    String[] parts = pair.split("=", 2);
                    if (parts.length != 2 || StringUtils.isBlank(parts[1]))
                    {
                        continue;
                    }
                    String key = parts[0];
                    String value = java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                    if ("id".equals(key) && StringUtils.isNotBlank(value))
                    {
                        appId = value;
                    }
                    else if ("hl".equals(key) && StringUtils.isNotBlank(value))
                    {
                        language = value;
                    }
                    else if ("gl".equals(key) && StringUtils.isNotBlank(value))
                    {
                        country = value;
                    }
                }
            }

            if (StringUtils.isBlank(appId))
            {
                throw new ServiceException("Google Play应用链接缺少应用ID，请使用完整的详情页链接");
            }

            StringBuilder normalized = new StringBuilder("https://play.google.com/store/apps/details?id=")
                .append(urlEncode(appId));
            if (StringUtils.isNotBlank(language))
            {
                normalized.append("&hl=").append(urlEncode(language));
            }
            if (StringUtils.isNotBlank(country))
            {
                normalized.append("&gl=").append(urlEncode(country));
            }
            return normalized.toString();
        }
        catch (Exception e)
        {
            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }
            throw new ServiceException("Google Play应用链接格式不正确，请使用应用详情页链接");
        }
    }

    private String normalizeGooglePlayDetailLinkWithoutLocale(String detailUrl)
    {
        String normalized = normalizeGooglePlayDetailLink(detailUrl);
        int localeIndex = normalized.indexOf("&hl=");
        if (localeIndex >= 0)
        {
            normalized = normalized.substring(0, localeIndex);
        }
        int countryIndex = normalized.indexOf("&gl=");
        if (countryIndex >= 0)
        {
            normalized = normalized.substring(0, countryIndex);
        }
        return normalized;
    }

    private String escapeGooglePlayUrl(String detailUrl)
    {
        return normalizeGooglePlayDetailLink(detailUrl).replace("&", "&amp;");
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
