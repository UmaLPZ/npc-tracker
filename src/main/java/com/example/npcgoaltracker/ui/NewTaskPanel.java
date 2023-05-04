package com.example.npcgoaltracker.ui;

import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.ui.components.TextButton;
import com.example.npcgoaltracker.ui.inputs.AllTaskInput;
import com.example.npcgoaltracker.ui.inputs.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class NewTaskPanel extends JPanel
{
    private final TextButton moreOptionsButton;
    private final GoalTrackerPlugin plugin;
    private final Goal goal;

    private JPanel moreOptionsPanel;
    private Runnable updater;

    NewTaskPanel(GoalTrackerPlugin plugin, Goal goal)
    {
        super();
        this.plugin = plugin;
        this.goal = goal;

        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = 0;
        constraints.ipady = 8;

        add(new AllTaskInput.ManualTaskInput(plugin, goal).onUpdate(this::update), constraints);
        constraints.gridy++;

        moreOptionsButton = new TextButton("+ More options");
        moreOptionsButton.setBorder(new EmptyBorder(4, 8, 0, 8));
        moreOptionsButton.onClick(e -> {
            if (moreOptionsPanel.isVisible()) {
                hideMoreOptions();
            } else {
                showMoreOptions();
            }
        });
        JPanel moreOptionsButtonPanel = new JPanel(new BorderLayout());
        moreOptionsButtonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        moreOptionsButtonPanel.add(moreOptionsButton, BorderLayout.WEST);

        add(moreOptionsButtonPanel, constraints);
        constraints.gridy++;

        createMoreOptionsPanel();
        add(moreOptionsPanel, constraints);
    }

    private void update()
    {
        updater.run();
    }

    private void hideMoreOptions()
    {
        moreOptionsButton.setText("+ More options");
        moreOptionsButton.setMainColor(ColorScheme.PROGRESS_COMPLETE_COLOR);

        moreOptionsPanel.setVisible(false);
    }

    private void showMoreOptions()
    {
        moreOptionsButton.setText("- More options");
        moreOptionsButton.setMainColor(ColorScheme.PROGRESS_ERROR_COLOR);

        moreOptionsPanel.setVisible(true);
    }

    private void createMoreOptionsPanel()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = 0;
        constraints.ipady = 8;

        moreOptionsPanel = new JPanel(new GridBagLayout());
        moreOptionsPanel.setVisible(false);
        moreOptionsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        moreOptionsPanel.add(new AllTaskInput.SkillLevelTaskInput(plugin, goal).onUpdate(this::update), constraints);
        constraints.gridy++;

        moreOptionsPanel.add(new AllTaskInput.SkillXpTaskInput(plugin, goal).onUpdate(this::update), constraints);
        constraints.gridy++;

        moreOptionsPanel.add(new AllTaskInput.QuestTaskInput(plugin, goal).onUpdate(this::update), constraints);
        constraints.gridy++;

        moreOptionsPanel.add(new AllTaskInput.ItemTaskInput(plugin, goal).onUpdate(this::update), constraints);
        constraints.gridy++;
    }

    public void onUpdate(Runnable updater)
    {
        this.updater = updater;
    }
}
