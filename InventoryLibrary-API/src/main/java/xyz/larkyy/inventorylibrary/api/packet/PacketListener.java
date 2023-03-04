package xyz.larkyy.inventorylibrary.api.packet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PacketListener implements Listener {

    private final Map<Class<? extends WrappedPacket>,PacketListenerRegistry<? extends WrappedPacket>> listeners = new HashMap<>();
    private final PlayerPacketInjector playerPacketInjector;

    public PacketListener(PlayerPacketInjector playerPacketInjector) {
        this.playerPacketInjector = playerPacketInjector;
    }

    public <T extends WrappedPacket> void register(Class<T> wrappedPacketClass, Consumer<T> consumer) {
        PacketListenerRegistry<T> registry;
        if (listeners.containsKey(wrappedPacketClass)) {
            registry = (PacketListenerRegistry<T>) listeners.get(wrappedPacketClass);
        } else {
            registry = new PacketListenerRegistry<>();
            listeners.put(wrappedPacketClass,registry);
        }
        registry.register(consumer);
    }

    public <T extends WrappedPacket> void call(T wrappedPacket) {
        PacketListenerRegistry<T> registry = (PacketListenerRegistry<T>) listeners.get(wrappedPacket.getClass());
        if (registry == null) {
            return;
        }
        registry.call(wrappedPacket);
    }

    public boolean isListeningTo(Class<? extends WrappedPacket> wrappedPacketClass) {
        return listeners.containsKey(wrappedPacketClass);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerPacketInjector.inject(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerPacketInjector.eject(e.getPlayer());
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        var invHandler = InventoryHandler.getInstance();
        var opened = invHandler.getOpenedMenu(player);
        if (opened == null) {
            return;
        }
        opened.handleClose(player);
    }

    public void unloadInjections() {
        Bukkit.getOnlinePlayers().forEach(playerPacketInjector::eject);
    }

    public void loadInjections() {
        Bukkit.getOnlinePlayers().forEach(playerPacketInjector::inject);
    }
}
