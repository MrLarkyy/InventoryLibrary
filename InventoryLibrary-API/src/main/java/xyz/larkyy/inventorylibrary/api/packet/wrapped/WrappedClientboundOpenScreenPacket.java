package xyz.larkyy.inventorylibrary.api.packet.wrapped;

import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.packet.PacketType;

public class WrappedClientboundOpenScreenPacket extends WrappedPacket {

    private final int containerId;
    private final String title;

    public WrappedClientboundOpenScreenPacket(Player player, int containerId, String title) {
        super(PacketType.CLIENT_BOUND_OPEN_SCREEN_PACKET,player);
        this.containerId = containerId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getContainerId() {
        return containerId;
    }
}
