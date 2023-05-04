package com.example.npcgoaltracker;

import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.goal.factory.GoalFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GoalSerializer
{
    @Inject
    private GoalFactory goalFactory;

    public List<Goal> deserialize(String serialized) throws Exception
    {
        List<Goal> goals = new ArrayList<>();
        JsonArray json = new JsonParser().parse(serialized).getAsJsonArray();

        for (JsonElement item : json) {
            JsonObject obj = item.getAsJsonObject();
            goals.add(goalFactory.create(obj));
        }

        return goals;
    }

    public String serialize(List<Goal> goals)
    {
        JsonArray json = new JsonArray();
        goals.forEach((goal -> json.add(goal.serialize())));

        return json.toString();
    }
}
