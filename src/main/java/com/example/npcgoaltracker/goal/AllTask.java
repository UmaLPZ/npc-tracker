package com.example.npcgoaltracker.goal;

import com.google.gson.JsonObject;
import com.example.npcgoaltracker.goal.factory.AllTaskFactory;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

import java.awt.image.BufferedImage;

public class AllTask {
    public static class ItemTask extends Task
    {
        @Getter
        private BufferedImage cachedIcon;

        @Setter
        @Getter
        private int quantity;

        @Setter
        @Getter
        private int acquired = 0;

        @Setter
        @Getter
        private int itemId;

        @Setter
        @Getter
        private String itemName;

        @Override
        public String toString()
        {
            if (quantity == 1) {
                return itemName;
            }

            if (acquired > 0 && acquired < quantity) {
                return String.format("%,d", acquired) + "/" + String.format("%,d", quantity) + " x " + itemName;
            }

            return String.format("%,d", quantity) + " x " + itemName;
        }

        @Override
        public TaskType getType()
        {
            return TaskType.ITEM;
        }

        @Override
        protected JsonObject addSerializedProperties(
            JsonObject json)
        {
            json.addProperty("item_id", itemId);
            json.addProperty("item_name", itemName);
            json.addProperty("quantity", quantity);
            json.addProperty("acquired", acquired);
            return json;
        }

        @Override
        public Class<AllTaskFactory.ItemTaskFactory> getFactoryClass()
        {
            return AllTaskFactory.ItemTaskFactory.class;
        }

        public BufferedImage setCachedIcon(BufferedImage cachedIcon)
        {
            return this.cachedIcon = cachedIcon;
        }
    }

    public static class ManualTask extends Task
    {
        @Getter
        @Setter
        private boolean done = false;

        @Getter
        @Setter
        private String description;

        public void toggle()
        {
            done = !done;
        }

        @Override
        public String toString()
        {
            return description;
        }

        @Override
        public TaskType getType()
        {
            return TaskType.MANUAL;
        }

        @Override
        public JsonObject addSerializedProperties(JsonObject json)
        {
            json.addProperty("done", done);
            json.addProperty("description", description);
            return json;
        }

        @Override
        public Class<AllTaskFactory.ManualTaskFactory> getFactoryClass()
        {
            return AllTaskFactory.ManualTaskFactory.class;
        }
    }

    public static class QuestTask extends Task
    {
        @Getter
        @Setter
        private Quest quest;

        @Override
        public String toString()
        {
            return quest.getName();
        }

        @Override
        public TaskType getType()
        {
            return TaskType.QUEST;
        }

        @Override
        protected JsonObject addSerializedProperties(JsonObject json)
        {
            json.addProperty("quest_id", quest.getId());
            return json;
        }

        @Override
        public Class<AllTaskFactory.QuestTaskFactory> getFactoryClass()
        {
            return AllTaskFactory.QuestTaskFactory.class;
        }
    }

    public static class SkillLevelTask extends Task
    {
        @Setter
        @Getter
        private Skill skill;

        @Getter
        @Setter
        private int level;

        @Override
        public String toString()
        {
            return level + " " + skill.getName();
        }

        @Override
        public TaskType getType()
        {
            return TaskType.SKILL_LEVEL;
        }

        @Override
        protected JsonObject addSerializedProperties(JsonObject json)
        {
            json.addProperty("skill", skill.getName());
            json.addProperty("level", level);
            return json;
        }

        @Override
        public Class<AllTaskFactory.SkillLevelTaskFactory> getFactoryClass()
        {
            return AllTaskFactory.SkillLevelTaskFactory.class;
        }
    }

    public static class SkillXpTask extends Task
    {
        @Getter
        @Setter
        private Skill skill;

        @Getter
        @Setter
        private int xp;

        @Override
        public String toString()
        {
            return String.format("%,d", xp) + " " + skill.getName() + " XP";
        }

        @Override
        public TaskType getType()
        {
            return TaskType.SKILL_XP;
        }

        @Override
        protected JsonObject addSerializedProperties(JsonObject json)
        {
            json.addProperty("skill", skill.getName());
            json.addProperty("xp", xp);
            return json;
        }

        @Override
        public Class<AllTaskFactory.SkillXpTaskFactory> getFactoryClass()
        {
            return AllTaskFactory.SkillXpTaskFactory.class;
        }
    }
}
