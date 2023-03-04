package xyz.larkyy.inventorylibrary.api.ui.rendered.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.template.component.Button;

import java.util.function.Consumer;
import java.util.function.Function;

public class RenderedButton implements RenderedComponent {

    private final Button button;

    private ItemStack itemStack;
    private Consumer<CustomInventoryClickEvent> clickConsumer;
    private Function<Player,Boolean> viewCondition = (p) -> true;
    private SlotSelection slotSelection;

    public RenderedButton(Button button) {
        this.button = button;
        applyValues(button);
    }

    public RenderedButton(ItemStack itemStack, SlotSelection slotSelection) {
        button = null;
        this.itemStack = itemStack;
        this.slotSelection = slotSelection;
        this.clickConsumer = (e) -> {};
    }
    public RenderedButton(ItemStack itemStack, SlotSelection slotSelection, Consumer<CustomInventoryClickEvent> clickConsumer) {
        button = null;
        this.itemStack = itemStack;
        this.slotSelection = slotSelection;
        this.clickConsumer = clickConsumer;
    }

    public void applyValues(Button button) {
        this.itemStack = button.getItemStack().clone();
        this.clickConsumer = button.getClickConsumer();
        this.slotSelection = button.getSlotSelection().clone();
    }

    public Function<Player, Boolean> getViewCondition() {
        return viewCondition;
    }

    public void setViewCondition(Function<Player, Boolean> viewCondition) {
        this.viewCondition = viewCondition;
    }

    public void resetDefaults() {
        if (button == null) {
            return;
        }
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
    public boolean isVisible(Player player) {
        return viewCondition.apply(player);
    }

    @Override
    public boolean interact(CustomInventoryClickEvent event) {
        if (slotSelection.slots().contains(event.getSlot())) {
            if (viewCondition.apply(event.getPlayer())) {
                clickConsumer.accept(event);
                return true;
            }
        }
        return false;
    }
}
