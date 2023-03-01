package xyz.larkyy.inventorylibrary.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.PlayerPacketInjector;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedClientboundOpenScreenPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedServerboundContainerClickPacket;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryOpenEvent;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public class InventoryHandler {

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

    public RenderedMenu getOpenedMenu(Player player) {
        var holder = getRenderHandler().getOpenedMenu(player).getHolder();
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
            var openedMenu = getOpenedMenu(player);

            var bukkitEvent = new CustomInventoryClickEvent(openedMenu,player,event.getClickType(),event.getSlotNum());
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(bukkitEvent);
                    if (bukkitEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    openedMenu.interact(bukkitEvent);
                    if (bukkitEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }.runTask(plugin);

        });
    }
}
