package xyz.larkyy.inventorylibrary.api.ui;

import java.util.*;

public class SlotSelection implements Cloneable {

    private Set<Integer> slots = new HashSet<>();

    public SlotSelection(List<Integer> slots) {
        this.slots.addAll(slots);
    }
    public SlotSelection(int... slots) {
        for (int slot : slots) {
            this.slots.add(slot);
        }
    }
    public SlotSelection(int from, int to) {
        var min = Math.min(from,to);
        var max = Math.max(from,to);

        for (int i = min; i <= max; i++) {
            slots.add(i);
        }
    }

    public SlotSelection(int slot) {
        this.slots = new HashSet<>(Arrays.asList(slot));
    }

    @Override
    public SlotSelection clone() {
        return new SlotSelection(new ArrayList<>(slots));
    }

    public Set<Integer> slots() {
        return slots;
    }

}
