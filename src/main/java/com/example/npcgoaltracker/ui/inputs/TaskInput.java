package com.example.npcgoaltracker.ui.inputs;

import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.factory.TaskFactory;
import com.example.npcgoaltracker.ui.components.TextButton;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public abstract class TaskInput extends JPanel
{
    protected final int PREFERRED_INPUT_HEIGHT = 16;
    protected GoalTrackerPlugin plugin;
    private Goal goal;
    @Getter
    private Runnable updater;
    @Getter
    private JPanel inputRow;

    TaskInput(GoalTrackerPlugin plugin, Goal goal, String title)
    {
        super(new GridBagLayout());
        this.plugin = plugin;
        this.goal = goal;

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = 0;
        constraints.ipady = 8;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.getRunescapeSmallFont());
        titleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        titleLabel.setBorder(new EmptyBorder(2, 8, 0, 8));

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.add(titleLabel, BorderLayout.WEST);

        add(titleContainer, constraints);
        constraints.gridy++;

        inputRow = new JPanel(new BorderLayout());
        inputRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        TextButton addButton = new TextButton("Add");
        addButton.onClick(e -> onSubmit());

        inputRow.add(addButton, BorderLayout.EAST);

        add(inputRow, constraints);
        constraints.gridy++;
    }

    abstract protected void onSubmit();

    protected <T extends TaskFactory<?>> T factory(Class<T> factoryClass)
    {
        return plugin.getTaskFactoryService().get(factoryClass);
    }

    public void addTask(Task task)
    {
        goal.add(task);
        updater.run();
        reset();
    }

    abstract protected void reset();

    public TaskInput onUpdate(Runnable updater)
    {
        this.updater = updater;
        return this;
    }
}
