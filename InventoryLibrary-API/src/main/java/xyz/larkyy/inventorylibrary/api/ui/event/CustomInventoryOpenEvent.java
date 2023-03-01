package xyz.larkyy.inventorylibrary.api.ui.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public class CustomInventoryOpenEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private final RenderedMenu renderedMenu;
    private final Player player;

    public CustomInventoryOpenEvent(RenderedMenu renderedMenu, Player player) {
        this.renderedMenu = renderedMenu;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public RenderedMenu getRenderedMenu() {
        return renderedMenu;
    }
}
