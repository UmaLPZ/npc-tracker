package com.example.npcgoaltracker.goal.factory;

import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.goal.TaskStatus;
import com.google.gson.JsonObject;

abstract public class TaskFactory<T extends Task>
{
    public T create(JsonObject json)
    {
        T task = create();

        task.setResult(TaskStatus.valueOf(json.get("previous_result").getAsString().toUpperCase()));
        return task;
    }

    public abstract T create();
}
