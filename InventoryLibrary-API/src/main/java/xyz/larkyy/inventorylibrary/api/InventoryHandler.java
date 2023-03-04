package xyz.larkyy.inventorylibrary.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedServerboundContainerClickPacket;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

import java.util.*;

public class InventoryHandler {

    private final Map<UUID, List<Integer>> cachedInventoryIds = new HashMap<>();

    private static InventoryHandler instance;

    private final JavaPlugin plugin;
    private final NMSHandler nmsHandler;

    private final PacketListener packetListener;
    private final HistoryHandler historyHandler;

    public static InventoryHandler init(JavaPlugin plugin, NMSHandler nmsHandler) {
        if (instance != null) {
            return instance;
        }
        instance = new InventoryHandler(plugin,nmsHandler);
        return instance;
    }

    private InventoryHandler(JavaPlugin plugin, NMSHandler nmsHandler) {
        this.plugin = plugin;
        this.nmsHandler = nmsHandler;
        this.packetListener = new PacketListener(nmsHandler.getPlayerPacketInjector());
        Bukkit.getPluginManager().registerEvents(packetListener,plugin);
        historyHandler = new HistoryHandler();

        registerListeners();
        onEnable();
    }

    public static InventoryHandler getInstance() {
        return instance;
    }

    public IRenderHandler getRenderHandler() {
        return nmsHandler.getRenderHandler();
    }

    public IItemHandler getItemHandler() {
        return nmsHandler.getItemHandler();
    }

    public HistoryHandler getHistoryHandler() {
        return historyHandler;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    /*
            Closes all opened custom inventories
         */
    public void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            var holder = getRenderHandler().getOpenedMenu(player).getHolder();
            if (holder instanceof RenderedMenu renderedMenu) {
                player.closeInventory();
            }
        }
    }

    public void addCachedInventoryId(UUID uuid, int id) {
        List<Integer> list;
        if (cachedInventoryIds.containsKey(uuid)) {
            list = cachedInventoryIds.get(uuid);
        } else {
            list = new ArrayList<>();
            cachedInventoryIds.put(uuid,list);
        }
        if (!list.contains(id)) {
            list.add(id);
        }
    }

    public void removeCachedInventoryId(UUID uuid, int id) {
        if (cachedInventoryIds.containsKey(uuid)) {
            List<Integer> list = cachedInventoryIds.get(uuid);
            list.remove(id);
        }
    }

    public boolean isCustomInventoryId(UUID uuid, int id) {
        if (cachedInventoryIds.containsKey(uuid)) {
            List<Integer> list = cachedInventoryIds.get(uuid);
            return list.contains(id);
        }
        return false;
    }

    public RenderedMenu getOpenedMenu(Player player) {
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof RenderedMenu renderedMenu) {
            return renderedMenu;
        }
        return null;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private void onEnable() {
        packetListener.loadInjections();
    }

    public void onDisable() {
        packetListener.unloadInjections();
    }

    private void registerListeners() {
        packetListener.register(WrappedServerboundContainerClickPacket.class, event -> {
            var player = event.getPlayer();

            if (!isCustomInventoryId(player.getUniqueId(),event.getContainerId())) {
                return;
            }
            var openedMenu = getOpenedMenu(player);
            if (openedMenu == null) {
                return;
            }

            var bukkitEvent = new CustomInventoryClickEvent(openedMenu,player,event.getClickType(),event.getSlotNum(),
                    event.getChangedSlots(),event.getCarriedItem());
            if (openedMenu.interact(bukkitEvent)) {
                event.setCancelled(true);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(bukkitEvent);
                }
            }.runTask(plugin);
        });
        /*
        packetListener.register(WrappedClientboundContainerSetContentPacket.class, event -> {
            var player = event.getPlayer();

            var openedMenu = getOpenedMenu(player);
            if (openedMenu == null) {
                return;
            }
            Bukkit.broadcastMessage("Opened menu is not null!");
            event.setCancelled(true);
        });
         */
    }
}
