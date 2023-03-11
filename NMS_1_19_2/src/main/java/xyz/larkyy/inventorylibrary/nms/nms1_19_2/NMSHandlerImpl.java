package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.NMSHandler;

public class NMSHandlerImpl implements NMSHandler {

    private final ItemHandler itemHandler = new ItemHandler(this);
    private final PlayerPacketInjectorImpl playerPacketInjector = new PlayerPacketInjectorImpl(this);
    private final RenderHandler renderHandler = new RenderHandler(this);

    @Override
    public RenderHandler getRenderHandler() {
        return renderHandler;
    }

    @Override
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public PlayerPacketInjectorImpl getPlayerPacketInjector() {
        return playerPacketInjector;
    }

    public void sendPacket(Player player, Packet<?> packet) {
        playerPacketInjector.sendPacket(player,packet);
    }
}
