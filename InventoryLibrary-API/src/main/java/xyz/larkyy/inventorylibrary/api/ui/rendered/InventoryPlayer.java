package xyz.larkyy.inventorylibrary.api.ui.rendered;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryPlayer {

    private final UUID uuid;
    private final int inventoryId;
    private ItemStack carriedItem = new ItemStack(Material.AIR);

    public InventoryPlayer(UUID uuid, int inventoryId) {
        this.uuid = uuid;
        this.inventoryId = inventoryId;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setCarriedItem(ItemStack carriedItem) {
        this.carriedItem = carriedItem;
    }
}
