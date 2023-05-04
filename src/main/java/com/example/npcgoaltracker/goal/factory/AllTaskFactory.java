package com.example.npcgoaltracker.goal.factory;

import com.example.npcgoaltracker.goal.AllTask;
import com.google.gson.JsonObject;
import com.example.npcgoaltracker.goal.*;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

public class AllTaskFactory {
    public static class ItemTaskFactory extends TaskFactory<AllTask.ItemTask>
    {
        @Override
        public AllTask.ItemTask create(JsonObject json)
        {
            return create(
                json.get("item_id").getAsInt(),
                json.get("item_name").getAsString(),
                json.get("quantity").getAsInt(),
                json.get("acquired").getAsInt()
            );
        }

        @Override
        public AllTask.ItemTask create()
        {
            return new AllTask.ItemTask();
        }

        public AllTask.ItemTask create(int itemId, String itemName, int quantity, int acquired)
        {
            AllTask.ItemTask task = create();
            task.setItemId(itemId);
            task.setItemName(itemName);
            task.setQuantity(quantity);
            task.setAcquired(acquired);
            return task;
        }

        public AllTask.ItemTask create(int itemId, String itemName, int quantity)
        {
            return create(itemId, itemName, quantity, 0);
        }
    }

    public static class ManualTaskFactory extends TaskFactory<AllTask.ManualTask>
    {
        @Override
        public AllTask.ManualTask create(JsonObject json)
        {
            return create(
                json.get("description").getAsString(),
                json.get("done").getAsBoolean()
            );
        }

        @Override
        public AllTask.ManualTask create()
        {
            return new AllTask.ManualTask();
        }

        public AllTask.ManualTask create(String description, boolean done)
        {
            AllTask.ManualTask task = create();
            task.setDescription(description);
            task.setDone(done);
            return task;
        }

        public AllTask.ManualTask create(String description)
        {
            return create(description, false);
        }
    }

    public static class QuestTaskFactory extends TaskFactory<AllTask.QuestTask>
    {
        @Override
        public AllTask.QuestTask create(JsonObject json)
        {
            return create(getQuestById(json.get("quest_id").getAsInt()));
        }

        @Override
        public AllTask.QuestTask create()
        {
            return new AllTask.QuestTask();
        }

        public AllTask.QuestTask create(Quest quest)
        {
            AllTask.QuestTask task = create();
            task.setQuest(quest);
            return task;
        }

        private Quest getQuestById(int id)
        {
            for (Quest quest : Quest.values()) {
                if (quest.getId() == id) {
                    return quest;
                }
            }
            throw new IllegalStateException("Quest not found");
        }
    }

    public static class SkillLevelTaskFactory extends TaskFactory<AllTask.SkillLevelTask>
    {
        @Override
        public AllTask.SkillLevelTask create(JsonObject json)
        {
            return create(
                Skill.valueOf(json.get("skill").getAsString().toUpperCase()),
                json.get("level").getAsInt()
            );
        }

        @Override
        public AllTask.SkillLevelTask create()
        {
            return new AllTask.SkillLevelTask();
        }

        public AllTask.SkillLevelTask create(Skill skill, int level)
        {
            AllTask.SkillLevelTask task = create();
            task.setSkill(skill);
            task.setLevel(level);
            return task;
        }
    }

    public static class SkillXpTaskFactory extends TaskFactory<AllTask.SkillXpTask>
    {
        @Override
        public AllTask.SkillXpTask create(JsonObject json)
        {
            return create(Skill.valueOf(json.get("skill")
                .getAsString()
                .toUpperCase()), json.get("xp").getAsInt());
        }

        @Override
        public AllTask.SkillXpTask create()
        {
            return new AllTask.SkillXpTask();
        }

        public AllTask.SkillXpTask create(Skill skill, int xp)
        {
            AllTask.SkillXpTask task = create();
            task.setSkill(skill);
            task.setXp(xp);
            return task;
        }
    }
}
