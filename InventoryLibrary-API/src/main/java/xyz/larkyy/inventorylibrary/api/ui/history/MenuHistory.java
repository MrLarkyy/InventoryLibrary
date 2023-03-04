package xyz.larkyy.inventorylibrary.api.ui.history;

import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

import java.util.EmptyStackException;
import java.util.Stack;

public class MenuHistory {

    private final Stack<RenderedMenu> history = new Stack<>();
    private final Player player;

    public MenuHistory(Player player) {
        this.player = player;
    }

    public void add(RenderedMenu renderedMenu) {
        history.push(renderedMenu);
    }

    public void openPrevious() {
        try {
            var menu = history.pop();
            menu.open(player);
        } catch (EmptyStackException ignored) {
            var menu = history.peek();
            menu.close(player);
        }
    }

    public boolean hasPreviousMenu() {
        return !history.empty();
    }

    public void clear() {
        history.clear();
    }

}
