package xyz.larkyy.inventorylibrary.api.packet.wrapped;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.packet.PacketType;

public abstract class WrappedPacket {

    private final PacketType packetType;
    private final Player player;
    private boolean cancel = false;

    public WrappedPacket(PacketType packetType, Player player) {
        this.packetType = packetType;
        this.player = player;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public Player getPlayer() {
        return player;
    }
}
