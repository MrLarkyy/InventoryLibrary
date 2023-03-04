package xyz.larkyy.inventorylibrary.api.ui.rendered.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;

import java.util.Map;

public interface RenderedComponent {
    boolean interact(CustomInventoryClickEvent event);
    SlotSelection getSlotSelection();
    ItemStack getItemStack();

    boolean isVisible(Player player);
}
