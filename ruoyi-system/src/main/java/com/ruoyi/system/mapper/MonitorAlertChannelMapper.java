package com.ruoyi.system.mapper;

import com.monitor.system.domain.MonitorAlertChannel;

import java.util.List;

/**
 * 鍛婅娓犻亾鏁版嵁灞? * 
 */
public interface MonitorAlertChannelMapper
{
    /**
     * 鏍规嵁ID鏌ヨ娓犻亾
     * 
     * @param id 涓婚敭
     * @return 娓犻亾淇℃伅
     */
    public MonitorAlertChannel selectMonitorAlertChannelById(Long id);

    /**
     * 鏌ヨ Telegram 娓犻亾鍒楄〃
     * 
     * @param channel 鏌ヨ鏉′欢
     * @return 娓犻亾鍒楄〃
     */
    public List<MonitorAlertChannel> selectTelegramChannelList(MonitorAlertChannel channel);

    /**
     * 鏂板娓犻亾
     * 
     * @param channel 娓犻亾淇℃伅
     * @return 缁撴灉
     */
    public int insertMonitorAlertChannel(MonitorAlertChannel channel);

    /**
     * 淇敼娓犻亾
     * 
     * @param channel 娓犻亾淇℃伅
     * @return 缁撴灉
     */
    public int updateMonitorAlertChannel(MonitorAlertChannel channel);

    /**
     * 閫昏緫鍒犻櫎娓犻亾
     * 
     * @param id 涓婚敭
     * @return 缁撴灉
     */
    public int deleteMonitorAlertChannelById(Long id);
}
