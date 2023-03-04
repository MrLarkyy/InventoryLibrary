package xyz.larkyy.inventorylibrary.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.history.MenuHistory;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedButton;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;

import java.util.function.Consumer;

public class InventoryAPI {

    public static RenderedComponent createReturnButton(Player player, ItemStack returnItemStack, ItemStack closeItemStack,
                                                       SlotSelection slotSelection, Consumer<CustomInventoryClickEvent> clickEvent) {
        var history = getHistory(player);
        ItemStack itemStack;
        if (history.hasPreviousMenu()) {
            itemStack = returnItemStack;
        } else {
            itemStack = closeItemStack;
        }
        return new RenderedButton(itemStack,slotSelection,event -> {
            history.openPrevious();
            clickEvent.accept(event);
        });
    }

    public static RenderedComponent createReturnButton(Player player, ItemStack returnItemStack, ItemStack closeItemStack, SlotSelection slotSelection) {
        return createReturnButton(player,returnItemStack,closeItemStack,slotSelection,event -> {});
    }

    public static MenuHistory getHistory(Player player) {
        return InventoryHandler.getInstance().getHistoryHandler().getOrCreate(player);
    }

}
