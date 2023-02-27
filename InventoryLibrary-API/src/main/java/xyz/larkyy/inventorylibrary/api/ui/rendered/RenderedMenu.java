package xyz.larkyy.inventorylibrary.api.ui.rendered;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.history.IReopenable;

import java.util.*;

public class RenderedMenu implements InventoryHolder, IReopenable, Cloneable {

    private final Inventory inventory;
    private final int size;
    private final InventoryType inventoryType;

    /*
        A cache of players that have already opened the menu.
        The map contains the UUID of player and ContainerID.

        It is being used when the open method is being called.
        When the player has a cached container id, it uses the id
        and when there is no cache a new container id is being created.
     */
    private final Map<UUID,Integer> cachedPlayers = new HashMap<>();

    /*
        A cache of all actual viewers.
        Clean viewers is being used to check if the viewers still have
        the container opened. If not, it removes particular users from
        the cache.
     */
    private final List<UUID> viewers = new ArrayList<>();
    private String title;

    public RenderedMenu(int size, String title) {
        this(InventoryType.CHEST,size,title);
    }

    public RenderedMenu(InventoryType type, String title) {
        this(type,-1,title);
    }

    public RenderedMenu(InventoryType type, int size, String title) {
        inventory = InventoryHandler.getInstance().getRenderHandler().createInventory(this,type,size);
        this.title = title;
        this.size = size;
        this.inventoryType = type;
    }

    private RenderedMenu(InventoryType type, int size, String title, boolean test){
        inventory = InventoryHandler.getInstance().getRenderHandler().createInventory(this,type,size);
        this.title = title;
        this.size = size;
        this.inventoryType = type;
    }

    public void open(Player player) {
        if (cachedPlayers.containsKey(player.getUniqueId())) {
            int id = cachedPlayers.get(player.getUniqueId());
            InventoryHandler.getInstance().getRenderHandler().openMenu(player,this,id);
        } else {
            int id = InventoryHandler.getInstance().getRenderHandler().openNewMenu(player,this);
            cachedPlayers.put(player.getUniqueId(),id);
        }
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public RenderedMenu getHistoryMenu() {
        return this;
    }

    @Override
    public RenderedMenu clone() {
        RenderedMenu menu = new RenderedMenu(inventoryType,size,title);
        return menu;
    }
}
