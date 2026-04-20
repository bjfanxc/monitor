package com.ruoyi.system.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * App import result payload for UI follow-up assignment.
 */
public class MonitorAppImportResultVo
{
    private String message;

    private List<Long> handledIds = new ArrayList<>();

    private List<Long> importedIds = new ArrayList<>();

    private List<Long> updatedIds = new ArrayList<>();

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<Long> getHandledIds()
    {
        return handledIds;
    }

    public void setHandledIds(List<Long> handledIds)
    {
        this.handledIds = handledIds;
    }

    public List<Long> getImportedIds()
    {
        return importedIds;
    }

    public void setImportedIds(List<Long> importedIds)
    {
        this.importedIds = importedIds;
    }

    public List<Long> getUpdatedIds()
    {
        return updatedIds;
    }

    public void setUpdatedIds(List<Long> updatedIds)
    {
        this.updatedIds = updatedIds;
    }
}
