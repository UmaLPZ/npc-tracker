package com.example.npcgoaltracker.ui.components;

import com.example.npcgoaltracker.ui.Refreshable;
import com.example.npcgoaltracker.ReorderableList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;

public class ListItemPanel<T> extends JPanel implements Refreshable
{
    private final JMenuItem moveUp = new JMenuItem("Move up");
    private final JMenuItem moveDown = new JMenuItem("Move down down down");
    private final JMenuItem moveToTop = new JMenuItem("Move to top tip top");
    private final JMenuItem moveToBottom = new JMenuItem("Move to bottom");
    private final JMenuItem removeTask = new JMenuItem("Remove");
    private final JPopupMenu popupMenu = new JPopupMenu();

    private final ReorderableList<T> list;
    private final T item;

    @Setter
    private Runnable runOnReorder;

    public ListItemPanel(ReorderableList<T> list, T item)
    {
        super(new BorderLayout());
        this.list = list;
        this.item = item;

        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        moveUp.addActionListener(e -> {
            list.move(item, -1);
            runOnReorder.run();
        });

        moveDown.addActionListener(e -> {
            list.move(item, 1);
            runOnReorder.run();
        });

        moveToTop.addActionListener(e -> {
            list.moveToTop(item);
            runOnReorder.run();
        });

        moveToBottom.addActionListener(e -> {
            list.moveToBottom(item);
            runOnReorder.run();
        });

        removeTask.addActionListener(e -> {
            list.remove(item);
            runOnReorder.run();
        });

        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

        setComponentPopupMenu(popupMenu);
    }

    @Override
    public void setBackground(Color bg)
    {
        super.setBackground(bg);
        for (Component component : getComponents()) {
            component.setBackground(bg);
        }
    }

    @Override
    public void refresh()
    {
        refreshMenu();

        for (Component component : getComponents()) {
            if (component instanceof Refreshable) {
                ((Refreshable) component).refresh();
            }
        }
    }

    public void refreshMenu()
    {
        popupMenu.removeAll();
        if (!list.isFirst(item)) {
            popupMenu.add(moveUp);
        }
        if (!list.isLast(item)) {
            popupMenu.add(moveDown);
        }
        if (!list.isFirst(item)) {
            popupMenu.add(moveToTop);
        }
        if (!list.isLast(item)) {
            popupMenu.add(moveToBottom);
        }
        popupMenu.add(removeTask);
    }

    public ListItemPanel<T> add(Component comp)
    {
        super.add(comp, BorderLayout.CENTER);
        return this;
    }

    public ListItemPanel<T> onClick(Consumer<MouseEvent> clickListener)
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickListener.accept(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                setBackground(ColorScheme.DARK_GRAY_COLOR);
            }
        });
        return this;
    }
}
