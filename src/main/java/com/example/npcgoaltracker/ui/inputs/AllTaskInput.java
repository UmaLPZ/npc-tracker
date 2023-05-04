package com.example.npcgoaltracker.ui.inputs;

import com.example.npcgoaltracker.goal.Goal;
import com.example.npcgoaltracker.ui.SimpleDocumentListener;
import com.example.npcgoaltracker.GoalTrackerPlugin;
import com.example.npcgoaltracker.goal.factory.AllTaskFactory;
import com.example.npcgoaltracker.ui.components.ComboBox;
import com.example.npcgoaltracker.ui.components.TextButton;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class AllTaskInput {
    public static class ItemTaskInput extends TaskInput
    {
        private final ItemManager itemManager;
        private final ClientThread clientThread;

        private final FlatTextField quantityField = new FlatTextField();
        private final TextButton searchItemButton = new TextButton("Search...");
        private final JLabel selectedItemLabel = new JLabel();
        private final JPanel selectedItemPanel = new JPanel(new BorderLayout());

        private final Pattern numberPattern = Pattern.compile("^(?:\\d+)?$");
        private final Pattern mPattern = Pattern.compile("^(?:\\d+m)?$", Pattern.CASE_INSENSITIVE);
        private final Pattern kPattern = Pattern.compile("^(?:\\d+k)?$", Pattern.CASE_INSENSITIVE);

        private String quantityFieldValue = "1";
        private ItemComposition selectedItem;
        private final TextButton clearItemButton = new TextButton("X")
            .setMainColor(ColorScheme.PROGRESS_ERROR_COLOR)
            .onClick((e) -> clearSelectedItem());

        public ItemTaskInput(GoalTrackerPlugin plugin, Goal goal)
        {
            super(plugin, goal, "Item");
            this.itemManager = plugin.getItemManager();
            this.clientThread = plugin.getClientThread();

            searchItemButton.onClick(e -> {
                if (plugin.getClient().getGameState() != GameState.LOGGED_IN) {
                    JOptionPane.showMessageDialog(this,
                        "You must be logged in to choose items",
                        "UwU",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                plugin.getItemSearch()
                    .tooltipText("Choose an item")
                    .onItemSelected(this::setSelectedItem)
                    .build();
            });
            getInputRow().add(searchItemButton, BorderLayout.WEST);

            quantityField.setBorder(new EmptyBorder(0, 8, 0, 8));
            quantityField.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
            quantityField.setText(quantityFieldValue);
            quantityField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            quantityField.getDocument().addDocumentListener(
                (SimpleDocumentListener) e -> SwingUtilities.invokeLater(() -> {
                    String value = quantityField.getText();

                    if (mPattern.matcher(value).find()) {
                        value = value.replace("m", "000000");
                        quantityFieldValue = value;
                        quantityField.setText(quantityFieldValue);
                    }

                    if (kPattern.matcher(value).find()) {
                        value = value.replace("k", "000");
                        quantityFieldValue = value;
                        quantityField.setText(quantityFieldValue);
                    }

                    if (!numberPattern.matcher(value).find()) {
                        quantityField.setText(quantityFieldValue);
                        return;
                    }

                    quantityFieldValue = value;
                }));
            quantityField.setPreferredSize(new Dimension(92, PREFERRED_INPUT_HEIGHT));

            getInputRow().add(quantityField, BorderLayout.CENTER);

            selectedItemPanel.setBorder(new EmptyBorder(0, 8, 0, 8));
            selectedItemPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            selectedItemPanel.add(selectedItemLabel, BorderLayout.CENTER);
            selectedItemPanel.add(clearItemButton, BorderLayout.EAST);
        }

        private void setSelectedItem(int rawId)
        {
            clientThread.invokeLater(() -> {
                int id = itemManager.canonicalize(rawId);
                selectedItem = itemManager.getItemComposition(id);
                selectedItemLabel.setText(selectedItem.getName());

                getInputRow().remove(searchItemButton);
                getInputRow().add(selectedItemPanel, BorderLayout.WEST);

                revalidate();
                repaint();
            });
        }

        @Override
        protected void onSubmit()
        {
            if (selectedItem == null || quantityField.getText().isEmpty()) {
                return;
            }

            addTask(factory(AllTaskFactory.ItemTaskFactory.class).create(
                selectedItem.getId(),
                selectedItem.getName(),
                Integer.parseInt(quantityField.getText())
            ));
        }

        @Override
        protected void reset()
        {
            clearSelectedItem();
            quantityFieldValue = "1";
            quantityField.setText(quantityFieldValue);
        }

        private void clearSelectedItem()
        {
            selectedItem = null;

            getInputRow().remove(selectedItemPanel);
            getInputRow().add(searchItemButton, BorderLayout.WEST);

            revalidate();
            repaint();
        }
    }

    public static class ManualTaskInput extends TaskInput
    {
        private FlatTextField titleField;
        private Goal goal;

        public ManualTaskInput(GoalTrackerPlugin plugin, Goal goal)
        {
            super(plugin, goal, "Quick add");
            this.goal = goal;

            titleField = new FlatTextField();
            titleField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            titleField.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && titleField.getText()
                        .length() > 0) {
                        onSubmit();
                    }
                }
            });

            getInputRow().add(titleField, BorderLayout.CENTER);
        }

        @Override
        protected void onSubmit()
        {
            if (titleField.getText().isEmpty()) {
                return;
            }

            addTask(factory(AllTaskFactory.ManualTaskFactory.class).create(
                titleField.getText())
            );
        }

        @Override
        protected void reset()
        {
            titleField.setText("");
            titleField.requestFocusInWindow();
        }
    }

    public static class QuestTaskInput extends TaskInput
    {
        private final ComboBox<Quest> questField;

        public QuestTaskInput(GoalTrackerPlugin plugin, Goal goal)
        {
            super(plugin, goal, "Quest");

            List<Quest> quests = Arrays.asList(Quest.values());
            quests.sort(Comparator.comparing(
                (quest) -> quest.getName().replaceFirst("^(A|The) ", "")));
            questField = new ComboBox<>(quests);
            questField.setFormatter(Quest::getName);
            getInputRow().add(questField, BorderLayout.CENTER);
        }

        @Override
        protected void onSubmit()
        {
            addTask(factory(AllTaskFactory.QuestTaskFactory.class).create(
                (Quest) questField.getSelectedItem()
            ));
        }

        @Override
        protected void reset()
        {
            questField.setSelectedIndex(0);
        }
    }

    public static class SkillLevelTaskInput extends TaskInput
    {

        private FlatTextField levelField;
        private String levelFieldValue = "99";

        private ComboBox<Skill> skillField;

        private Pattern numberPattern = Pattern.compile("^(?:\\d{1,2})?$");

        public SkillLevelTaskInput(GoalTrackerPlugin plugin, Goal goal)
        {
            super(plugin, goal, "Skill level");

            levelField = new FlatTextField();
            levelField.setBorder(new EmptyBorder(0, 8, 0, 8));
            levelField.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
            levelField.setText(levelFieldValue);
            levelField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            levelField.getDocument().addDocumentListener(
                (SimpleDocumentListener) e -> SwingUtilities.invokeLater(() -> {
                    String value = levelField.getText();
                    if (!numberPattern.matcher(value).find()) {
                        levelField.setText(levelFieldValue);
                        return;
                    }
                    levelFieldValue = value;
                }));
            levelField.setPreferredSize(new Dimension(92, PREFERRED_INPUT_HEIGHT));

            getInputRow().add(levelField, BorderLayout.CENTER);

            skillField = new ComboBox<>(Skill.values());

            getInputRow().add(skillField, BorderLayout.WEST);
        }

        @Override
        protected void onSubmit()
        {
            if (levelField.getText().isEmpty()) {
                return;
            }

            addTask(factory(AllTaskFactory.SkillLevelTaskFactory.class).create(
                (Skill) skillField.getSelectedItem(),
                Integer.parseInt(levelField.getText())
            ));
        }

        @Override
        protected void reset()
        {
            levelFieldValue = "99";
            levelField.setText(levelFieldValue);

            skillField.setSelectedIndex(0);
        }
    }

    public static class SkillXpTaskInput extends TaskInput
    {
        private final FlatTextField xpField;
        private final ComboBox<Skill> skillField;
        private final Pattern numberPattern = Pattern.compile("^(?:\\d+)?$");
        private final Pattern mPattern = Pattern.compile("^(?:\\d+m)?$", Pattern.CASE_INSENSITIVE);
        private final Pattern kPattern = Pattern.compile("^(?:\\d+k)?$", Pattern.CASE_INSENSITIVE);
        private String xpFieldValue = "13034431";

        public SkillXpTaskInput(GoalTrackerPlugin plugin, Goal goal)
        {
            super(plugin, goal, "Skill XP");

            xpField = new FlatTextField();
            xpField.setBorder(new EmptyBorder(0, 8, 0, 8));
            xpField.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
            xpField.setText(xpFieldValue);
            xpField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            xpField.getDocument().addDocumentListener(
                (SimpleDocumentListener) e -> SwingUtilities.invokeLater(() -> {
                    String value = xpField.getText();

                    if (mPattern.matcher(value).find()) {
                        value = value.replace("m", "000000");
                        xpFieldValue = value;
                        xpField.setText(xpFieldValue);
                    }

                    if (kPattern.matcher(value).find()) {
                        value = value.replace("k", "000");
                        xpFieldValue = value;
                        xpField.setText(xpFieldValue);
                    }

                    if (!numberPattern.matcher(value).find()) {
                        xpField.setText(xpFieldValue);
                        return;
                    }

                    if (Integer.parseInt(value) > 200000000) {
                        xpField.setText("200000000");
                        value = "200000000";
                    }

                    xpFieldValue = value;
                }));
            xpField.setPreferredSize(new Dimension(92, PREFERRED_INPUT_HEIGHT));

            getInputRow().add(xpField, BorderLayout.CENTER);

            skillField = new ComboBox<>(Skill.values());

            getInputRow().add(skillField, BorderLayout.WEST);
        }

        @Override
        protected void onSubmit()
        {
            if (xpField.getText().isEmpty()) {
                return;
            }

            addTask(factory(AllTaskFactory.SkillXpTaskFactory.class).create(
                (Skill) skillField.getSelectedItem(),
                Integer.parseInt(xpField.getText())
            ));
        }

        @Override
        protected void reset()
        {
            xpFieldValue = "13034431";
            xpField.setText(xpFieldValue);

            skillField.setSelectedIndex(0);
        }
    }
}
