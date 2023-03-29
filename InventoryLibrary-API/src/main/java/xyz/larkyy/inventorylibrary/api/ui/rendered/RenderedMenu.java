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
import xyz.larkyy.inventorylibrary.api.ui.SlotSelection;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlag;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlags;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedComponent;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedPlayerItem;

import javax.annotation.Nonnull;
import java.util.*;

public class RenderedMenu implements InventoryHolder {

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

    public List<RenderedComponent> getComponents(int slot) {
        List<RenderedComponent> list = new ArrayList<>();
        for (var component : components) {
            if (component.getSlotSelection().slots().contains(slot)) {
                list.add(component);
            }
        }
        return list;
    }

    public void clearComponentsSlot(int slot) {
        for (var component : components) {
            var selection = component.getSlotSelection();
            if (selection.slots().contains(slot)) {
                selection.slots().remove(slot);
            }
        }
    }

    public InventoryFlags getFlags() {
        return flags;
    }

    public void open(Player player) {
        this.open(player, false);
    }

    public void open(Player player, boolean ignoreHistory) {
        var previousMenu = InventoryHandler.getInstance().getOpenedMenu(player);
        if (!ignoreHistory) {
            var history = historyHandler().getOrCreate(player);
            if (flags.contains(InventoryFlag.CLEAR_HISTORY_ON_OPEN)) {
                history.clear();
            } else if (history != null) {
                history.add(previousMenu);
            }
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
        runSyncTask(() -> {
            InventoryPlayer inventoryPlayer;
            if (cachedPlayers.containsKey(player.getUniqueId())) {
                inventoryPlayer = cachedPlayers.get(player.getUniqueId());
                int id = inventoryPlayer.getInventoryId();
                var playerComponents = loadPlayerComponents(player);
                inventoryPlayer.setComponents(playerComponents);
                runSyncTask(() -> handleUpdateContent(player));
                InventoryHandler.getInstance().getRenderHandler().openMenu(player,RenderedMenu.this,id);
            } else {
                int id = InventoryHandler.getInstance().getRenderHandler().openNewMenu(player,RenderedMenu.this);
                InventoryHandler.getInstance().addCachedInventoryId(player.getUniqueId(),id);
                inventoryPlayer = new InventoryPlayer(player.getUniqueId(),id,new ArrayList<>());
                cachedPlayers.put(player.getUniqueId(),inventoryPlayer);
                var playerComponents = loadPlayerComponents(player);
                inventoryPlayer.setComponents(playerComponents);
                runSyncTask(() -> handleUpdateContent(player));
            }
        });
    }

    private List<RenderedComponent> loadPlayerComponents(Player player) {
        var playerItems = InventoryHandler.getInstance().getRenderHandler().getPlayerInventoryContent(player);
        List<RenderedComponent> playerComponents = new ArrayList<>();
        if (!flags.contains(InventoryFlag.CLEAR_PLAYERS_INVENTORY)) {
            int slot = -9;
            for (var item : playerItems) {
                if (slot < 0) {
                    slot++;
                    continue;
                }
                if (item.getType() == Material.AIR) {
                    slot++;
                    continue;
                }
                var component = new RenderedPlayerItem(item, (e) -> {
                    if (getFlags().contains(InventoryFlag.CANCEL_PLAYERS_ITEM_INTERACTION)) {
                        e.setCancelled(true);
                    }
                },
                        new SlotSelection(Arrays.asList(slot+getInventory().getContents().length)));
                playerComponents.add(component);
                slot++;
            }
        }
        return playerComponents;
    }

    public void updateInventoryContent(Player player) {
        handleUpdateContent(player);
    }

    private void handleUpdateContent(@Nonnull Player player) {
        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            return;
        }

        var inventoryPlayer = cachedPlayers.get(player.getUniqueId());

        // Top inventory slots
        int topSlotAmount = getInventory().getContents().length;
        // Bottom inventory slots + offhand
        int bottomSlotAmount = 37;

        final List<ItemStack> topItems = new ArrayList<>();

        for (int i = 0; i < topSlotAmount; i++) {
            topItems.add(new ItemStack(Material.AIR));
        }

        List<ItemStack> bottomItems = new ArrayList<>();

        for (int i = 0; i < bottomSlotAmount + 9; i++) {
            bottomItems.add(new ItemStack(Material.AIR));
        }

        List<RenderedComponent> allComponents = new ArrayList<>(inventoryPlayer.getComponents());
        allComponents.addAll(components);


        for (var component : allComponents) {
            if (!component.isVisible(player)) {
                continue;
            }
            for (Integer slot : component.getSlotSelection().slots()) {
                if (slot > getTotalSlots()) {
                    continue;
                }
                var itemStack = component.getItemStack();
                if (itemStack == null) {
                    continue;
                }

                if (slot < topSlotAmount) {
                    topItems.set(slot,itemStack);
                } else {
                    bottomItems.set(slot - topSlotAmount + 9, itemStack);
                }
            }
        }

        // Setting the top inventory content
        InventoryHandler.getInstance().getRenderHandler().setWindowContent(
                player,
                inventoryPlayer.getInventoryId(),
                topItems
        );
        // Setting the bottom inventory content (containerId 0 = Player's inventory)
        InventoryHandler.getInstance().getRenderHandler().setWindowContent(
                player,
                0,
                bottomItems
        );
    }

    /*
        Refreshes an area of slots to what should be rendered from components.
        This method is mainly used for the CustomInventoryClickEvent. So when the
        event is cancelled, it refreshes slots that were modified by player.
     */

    public void refreshSlot(Player player, int slot) {
        ItemStack itemStack = null;

        var invPlayer = cachedPlayers.get(player.getUniqueId());

        List<RenderedComponent> allComponents = new ArrayList<>(invPlayer.getComponents());
        allComponents.addAll(components);

        for (var component : allComponents) {
            if (!component.isVisible(player)) {
                continue;
            }
            if (!component.getSlotSelection().slots().contains(slot)) {
                continue;
            }
            if (component.getItemStack() == null) {
                continue;
            }
            itemStack = component.getItemStack();
        }
        if (itemStack == null) {
            itemStack = new ItemStack(Material.AIR);
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

    public boolean interact(CustomInventoryClickEvent event) {
        if (!cachedPlayers.containsKey(event.getPlayer().getUniqueId())) {
            return true;
        }

        var invPlayer = cachedPlayers.get(event.getPlayer().getUniqueId());
        List<RenderedComponent> allComponents = new ArrayList<>(invPlayer.getComponents());
        allComponents.addAll(components);

        for (var component : allComponents) {
            if (component.interact(event)) {
                event.getClickComponents().add(component);
            }
        }

        if (event.isCancelled()) {
            for (int i : event.getChangedSlots().keySet()) {
                refreshSlot(event.getPlayer(), i);
            }
            setCursorItem(event.getPlayer(),invPlayer.getCarriedItem());
        } else {
            invPlayer.setCarriedItem(event.getCarriedItem());
        }
        return true;
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public void handleClose(Player player) {
        var invHandler = InventoryHandler.getInstance();
        invHandler.getRenderHandler().setWindowContent(player,0,
                invHandler.getRenderHandler().getPlayerInventoryContent(player));
        if (flags.contains(InventoryFlag.CLEAR_HISTORY_ON_CLOSE)) {
            historyHandler().removeHistory(player);
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

    private HistoryHandler historyHandler() {
        return InventoryHandler.getInstance().getHistoryHandler();
    }

    public int getTotalSlots() {
        int topSlotAmount = getInventory().getContents().length;

        // Player's inventory
        return topSlotAmount + 36;
    }
}
