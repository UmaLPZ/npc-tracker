package com.example.npcgoaltracker.goal;

import com.example.npcgoaltracker.goal.factory.AllTaskFactory;
import com.example.npcgoaltracker.goal.factory.TaskFactory;
import com.example.npcgoaltracker.goal.factory.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskType
{
    MANUAL("manual", AllTaskFactory.ManualTaskFactory.class),
    SKILL_LEVEL("skill_level", AllTaskFactory.SkillLevelTaskFactory.class),
    SKILL_XP("skill_xp", AllTaskFactory.SkillXpTaskFactory.class),
    QUEST("quest", AllTaskFactory.QuestTaskFactory.class),
    ITEM("item", AllTaskFactory.ItemTaskFactory.class);

    @Getter
    private final String name;

    @Getter
    private final Class<? extends TaskFactory<? extends Task>> factory;

    public static TaskType fromString(String name)
    {
        for (TaskType type : TaskType.values()) {
            if (type.toString().equals(name)) {
                return type;
            }
        }
        throw new IllegalStateException("Invalid task type " + name);
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
