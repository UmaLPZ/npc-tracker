package com.example.npcgoaltracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("goaltracker")
public interface GoalTrackerConfig extends Config
{
    @ConfigItem(keyName = "goalTrackerData", name = "", description = "", hidden = true)
    default String goalTrackerData()
    {
        return "";
    }

    @ConfigItem(keyName = "goalTrackerData", name = "", description = "", hidden = true)
    void goalTrackerData(String str);

    @ConfigItem(keyName = "goalTrackerItemCache", name = "", description = "", hidden = true)
    default String goalTrackerItemCache()
    {
        return "";
    }

    @ConfigItem(keyName = "goalTrackerItemCache", name = "", description = "", hidden = true)
    void goalTrackerItemCache(String str);

    @ConfigItem(keyName = "goalTrackerItemNoteMapCache", name = "", description = "", hidden = true)
    default String goalTrackerItemNoteMapCache()
    {
        return "";
    }

    @ConfigItem(keyName = "goalTrackerItemNoteMapCache", name = "", description = "", hidden = true)
    void goalTrackerItemNoteMapCache(String str);
}
