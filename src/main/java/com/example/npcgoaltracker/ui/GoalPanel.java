package com.example.npcgoaltracker.ui;

import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.AllTask;
import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.ui.components.EditableInput;
import com.example.npcgoaltracker.ui.components.ListItemPanel;
import com.example.npcgoaltracker.ui.components.ListPanel;
import com.example.npcgoaltracker.ui.components.TextButton;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class GoalPanel extends JPanel implements Refreshable
{
    private final GoalTrackerPlugin plugin;
    private final Goal goal;

    private final EditableInput descriptionInput;
    private final ListPanel<Task> taskListPanel;

    GoalPanel(GoalTrackerPlugin plugin, Goal goal, Runnable closeListener)
    {
        super();
        this.plugin = plugin;
        this.goal = goal;

        setLayout(new BorderLayout());

        TextButton backButton = new TextButton("< Back", ColorScheme.PROGRESS_ERROR_COLOR);
        backButton.onClick((e) -> closeListener.run());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(headerPanel, BorderLayout.NORTH);

        descriptionInput = new EditableInput(null);
        headerPanel.add(descriptionInput, BorderLayout.CENTER);

        taskListPanel = new ListPanel<>(goal, (task) -> {
            ListItemPanel<Task> taskPanel = new ListItemPanel<>(goal, task);
            taskPanel.add(new TaskItemContent(plugin, task));
            taskPanel.setBorder(new EmptyBorder(2, 4, 2, 4));

            if (task instanceof AllTask.ManualTask) {
                taskPanel.onClick(e -> {
                    ((AllTask.ManualTask) task).toggle();
                    if (plugin.getTaskCheckerService().check(task).isCompleted()) {
                        plugin.notifyTask(task);
                    }
                    plugin.getUiStatusManager().refresh(task);
                });
            }

            return taskPanel;
        });
        taskListPanel.setGap(0);
        taskListPanel.setPlaceholder("No tasks added yet");
        add(taskListPanel, BorderLayout.CENTER);

        NewTaskPanel newTaskPanel = new NewTaskPanel(plugin, goal);
        newTaskPanel.onUpdate(this::updateFromNewTask);
        add(newTaskPanel, BorderLayout.SOUTH);
    }

    public void updateFromNewTask()
    {
        taskListPanel.tryBuildList();
        taskListPanel.refresh();
        plugin.setValidateAll(true);
    }

    @Override
    public void refresh()
    {
        descriptionInput.setValue(goal.getDescription());
        taskListPanel.refresh();
    }
}
