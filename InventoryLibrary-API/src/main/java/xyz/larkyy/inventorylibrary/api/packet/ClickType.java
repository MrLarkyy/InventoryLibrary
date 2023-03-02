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
    PICKUP_ALL,
    NUM_1,
    NUM_2,
    NUM_3,
    NUM_4,
    NUM_5,
    NUM_6,
    NUM_7,
    NUM_8,
    NUM_9;

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
            case "SWAP" -> {
                switch (button) {
                    case 0 -> {
                        return NUM_1;
                    }
                    case 1 -> {
                        return NUM_2;
                    }
                    case 2 -> {
                        return NUM_3;
                    }
                    case 3 -> {
                        return NUM_4;
                    }
                    case 4 -> {
                        return NUM_5;
                    }
                    case 5 -> {
                        return NUM_6;
                    }
                    case 6 -> {
                        return NUM_7;
                    }
                    case 7 -> {
                        return NUM_8;
                    }
                    case 8 -> {
                        return NUM_9;
                    }
                    case 40 -> {
                        return SWAP;
                    }
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
