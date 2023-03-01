package xyz.larkyy.inventorylibrary.api.ui.template.component;

import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Button implements Component {

    private ItemStack itemStack;
    private SlotSelection slotSelection;
    private Consumer<CustomInventoryClickEvent> clickConsumer;

    public Button(ItemStack itemStack, SlotSelection slotSelection, @Nonnull Consumer<CustomInventoryClickEvent> clickConsumer) {
        this.itemStack = itemStack;
        this.slotSelection = slotSelection;
        this.clickConsumer = clickConsumer;
    }

    public Button(ItemStack itemStack, SlotSelection slotSelection) {
        this(itemStack,slotSelection,e -> {});
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SlotSelection getSlotSelection() {
        return slotSelection;
    }

    public Consumer<CustomInventoryClickEvent> getClickConsumer() {
        return clickConsumer;
    }

    public void setClickConsumer(Consumer<CustomInventoryClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setSlotSelection(SlotSelection slotSelection) {
        this.slotSelection = slotSelection;
    }
}
