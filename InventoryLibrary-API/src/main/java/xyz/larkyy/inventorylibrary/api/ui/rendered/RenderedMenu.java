package xyz.larkyy.inventorylibrary.api.ui.rendered;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlag;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlags;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.history.IReopenable;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;

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
    private final Map<UUID,InventoryPlayer> cachedPlayers = new HashMap<>();

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
                int id = cachedPlayers.get(player.getUniqueId()).getInventoryId();
                InventoryHandler.getInstance().getRenderHandler().openMenu(player,RenderedMenu.this,id);
            } else {
                int id = InventoryHandler.getInstance().getRenderHandler().openNewMenu(player,RenderedMenu.this);
                cachedPlayers.put(player.getUniqueId(),new InventoryPlayer(player.getUniqueId(),id));
            }
            runSyncTask(() -> handleUpdateContent(player));
        });
    }

    private void handleUpdateContent(@Nonnull Player player) {
        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            return;
        }

        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i <= getTotalSlots(); i++) {
            itemStacks.add(new ItemStack(Material.AIR));
        }

        for (var component : components) {
            for (Integer slot : component.getSlotSelection().slots()) {
                if (slot > getTotalSlots()) {
                    continue;
                }
                var itemStack = component.getItemStack();
                if (itemStack == null) {
                    continue;
                }
                if (slot == getTotalSlots()) {
                    handleSetItem(
                            player,
                            itemStack,
                            slot
                            );
                } else {
                    itemStacks.set(slot, itemStack);
                }
            }
        }

        var inventoryPlayer = cachedPlayers.get(player.getUniqueId());
        InventoryHandler.getInstance().getRenderHandler().setWindowContent(
                player,
                inventoryPlayer.getInventoryId(),
                itemStacks
        );
    }

    /*
        Refreshes an area of slots to what should be rendered from components.
        This method is mainly used for the CustomInventoryClickEvent. So when the
        event is cancelled, it refreshes slots that were modified by player.
     */

    public void refreshSlot(Player player, int slot) {
        ItemStack itemStack = null;
        for (var component : components) {
            if (!component.getSlotSelection().slots().contains(slot)) {
                continue;
            }
            if (component.getItemStack() == null) {
                continue;
            }
            itemStack = component.getItemStack();
        }
        if (itemStack == null) {
            return;
        }
        handleSetItem(player,itemStack,slot);
    }

    private void handleSetItem(@Nonnull Player player, @Nonnull ItemStack itemStack, int slot) {
        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        var inventoryPlayer = cachedPlayers.get(player.getUniqueId());

        int topSlotAmount = getInventory().getContents().length;
        int totalSlotAmount = topSlotAmount + 36;

        if (slot == totalSlotAmount) {
            Bukkit.broadcastMessage("Setting offhend");
            InventoryHandler.getInstance().getRenderHandler().setSlot(
                    player,
                    0,
                    45,
                    itemStack
            );
        } else {
            InventoryHandler.getInstance().getRenderHandler().setSlot(
                    player,
                    inventoryPlayer.getInventoryId(),
                    slot,
                    itemStack
            );
        }

    }

    /*
        Updates the item on cursor. This method is mainly used for the
        CustomInventoryClickEvent. So when the event is cancelled,
        it sets the cursor item back to what it should be.
     */
    public void setCursorItem(Player player, ItemStack itemStack) {
        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        var invPlayer = cachedPlayers.get(player.getUniqueId());
        invPlayer.setCarriedItem(itemStack);
        InventoryHandler.getInstance().getRenderHandler().setSlot(player,-1,-1,itemStack);
    }

    public void interact(CustomInventoryClickEvent event) {
        if (!cachedPlayers.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        for (var component : components) {
            component.interact(event);
        }
        var invPlayer = cachedPlayers.get(event.getPlayer().getUniqueId());

        if (event.isCancelled()) {
            for (int i : event.getChangedSlots().keySet()) {
                Bukkit.broadcastMessage("Updating slot");
                refreshSlot(event.getPlayer(), i);
            }
            setCursorItem(event.getPlayer(),invPlayer.getCarriedItem());
        } else {
            invPlayer.setCarriedItem(event.getCarriedItem());
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

    private void runSyncTask(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(InventoryHandler.getInstance().getPlugin());
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

    public int getTotalSlots() {
        int topSlotAmount = getInventory().getContents().length;

        // Player's inventory
        return topSlotAmount + 36;
    }
}
