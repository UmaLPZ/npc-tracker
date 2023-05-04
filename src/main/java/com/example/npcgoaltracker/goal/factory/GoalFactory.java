package com.example.npcgoaltracker.goal.factory;

import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.goal.TaskType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.example.npcgoaltracker.GoalManager;
import com.example.npcgoaltracker.services.TaskCheckerService;
import com.example.npcgoaltracker.services.TaskFactoryService;
import javax.inject.Inject;

public class GoalFactory
{
    @Inject
    private TaskFactoryService taskFactoryService;

    @Inject
    private TaskCheckerService taskCheckerService;

    @Inject
    private GoalManager goalManager;

    public Goal create(JsonObject json) throws
        Exception
    {
        Goal goal = create();
        goal.setDescription(json.get("description").getAsString());
        goal.setDisplayOrder(json.get("display_order").getAsInt());

        for (JsonElement item : json.get("items").getAsJsonArray()) {
            JsonObject obj = item.getAsJsonObject();

            TaskFactory<?> factory = taskFactoryService
                .get(TaskType.fromString(obj.get("type").getAsString()).getFactory());

            goal.add(factory.create(obj));
        }

        return goal;
    }

    public Goal create()
    {
        return new Goal(goalManager, taskCheckerService);
    }
}
