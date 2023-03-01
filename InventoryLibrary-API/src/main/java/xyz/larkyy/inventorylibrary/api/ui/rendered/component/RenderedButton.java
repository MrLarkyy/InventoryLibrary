package xyz.larkyy.inventorylibrary.api.ui.rendered.component;

import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.template.component.Button;

import java.util.function.Consumer;

public class RenderedButton implements RenderedComponent {

    private final Button button;

    private ItemStack itemStack;
    private Consumer<CustomInventoryClickEvent> clickConsumer;
    private SlotSelection slotSelection;


    public RenderedButton(Button button) {
        this.button = button;
        applyValues(button);
    }

    public void applyValues(Button button) {
        this.itemStack = button.getItemStack().clone();
        this.clickConsumer = button.getClickConsumer();
        this.slotSelection = button.getSlotSelection().clone();
    }

    public void resetDefaults() {
        applyValues(button);
    }

    public void setSlotSelection(SlotSelection slotSelection) {
        this.slotSelection = slotSelection;
    }

    public void setClickConsumer(Consumer<CustomInventoryClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public SlotSelection getSlotSelection() {
        return slotSelection;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void interact(CustomInventoryClickEvent event) {
        if (slotSelection.slots().contains(event.getSlot())) {
            clickConsumer.accept(event);
        }
    }
}
