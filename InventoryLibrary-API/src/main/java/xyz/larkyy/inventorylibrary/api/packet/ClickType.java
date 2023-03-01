package xyz.larkyy.inventorylibrary.api.packet;

import java.util.Locale;

public enum ClickType {

    PICKUP,
    QUICK_MOVE,
    SWAP,
    CLONE,
    THROW,
    QUICK_CRAFT,
    PICKUP_ALL;

    public static ClickType get(String type) {
        try {
            return valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }
}
