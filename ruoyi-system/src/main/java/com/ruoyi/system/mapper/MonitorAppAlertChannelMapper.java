package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MonitorAppAlertChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * App and alert-channel relation mapper.
 */
public interface MonitorAppAlertChannelMapper
{
    int deleteByAppId(Long appId);

    int deleteByAppIds(@Param("appIds") List<Long> appIds);

    int batchInsert(@Param("list") List<MonitorAppAlertChannel> list);
}
