package xyz.larkyy.inventorylibrary.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public interface IRenderHandler {


    Inventory createInventory(InventoryHolder holder, org.bukkit.event.inventory.InventoryType inventoryType, int size);
    void openMenu(Player player, RenderedMenu renderedMenu, int id);
    int openNewMenu(Player player, RenderedMenu renderedMenu);
    Inventory getOpenedMenu(Player player);

}
