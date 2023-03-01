package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.Utils;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.PlayerPacketInjector;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedClientboundOpenScreenPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedServerboundContainerClickPacket;

public class PlayerPacketInjectorImpl implements PlayerPacketInjector {
    @Override
    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer)player;
        var channel = craftPlayer.getHandle().connection.connection.channel;
        ChannelDuplexHandler cdh = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packetObj) {
                Packet<?> pkt = (Packet<?>) packetObj;
                var name = pkt.getClass().getSimpleName();

                WrappedPacket wrapped = null;

                switch (name.toLowerCase()) {
                    case "packetplayinwindowclick" -> {
                        if (!packetListener().isListeningTo(WrappedServerboundContainerClickPacket.class)) {
                            break;
                        }
                        ServerboundContainerClickPacket packet = (ServerboundContainerClickPacket) pkt;
                        var changedSlots =
                                Utils.map(packet.getChangedSlots(),CraftItemStack::asBukkitCopy);

                        wrapped = new WrappedServerboundContainerClickPacket(
                                player,
                                packet.getButtonNum(),
                                packet.getContainerId(),
                                CraftItemStack.asBukkitCopy(packet.getCarriedItem()),
                                changedSlots,
                                ClickType.get(packet.getClickType().name()),
                                packet.getSlotNum(),
                                packet.getStateId()
                        );
                        InventoryHandler.getInstance().getPacketListener().call(wrapped);

                    }
                }

                if (wrapped != null && wrapped.isCancelled()) {
                    return;
                }

                try {
                    super.channelRead(ctx,packetObj);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packetObj, ChannelPromise promise) {
                Packet<?> pkt = (Packet<?>) packetObj;
                var name = pkt.getClass().getSimpleName();

                WrappedPacket wrapped = null;

                switch (name.toLowerCase()) {
                    case "packetplayoutopenwindow" -> {
                        if (!packetListener().isListeningTo(WrappedClientboundOpenScreenPacket.class)) {
                            break;
                        }
                        ClientboundOpenScreenPacket packet = (ClientboundOpenScreenPacket) pkt;
                        wrapped = new WrappedClientboundOpenScreenPacket(player,packet.getContainerId(),packet.getTitle().getString());
                        InventoryHandler.getInstance().getPacketListener().call(wrapped);
                    }
                }
                if (wrapped != null && wrapped.isCancelled()) {
                    return;
                }

                try {
                    super.write(ctx,packetObj,promise);
                } catch (Exception ignored) {

                }
            }
        };

        if (channel != null) {
            channel.eventLoop().execute(() ->
                    channel.pipeline().addBefore("packet_handler", "InventoryLibrary_packet_reader", cdh));
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
        }
    }

    private InventoryHandler inventoryHandler() {
        return InventoryHandler.getInstance();
    }

    private PacketListener packetListener() {
        return inventoryHandler().getPacketListener();
    }
}
