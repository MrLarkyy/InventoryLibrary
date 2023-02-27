package xyz.larkyy.inventorylibrary.api.ui.template.component;

import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Button implements Component {

    private CustomItem customItem;
    private SlotSelection slotSelection;
    private Consumer<CustomInventoryClickEvent> clickConsumer;

    public Button(CustomItem customItem, SlotSelection slotSelection, @Nonnull Consumer<CustomInventoryClickEvent> clickConsumer) {
        this.customItem = customItem;
        this.slotSelection = slotSelection;
        this.clickConsumer = clickConsumer;
    }

    public Button(CustomItem customItem, SlotSelection slotSelection) {
        this(customItem,slotSelection,e -> {});
    }

    public CustomItem getCustomItem() {
        return customItem;
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

    public void setCustomItem(CustomItem customItem) {
        this.customItem = customItem;
    }

    public void setSlotSelection(SlotSelection slotSelection) {
        this.slotSelection = slotSelection;
    }
}
