package com.example.npcgoaltracker;

import com.example.npcgoaltracker.goal.AllTask;
import com.example.npcgoaltracker.goal.Task;
import com.example.npcgoaltracker.goal.TaskType;
import com.example.npcgoaltracker.services.TaskIconService;
import com.google.inject.Provides;
import com.example.npcgoaltracker.goal.*;
import com.example.npcgoaltracker.services.TaskCheckerService;
import com.example.npcgoaltracker.services.TaskFactoryService;
import com.example.npcgoaltracker.ui.GoalTrackerPanel;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.chatbox.ChatboxItemSearch;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(name = "Goal Tracker", description = "Keep track of your goals and complete them automatically")
public class GoalTrackerPlugin extends Plugin
{
    @Getter
    @Inject
    private Client client;

    @Getter
    @Inject
    private SkillIconManager skillIconManager;

    @Getter
    @Inject
    private ItemManager itemManager;

    @Getter
    @Inject
    private ChatboxItemSearch itemSearch;

    @Inject
    private ClientToolbar clientToolbar;

    @Getter
    @Inject
    private ClientThread clientThread;

    @Getter
    @Inject
    private ItemCache itemCache;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Getter
    @Inject
    private GoalTrackerConfig config;

    @Getter
    @Inject
    private TaskFactoryService taskFactoryService;

    @Getter
    @Inject
    private TaskCheckerService taskCheckerService;

    @Getter
    @Inject
    private TaskIconService taskIconService;

    @Getter
    @Inject
    private TaskUIStatusManager uiStatusManager;

    @Getter
    @Inject
    private GoalManager goalManager;

    @Inject
    private GoalTrackerPanel goalTrackerPanel;

    private NavigationButton uiNavigationButton;

    @Setter
    private boolean validateAll = true;

    @Override
    protected void startUp()
    {
        goalManager.load();
        itemCache.load();
        goalTrackerPanel.home();

        final BufferedImage icon = itemManager.getImage(ItemID.DIRTY_NOTE);

        uiNavigationButton = NavigationButton.builder()
            .tooltip("Goal Tracker")
            .icon(icon)
            .priority(7)
            .panel(goalTrackerPanel)
            .build();

        clientToolbar.addNavigation(uiNavigationButton);

    }

    @Override
    protected void shutDown()
    {
        goalManager.save();
        itemCache.save();

        clientToolbar.removeNavigation(uiNavigationButton);
    }

    @Subscribe
    public void onSessionOpen(SessionOpen event)
    {
        goalManager.load();
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        List<AllTask.SkillLevelTask> skillLevelTasks = goalManager.getAllIncompleteTasksOfType(TaskType.SKILL_LEVEL);
        for (AllTask.SkillLevelTask task : skillLevelTasks) {
            if (event.getSkill() == task.getSkill() && event.getLevel() >= task.getLevel()) {
                notifyTask(task);
                uiStatusManager.refresh(task);
            }
        }

        List<AllTask.SkillXpTask> skillXpTasks = goalManager.getAllIncompleteTasksOfType(TaskType.SKILL_XP);
        for (AllTask.SkillXpTask task : skillXpTasks) {
            if (event.getSkill() == task.getSkill() && event.getXp() >= task.getXp()) {
                notifyTask(task);
                uiStatusManager.refresh(task);
            }
        }
    }

    public void notifyTask(Task task)
    {
        if (client.getGameState() != GameState.LOGGED_IN || task.hasBeenNotified()) {
            return;
        }

        log.debug("Notify: " + "[Goal Tracker] You have completed a task: " + task.toString() + "!");

        String message = "[Goal Tracker] You have completed a task: " + task.toString() + "!";
        String formattedMessage = new ChatMessageBuilder().append(ColorScheme.PROGRESS_COMPLETE_COLOR, message).build();
        chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .name("Goal Tracker")
            .runeLiteFormattedMessage(formattedMessage)
            .build());

        task.hasBeenNotified(true);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        // redo the login check on the next game tick
        validateAll = true;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (!validateAll) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        validateAll = false;
        // perform a full refresh just once on login
        // onGameStateChanged reports incorrect quest statuses,
        // so this need to be done in this subscriber
        goalTrackerPanel.refresh();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getMessage().contains("Quest complete")) {
            List<AllTask.QuestTask> questTasks = goalManager.getAllIncompleteTasksOfType(TaskType.QUEST);
            for (AllTask.QuestTask task : questTasks) {
                if (taskCheckerService.check(task).isCompleted()) {
                    notifyTask(task);
                    uiStatusManager.refresh(task);
                }
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        itemCache.update(event.getContainerId(), event.getItemContainer().getItems());

        List<AllTask.ItemTask> itemTasks = goalManager.getAllIncompleteTasksOfType(TaskType.ITEM);
        for (AllTask.ItemTask task : itemTasks) {
            if (task.getResult().isCompleted()) {
                continue;
            }

            if (taskCheckerService.check(task).isCompleted()) {
                notifyTask(task);
            }

            // always refresh item tasks, since the acquired
            // count could have changed
            uiStatusManager.refresh(task);
        }
    }

    @Provides
    GoalTrackerConfig getGoalTrackerConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GoalTrackerConfig.class);
    }
}
