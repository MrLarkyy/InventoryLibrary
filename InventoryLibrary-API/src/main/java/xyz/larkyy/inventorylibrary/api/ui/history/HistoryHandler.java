package xyz.larkyy.inventorylibrary.api.ui.history;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HistoryHandler {

    private final Map<UUID,MenuHistory> historyRegistry = new HashMap<>();

    public MenuHistory getOrCreate(Player player) {
        if (historyRegistry.containsKey(player.getUniqueId())) {
            return historyRegistry.get(player.getUniqueId());
        }
        var history = new MenuHistory(player);
        historyRegistry.put(player.getUniqueId(),history);
        return history;
    }

    public void removeHistory(Player player) {
        removeHistory(player.getUniqueId());
    }
    public void removeHistory(UUID uuid) {
        historyRegistry.remove(uuid);
    }
}
