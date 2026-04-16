package com.ruoyi.system.service;

import com.monitor.system.domain.MonitorApp;
import com.monitor.system.domain.dto.MonitorAppStatusDto;
import com.monitor.system.domain.vo.MonitorAppOverviewVo;

import java.util.List;

/**
 * 搴旂敤鐩戞帶鏈嶅姟灞? * 
 */
public interface IMonitorAppService
{
    /**
     * 鏌ヨ搴旂敤鐩戞帶姒傝
     * 
     * @return 姒傝鏁版嵁
     */
    public MonitorAppOverviewVo selectMonitorAppOverview();

    /**
     * 鏌ヨ搴旂敤鍒楄〃
     * 
     * @param monitorApp 鏌ヨ鏉′欢
     * @return 搴旂敤鍒楄〃
     */
    public List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    /**
     * 鏂板搴旂敤
     * 
     * @param monitorApp 搴旂敤淇℃伅
     * @return 缁撴灉
     */
    public int insertMonitorApp(MonitorApp monitorApp);

    /**
     * 淇敼搴旂敤
     * 
     * @param monitorApp 搴旂敤淇℃伅
     * @return 缁撴灉
     */
    public int updateMonitorApp(MonitorApp monitorApp);

    /**
     * 鍒犻櫎搴旂敤
     * 
     * @param id 搴旂敤ID
     * @return 缁撴灉
     */
    public int deleteMonitorAppById(Long id);

    /**
     * 淇敼搴旂敤鐘舵€?     * 
     * @param statusDto 鐘舵€佷俊鎭?     * @param updateBy 鏇存柊浜?     * @return 缁撴灉
     */
    public int updateMonitorAppStatus(MonitorAppStatusDto statusDto, String updateBy);

    /**
     * 鎵归噺瀵煎叆搴旂敤
     * 
     * @param appList 搴旂敤鍒楄〃
     * @param updateSupport 鏄惁鏇存柊宸插瓨鍦ㄦ暟鎹?     * @param operName 鎿嶄綔浜?     * @return 瀵煎叆缁撴灉
     */
    public String importMonitorApp(List<MonitorApp> appList, boolean updateSupport, String operName);
}
