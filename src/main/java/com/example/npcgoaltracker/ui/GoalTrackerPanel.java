package com.example.npcgoaltracker.ui;

import com.example.npcgoaltracker.GoalManager;
import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.ui.components.ListItemPanel;
import com.example.npcgoaltracker.ui.components.ListPanel;
import com.example.npcgoaltracker.ui.components.TextButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Singleton
public class GoalTrackerPanel extends PluginPanel implements Refreshable
{
    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final ListPanel<Goal> goalListPanel;
    private final GoalTrackerPlugin plugin;

    @Getter
    private Goal currentGoal;

    @Inject
    public GoalTrackerPanel(GoalTrackerPlugin plugin, GoalManager goalManager)
    {
        super(false);
        this.plugin = plugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        titlePanel.add(
            new TextButton("+ Add goal",
                e -> view(goalManager.createGoal())
            ).narrow(), BorderLayout.EAST);

        JLabel title = new JLabel();
        title.setText("Goal Tracker");
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());
        titlePanel.add(title, BorderLayout.WEST);

        goalListPanel = new ListPanel<>(goalManager,
            (goal) -> new ListItemPanel<>(goalManager, goal).onClick(e -> this.view(goal))
                .add(new GoalItemContent(plugin, goal))
        );
        goalListPanel.setGap(0);
        goalListPanel.setPlaceholder("Add a new goal using the button above");

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(goalListPanel, BorderLayout.CENTER);

        home();
    }

    public void view(Goal goal)
    {
        removeAll();

        GoalPanel goalPanel = new GoalPanel(plugin, goal, this::home);
        add(goalPanel, BorderLayout.CENTER);
        goalPanel.refresh();

        revalidate();
        repaint();
    }

    public void home()
    {
        removeAll();
        add(mainPanel, BorderLayout.CENTER);
        goalListPanel.tryBuildList();
        goalListPanel.refresh();

        revalidate();
        repaint();
    }

    @Override
    public void refresh()
    {
        // refresh single-view goal
        for (Component component : getComponents()) {
            if (component instanceof Refreshable) {
                ((Refreshable) component).refresh();
            }
        }

        goalListPanel.refresh();
    }
}
