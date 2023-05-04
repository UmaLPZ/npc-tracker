package com.example.npcgoaltracker.services;

import com.example.npcgoaltracker.ItemCache;
import com.example.npcgoaltracker.goal.AllTask;
import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.goal.TaskStatus;
import com.example.npcgoaltracker.goal.*;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import org.apache.commons.lang3.NotImplementedException;

public class TaskCheckerService
{
    @Inject
    private Client client;

    @Inject
    private ItemCache itemCache;

    public TaskStatus check(Task task)
    {
        if (task instanceof AllTask.ManualTask) {
            return check((AllTask.ManualTask) task);
        }

        if (task instanceof AllTask.SkillLevelTask) {
            return check((AllTask.SkillLevelTask) task);
        }

        if (task instanceof AllTask.SkillXpTask) {
            return check((AllTask.SkillXpTask) task);
        }

        if (task instanceof AllTask.QuestTask) {
            return check((AllTask.QuestTask) task);
        }

        if (task instanceof AllTask.ItemTask) {
            return check((AllTask.ItemTask) task);
        }

        throw new NotImplementedException("Missing task check implementation");
    }

    public TaskStatus check(AllTask.ManualTask task)
    {
        return task.setResult(task.isDone()
            ? TaskStatus.COMPLETED
            : TaskStatus.NOT_STARTED);
    }

    public TaskStatus check(AllTask.SkillLevelTask task)
    {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return task.getResult();
        }

        return task.setResult(
            client.getRealSkillLevel(task.getSkill()) >= task.getLevel()
                ? TaskStatus.COMPLETED
                : TaskStatus.NOT_STARTED);
    }

    public TaskStatus check(AllTask.SkillXpTask task)
    {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return task.getResult();
        }

        return task.setResult(
            client.getSkillExperience(task.getSkill()) >= task.getXp()
                ? TaskStatus.COMPLETED
                : TaskStatus.NOT_STARTED);
    }

    public TaskStatus check(AllTask.QuestTask task)
    {
        if (client.getGameState() != GameState.LOGGED_IN ||
            !client.isClientThread()) {
            return task.getResult();
        }

        return task.setResult(TaskStatus.fromQuestState(task.getQuest().getState(client)));
    }

    public TaskStatus check(AllTask.ItemTask task)
    {
        task.setAcquired(Math.min(
            itemCache.getTotalQuantity(task.getItemId()),
            task.getQuantity()));

        return task.setResult(
            task.getAcquired() >= task.getQuantity()
                ? TaskStatus.COMPLETED
                : (task.getAcquired() > 0
                    ? TaskStatus.IN_PROGRESS
                    : TaskStatus.NOT_STARTED));
    }
}
