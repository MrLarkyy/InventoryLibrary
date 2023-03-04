package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.Utils;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedClientboundContainerSetContentPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedClientboundOpenScreenPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedServerboundContainerClickPacket;

import java.util.stream.Collectors;

public class PacketChannelDuplexHandler extends ChannelDuplexHandler {

    private final Player player;

    public PacketChannelDuplexHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packetObj) throws Exception {

        if (!(packetObj instanceof Packet<?> pkt)) {
            super.channelRead(ctx, packetObj);
            return;
        }
        var name = pkt.getClass().getSimpleName();

        WrappedPacket wrapped = null;

        switch (name.toLowerCase()) {
            case "packetplayinwindowclick" -> {
                if (!packetListener().isListeningTo(WrappedServerboundContainerClickPacket.class)) {
                    break;
                }
                ServerboundContainerClickPacket packet = (ServerboundContainerClickPacket) pkt;
                var changedSlots =
                        Utils.map(packet.getChangedSlots(), CraftItemStack::asBukkitCopy);
                wrapped = new WrappedServerboundContainerClickPacket(
                        player,
                        packet.getButtonNum(),
                        packet.getContainerId(),
                        CraftItemStack.asBukkitCopy(packet.getCarriedItem()),
                        changedSlots,
                        ClickType.get(packet.getClickType().name(), packet.getButtonNum()),
                        packet.getSlotNum(),
                        packet.getStateId()
                );
            }
        }

        if (wrapped != null) {
            InventoryHandler.getInstance().getPacketListener().call(wrapped);
            if (wrapped.isCancelled()) {
                return;
            }
        }

        super.channelRead(ctx, packetObj);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packetObj, ChannelPromise promise) throws Exception {
        if (!(packetObj instanceof Packet<?> pkt)) {
            super.write(ctx,packetObj,promise);
            return;
        }

        var name = pkt.getClass().getSimpleName();

        WrappedPacket wrapped = null;

        switch (name.toLowerCase()) {
            case "packetplayoutopenwindow" -> {

                if (!packetListener().isListeningTo(WrappedClientboundOpenScreenPacket.class)) {
                    break;
                }
                ClientboundOpenScreenPacket packet = (ClientboundOpenScreenPacket) pkt;
                wrapped = new WrappedClientboundOpenScreenPacket(player, packet.getContainerId(), packet.getTitle().getString());


            }
            case "packetplayoutwindowitems" -> {

                if (!packetListener().isListeningTo(WrappedClientboundContainerSetContentPacket.class)) {
                    break;
                }


                ClientboundContainerSetContentPacket packet = (ClientboundContainerSetContentPacket) pkt;
                if (!(((CraftPlayer)player).getHandle().inventoryMenu.getBukkitView().getTopInventory() instanceof CraftInventoryCustom)) {
                    break;
                }
                Bukkit.broadcastMessage("It is custom menu");

                wrapped = new WrappedClientboundContainerSetContentPacket(player, CraftItemStack.asBukkitCopy(packet.getCarriedItem()),
                        packet.getContainerId(),packet.getStateId(),packet.getItems()
                        .stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList()));
            }
        }

        if (wrapped != null) {
            InventoryHandler.getInstance().getPacketListener().call(wrapped);
            if (wrapped.isCancelled()) {
                return;
            }
        }

        super.write(ctx, packetObj, promise);
    }

    private InventoryHandler inventoryHandler() {
        return InventoryHandler.getInstance();
    }

    private PacketListener packetListener() {
        return inventoryHandler().getPacketListener();
    }

}
