package xyz.larkyy.inventorylibrary.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

import java.util.List;

public interface IRenderHandler {


    Inventory createInventory(InventoryHolder holder, org.bukkit.event.inventory.InventoryType inventoryType, int size);
    void openMenu(Player player, RenderedMenu renderedMenu, int id);
    int openNewMenu(Player player, RenderedMenu renderedMenu);
    Inventory getOpenedMenu(Player player);
    void setWindowContent(Player player, int containerId, List<ItemStack> itemStacks);
    void setSlot(Player player, int inventoryId, int slot, ItemStack itemStack);
    List<ItemStack> getPlayerInventoryContent(Player player);

}
