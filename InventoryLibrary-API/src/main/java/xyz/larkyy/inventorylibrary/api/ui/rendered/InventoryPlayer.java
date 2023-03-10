package xyz.larkyy.inventorylibrary.api.ui.rendered;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;

import java.util.List;
import java.util.UUID;

public class InventoryPlayer {

    private final UUID uuid;
    private final int inventoryId;
    private ItemStack carriedItem = new ItemStack(Material.AIR);
    private List<RenderedComponent> components;

    public InventoryPlayer(UUID uuid, int inventoryId, List<RenderedComponent> components) {
        this.uuid = uuid;
        this.inventoryId = inventoryId;
        this.components = components;
    }

    public void setComponents(List<RenderedComponent> components) {
        this.components = components;
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

    public List<RenderedComponent> getComponents() {
        return components;
    }

    public void setCarriedItem(ItemStack carriedItem) {
        this.carriedItem = carriedItem;
    }
}
