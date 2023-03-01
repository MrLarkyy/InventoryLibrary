package xyz.larkyy.inventorylibrary.api.packet.wrapped;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.packet.PacketType;

import java.util.List;

public class WrappedClientboundContainerSetContentPacket extends WrappedPacket {

    private final ItemStack carriedItem;
    private final int containerId;
    private final int stateId;
    private final List<ItemStack> items;

    public WrappedClientboundContainerSetContentPacket(Player player, ItemStack carriedItem, int containerId,
                                                       int stateId, List<ItemStack> items) {
        super(PacketType.CLIENT_BOUND_CONTAINER_SER_CONTENT_PACKET, player);
        this.carriedItem = carriedItem;
        this.containerId = containerId;
        this.stateId = stateId;
        this.items = items;
    }

    public int getContainerId() {
        return containerId;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    public int getStateId() {
        return stateId;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
