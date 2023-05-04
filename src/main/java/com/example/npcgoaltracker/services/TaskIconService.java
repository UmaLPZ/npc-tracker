package com.example.npcgoaltracker.services;

import com.example.npcgoaltracker.goal.AllTask;
import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.*;

import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.NotImplementedException;

public class TaskIconService
{
    private static final BufferedImage CROSS_MARK_ICON;
    private static final BufferedImage QUEST_ICON;

    static {
        CROSS_MARK_ICON = ImageUtil.loadImageResource(
            GoalTrackerPlugin.class, "/cross_mark.png");
        QUEST_ICON = ImageUtil.loadImageResource(
            GoalTrackerPlugin.class, "/quest_icon.png");
    }

    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SkillIconManager skillIconManager;

    public BufferedImage get(Task task)
    {
        if (task instanceof AllTask.ManualTask) {
            return get((AllTask.ManualTask) task);
        }

        if (task instanceof AllTask.SkillLevelTask) {
            return get((AllTask.SkillLevelTask) task);
        }

        if (task instanceof AllTask.SkillXpTask) {
            return get((AllTask.SkillXpTask) task);
        }

        if (task instanceof AllTask.QuestTask) {
            return get((AllTask.QuestTask) task);
        }

        if (task instanceof AllTask.ItemTask) {
            return get((AllTask.ItemTask) task);
        }

        throw new NotImplementedException("Missing task icon implementation");
    }

    public BufferedImage get(AllTask.ManualTask task)
    {
        return CROSS_MARK_ICON;
    }

    public BufferedImage get(AllTask.SkillLevelTask task)
    {
        return skillIconManager.getSkillImage(task.getSkill());
    }

    public BufferedImage get(AllTask.SkillXpTask task)
    {
        return skillIconManager.getSkillImage(task.getSkill());
    }

    public BufferedImage get(AllTask.QuestTask task)
    {
        return QUEST_ICON;
    }

    public BufferedImage get(AllTask.ItemTask task)
    {
        if (task.getCachedIcon() != null) {
            return task.getCachedIcon();
        }

        if (!client.isClientThread()) {
            return null;
        }

        return task.setCachedIcon(itemManager.getImage(task.getItemId()));
    }
}
