package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.larkyy.inventorylibrary.api.IRenderHandler;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public class RenderHandler implements IRenderHandler {
    @Override
    public Inventory createInventory(InventoryHolder holder, InventoryType inventoryType, int size) {
        CraftInventoryCustom inventory;
        if (size > 0) {
            inventory = new CraftInventoryCustom(holder,size);
        } else {
            inventory = new CraftInventoryCustom(holder,inventoryType);
        }
        return inventory;
    }

    @Override
    public void openMenu(Player player, RenderedMenu renderedMenu, int id) {
        var craftInv = (CraftInventoryCustom)renderedMenu.getInventory();

        var type = CraftContainer.getNotchInventoryType(craftInv);
        if (type == null) {
            return;
        }
        var entityPlayer = entityPlayer(player);
        var packet = new ClientboundOpenScreenPacket(id,type,
                CraftChatMessage.fromJSONOrString(renderedMenu.getTitle()));

        entityPlayer.connection.connection.send(packet);
    }

    @Override
    public int openNewMenu(Player player, RenderedMenu renderedMenu) {
        var craftInv = (CraftInventoryCustom)renderedMenu.getInventory();

        var type = CraftContainer.getNotchInventoryType(craftInv);
        if (type == null) {
            return -1;
        }
        var entityPlayer = entityPlayer(player);
        var craftContainer = new CraftContainer(craftInv,entityPlayer,entityPlayer.nextContainerCounter());
        var container = CraftEventFactory.callInventoryOpenEvent(entityPlayer,craftContainer);

        var packet = new ClientboundOpenScreenPacket(container.containerId,type,
                CraftChatMessage.fromJSONOrString(renderedMenu.getTitle()));

        entityPlayer.connection.connection.send(packet);

        return container.containerId;
    }

    private ServerPlayer entityPlayer(Player player) {
        return ((CraftPlayer)player).getHandle();
    }
}
