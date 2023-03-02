package xyz.larkyy.inventorylibrary.api.ui.flag;

import java.util.ArrayList;
import java.util.List;

public class InventoryFlags {

    private final List<InventoryFlag> flags;

    public InventoryFlags(List<InventoryFlag> flags) {
        this.flags = flags;
    }

    public InventoryFlags(InventoryFlag... flags) {
        this(new ArrayList<>(List.of(flags)));
    }

    public InventoryFlags() {
        this(new ArrayList<>(List.of(InventoryFlag.CLEAR_HISTORY_ON_CLOSE, InventoryFlag.CANCEL_PLAYERS_ITEM_INTERACTION)));
    }

    public void addFlag(InventoryFlag flag) {
        if (flags.contains(flag)) {
            return;
        }
        flags.add(flag);
    }

    public void removeFlag(InventoryFlag flag) {
        flags.remove(flag);
    }

    public List<InventoryFlag> getFlags() {
        return flags;
    }

    public boolean contains(InventoryFlag flag) {
        return flags.contains(flag);
    }

    public InventoryFlags clone() {
        return new InventoryFlags(new ArrayList<>(flags));
    }
}
