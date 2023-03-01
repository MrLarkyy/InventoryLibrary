package xyz.larkyy.inventorylibrary.api.ui;

import java.util.ArrayList;
import java.util.List;

public class SlotSelection implements Cloneable {

    private List<Integer> slots = new ArrayList<>();

    public SlotSelection(List<Integer> slots) {
        this.slots = slots;
    }
    public SlotSelection(int from, int to) {
        var min = Math.min(from,to);
        var max = Math.max(from,to);

        for (int i = min; i <= max; i++) {
            slots.add(i);
        }
    }
    @Override
    public SlotSelection clone() {
        return new SlotSelection(new ArrayList<>(slots));
    }

    public List<Integer> slots() {
        return slots;
    }

}
