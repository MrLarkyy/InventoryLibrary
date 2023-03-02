package xyz.larkyy.inventorylibrary.api.ui.rendered.component;

import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;

import java.util.function.Consumer;

public class RenderedPlayerItem implements RenderedComponent{

    private final ItemStack itemStack;
    private Consumer<CustomInventoryClickEvent> clickConsumer;
    private SlotSelection slotSelection;

    public RenderedPlayerItem(ItemStack itemStack, Consumer<CustomInventoryClickEvent> clickConsumer, SlotSelection slotSelection) {
        this.itemStack = itemStack;
        this.clickConsumer = clickConsumer;
        this.slotSelection = slotSelection;
    }

    @Override
    public boolean interact(CustomInventoryClickEvent event) {
        if (slotSelection.slots().contains(event.getSlot())) {
            clickConsumer.accept(event);
            return true;
        }
        return false;
    }

    @Override
    public SlotSelection getSlotSelection() {
        return slotSelection;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }
}
