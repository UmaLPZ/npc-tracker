package com.example.npcgoaltracker.services;

import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.goal.factory.AllTaskFactory;
import com.example.npcgoaltracker.goal.factory.TaskFactory;
import com.example.npcgoaltracker.goal.factory.*;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class TaskFactoryService
{
    private final Map<Class<?>, TaskFactory<? extends Task>> services = new HashMap<>();

    @Inject
    TaskFactoryService(
        AllTaskFactory.ManualTaskFactory manualTaskFactory,
        AllTaskFactory.SkillLevelTaskFactory skillLevelTaskFactory,
        AllTaskFactory.SkillXpTaskFactory skillXpTaskFactory,
        AllTaskFactory.QuestTaskFactory questTaskFactory,
        AllTaskFactory.ItemTaskFactory itemTaskService
    )
    {
        services.put(AllTaskFactory.ManualTaskFactory.class, manualTaskFactory);
        services.put(AllTaskFactory.SkillLevelTaskFactory.class, skillLevelTaskFactory);
        services.put(AllTaskFactory.SkillXpTaskFactory.class, skillXpTaskFactory);
        services.put(AllTaskFactory.QuestTaskFactory.class, questTaskFactory);
        services.put(AllTaskFactory.ItemTaskFactory.class, itemTaskService);
    }

    public TaskFactory<? extends Task> get(Task task)
    {
        return get(task.getFactoryClass());
    }

    @SuppressWarnings("unchecked")
    public <T extends TaskFactory<? extends Task>> T get(Class<T> factoryClass)
    {
        return (T) services.get(factoryClass);
    }
}
