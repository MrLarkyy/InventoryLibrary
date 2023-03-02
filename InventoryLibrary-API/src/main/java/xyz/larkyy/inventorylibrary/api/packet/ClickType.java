package xyz.larkyy.inventorylibrary.api.packet;

import java.util.Locale;

public enum ClickType {

    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    SWAP,
    CLONE,
    THROW,
    QUICK_CRAFT,
    PICKUP_ALL;

    public static ClickType get(String type, int button) {
        switch (type.toUpperCase()) {
            case "PICKUP" -> {
                if (button == 0) {
                    return LEFT_CLICK;
                } else {
                    return RIGHT_CLICK;
                }
            }
            case "QUICK_MOVE" -> {
                if (button == 0) {
                    return SHIFT_LEFT_CLICK;
                } else {
                    return SHIFT_RIGHT_CLICK;
                }
            }
        }

        try {
            return valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }
}
