package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.Utils;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.PlayerPacketInjector;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPacketInjectorImpl implements PlayerPacketInjector {

    private final NMSHandlerImpl nmsHandler;

    private final Map<UUID, ChannelPipeline> pipelines = new HashMap<>();

    public PlayerPacketInjectorImpl(NMSHandlerImpl nmsHandler) {
        this.nmsHandler = nmsHandler;
    }

    public void sendPacket(Player player, Packet<?> packet) {
        var pipeline = pipelines.get(player.getUniqueId());
        if (pipeline == null) {
            return;
        }
        if (packet == null) {
            return;
        }

        var compressed = compressPacket(packet);
        if (compressed == null) {
            return;
        }
        pipeline.write(compressed);
        pipeline.flush();
    }

    private Integer getPacketId(Packet<?> packet) {
        return ConnectionProtocol.PLAY.getPacketId(PacketFlow.CLIENTBOUND, packet);
    }

    private FriendlyByteBuf compressPacket(Packet<?> packet) {
        var packetId = getPacketId(packet);
        if (packetId == null) {
            return null;
        }
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(packetId);
        packet.write(buf);

        return buf;
    }

    @Override
    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        var pipeline = craftPlayer.getHandle().connection.getConnection().channel.pipeline();

        pipelines.put(player.getUniqueId(), pipeline);

        PacketChannelDuplexHandler cdh = new PacketChannelDuplexHandler(player);

        for (String str : pipeline.toMap().keySet()) {
            if (pipeline.get(str) instanceof Connection) {
                pipeline.addBefore("packet_handler", "InventoryLibrary_packet_reader", cdh);
                break;
            }
        }
    }

    @Override
    public void eject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        if (channel != null) {
            try {
                if (channel.pipeline().names().contains("InventoryLibrary_packet_reader")) {
                    channel.pipeline().remove("InventoryLibrary_packet_reader");
                }
            } catch (Exception ignored) {
            }
            pipelines.remove(player.getUniqueId());
        }
    }

    private InventoryHandler inventoryHandler() {
        return InventoryHandler.getInstance();
    }

    private PacketListener packetListener() {
        return inventoryHandler().getPacketListener();
    }
}
