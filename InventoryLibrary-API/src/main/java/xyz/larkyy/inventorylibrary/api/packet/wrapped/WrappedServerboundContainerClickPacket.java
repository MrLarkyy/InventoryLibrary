package xyz.larkyy.inventorylibrary.api.packet.wrapped;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.packet.PacketType;

public class WrappedServerboundContainerClickPacket extends WrappedPacket{

    private final int buttonNum;
    private final int containerId;
    private final ItemStack carriedItem;
    private final Int2ObjectMap<ItemStack> changedSlots;
    private final ClickType clickType;
    private final int slotNum;
    private final int stateId;

    public WrappedServerboundContainerClickPacket(Player player, int buttonNum, int containerId, ItemStack carriedItem,
                                                  Int2ObjectMap<ItemStack> changedSlots, ClickType clickType,
                                                  int slotNum, int stateId) {
        super(PacketType.SERVER_BOUND_CONTAINER_CLICK_PACKET, player);
        this.buttonNum = buttonNum;
        this.carriedItem = carriedItem;
        this.containerId = containerId;
        this.changedSlots = changedSlots;
        this.clickType = clickType;
        this.slotNum = slotNum;
        this.stateId = stateId;
    }

    public int getContainerId() {
        return containerId;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public int getButtonNum() {
        return buttonNum;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public int getStateId() {
        return stateId;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return changedSlots;
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }
}
