package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.MonitorApp;
import com.ruoyi.system.domain.vo.MonitorAppOverviewVo;

import java.util.List;

/**
 * 搴旂敤鐩戞帶鏁版嵁灞? * 
 */
public interface MonitorAppMapper
{
    /**
     * 鏌ヨ搴旂敤鐩戞帶姒傝
     * 
     * @return 姒傝鏁版嵁
     */
    public MonitorAppOverviewVo selectMonitorAppOverview();

    /**
     * 鏍规嵁ID鏌ヨ搴旂敤
     * 
     * @param id 搴旂敤ID
     * @return 搴旂敤淇℃伅
     */
    public MonitorApp selectMonitorAppById(Long id);

    /**
     * 鏌ヨ搴旂敤鍒楄〃
     * 
     * @param monitorApp 鏌ヨ鏉′欢
     * @return 搴旂敤鍒楄〃
     */
    public List<MonitorApp> selectMonitorAppList(MonitorApp monitorApp);

    /**
     * 鏌ヨ鍞竴閿搴斿簲鐢?     * 
     * @param monitorApp 搴旂敤淇℃伅
     * @return 搴旂敤淇℃伅
     */
    public MonitorApp selectMonitorAppByUniqueKey(MonitorApp monitorApp);

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
     * 淇敼搴旂敤鐘舵€?     * 
     * @param monitorApp 搴旂敤淇℃伅
     * @return 缁撴灉
     */
    public int updateMonitorAppStatus(MonitorApp monitorApp);

    /**
     * 閫昏緫鍒犻櫎搴旂敤
     * 
     * @param id 搴旂敤ID
     * @return 缁撴灉
     */
    public int deleteMonitorAppById(Long id);
}
