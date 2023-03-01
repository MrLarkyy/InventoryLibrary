package xyz.larkyy.inventorylibrary.api.ui.template;


import org.bukkit.event.inventory.InventoryType;

public class Menu {

    private final int size;
    private final InventoryType inventoryType;

    public Menu(InventoryType inventoryType, int size) {
        this.size = size;
        this.inventoryType = inventoryType;
    }

}
