package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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


        new CraftInventoryCustom(holder,inventoryType);

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
        var craftContainer = new CraftContainer(craftInv,entityPlayer,id);
        handleOpen(entityPlayer,craftContainer,type,CraftChatMessage.fromJSONOrString(renderedMenu.getTitle()));
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
        return handleOpen(entityPlayer,craftContainer,type,CraftChatMessage.fromJSONOrString(renderedMenu.getTitle()));
    }

    private int handleOpen(ServerPlayer player, AbstractContainerMenu craftContainer, MenuType<?> type, Component title) {

        //var container = CraftEventFactory.callInventoryOpenEvent(player,craftContainer);
        player.containerMenu.transferTo(craftContainer, player.getBukkitEntity());
        var packet = new ClientboundOpenScreenPacket(craftContainer.containerId,type,title);

        player.connection.connection.send(packet);
        player.containerMenu = craftContainer;
        player.initMenu(craftContainer);

        return craftContainer.containerId;
    }

    @Override
    public Inventory getOpenedMenu(Player player) {

        var entityPlayer = entityPlayer(player);
        var containerMenu = entityPlayer.containerMenu;

        return containerMenu.getBukkitView().getTopInventory();
    }

    private ServerPlayer entityPlayer(Player player) {
        return ((CraftPlayer)player).getHandle();
    }
}
