package xyz.larkyy.inventorylibrary.api.ui.rendered;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlag;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlags;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.history.IReopenable;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;
import xyz.larkyy.inventorylibrary.api.ui.template.component.Component;

import javax.annotation.Nonnull;
import java.util.*;

public class RenderedMenu implements InventoryHolder, IReopenable, Cloneable {

    private final Inventory inventory;
    private final int size;
    private final InventoryType inventoryType;
    private final InventoryFlags flags;
    private List<RenderedComponent> components = new ArrayList<>();

    /*
        A cache of players that have already opened the menu.
        The map contains the UUID of player and ContainerID.

        It is being used when the open method is being called.
        When the player has a cached container id, it uses the id
        and when there is no cache a new container id is being created.
     */
    private final Map<UUID,Integer> cachedPlayers = new HashMap<>();

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
        this.flags = new InventoryFlags();
    }

    private RenderedMenu(InventoryType type, int size, String title, InventoryFlags flags){
        inventory = InventoryHandler.getInstance().getRenderHandler().createInventory(this,type,size);
        this.title = title;
        this.size = size;
        this.inventoryType = type;
        this.flags = flags;
    }

    public List<RenderedComponent> getComponents() {
        return components;
    }

    public void setComponents(List<RenderedComponent> components) {
        this.components = components;
    }

    public void addComponent(RenderedComponent component) {
        if (components.contains(component)) {
            return;
        }
        components.add(component);
    }
    public void addComponents(RenderedComponent... components) {
        for (var component : components) {
            addComponent(component);
        }
    }

    public void addComponents(Collection<RenderedComponent> components) {
        for (var component : components) {
            addComponent(component);
        }
    }

    public InventoryFlags getFlags() {
        return flags;
    }

    public void open(Player player) {
        var history = historyHandler().getOrCreate(player);
        if (!flags.contains(InventoryFlag.IGNORE_HISTORY)) {
            history.add(this);
        }
        if (flags.contains(InventoryFlag.CLEAR_HISTORY_ON_OPEN)) {
            history.clear();
        }
        handleOpen(player);
    }

    /*
        Sets the title of the inventory and updates it to all players
        viewing this menu.
     */
    public void setTitle(String title) {
        this.title = title;
        updateTitle();
    }

    private void updateTitle() {
        for (var viewer : inventory.getViewers()) {
            if (viewer instanceof Player player) {
                handleOpen(player);
            }
        }
    }

    private void handleOpen(@Nonnull Player player) {
        runAsyncTask(() -> {
            if (cachedPlayers.containsKey(player.getUniqueId())) {
                int id = cachedPlayers.get(player.getUniqueId());
                InventoryHandler.getInstance().getRenderHandler().openMenu(player,RenderedMenu.this,id);
            } else {
                int id = InventoryHandler.getInstance().getRenderHandler().openNewMenu(player,RenderedMenu.this);
                Bukkit.broadcastMessage("Open id: "+id);
                cachedPlayers.put(player.getUniqueId(),id);
            }
        });
    }

    public void interact(CustomInventoryClickEvent event) {
        for (var component : components) {
            component.interact(event);
        }
    }

    private void runAsyncTask(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(InventoryHandler.getInstance().getPlugin());
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
        RenderedMenu menu = new RenderedMenu(inventoryType,size,title,flags.clone());
        return menu;
    }

    private HistoryHandler historyHandler() {
        return InventoryHandler.getInstance().getHistoryHandler();
    }
}
