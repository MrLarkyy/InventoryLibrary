package xyz.larkyy.inventorylibrary.api.ui.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;

import java.util.ArrayList;
import java.util.List;

public class CustomInventoryClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private final RenderedMenu renderedMenu;
    private final Player player;
    private final ClickType clickType;
    private final int slot;
    private final Int2ObjectMap<ItemStack> changedSlots;
    private final ItemStack carriedItem;
    private final List<RenderedComponent> clickComponents;

    public CustomInventoryClickEvent(RenderedMenu renderedMenu, Player player, ClickType clickType, int slot,
                                     Int2ObjectMap<ItemStack> changedSlots, ItemStack carriedItem) {
        this.renderedMenu = renderedMenu;
        this.player = player;
        this.clickType = clickType;
        this.slot = slot;
        this.changedSlots = changedSlots;
        this.carriedItem = carriedItem;
        this.clickComponents = new ArrayList<>();
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

    public ClickType getClickType() {
        return clickType;
    }

    public int getSlot() {
        return slot;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return changedSlots;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    public List<RenderedComponent> getClickComponents() {
        return clickComponents;
    }
}
