package xyz.larkyy.inventorylibrary.api;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.larkyy.inventorylibrary.nms.nms1_19_2.ItemHandler;
import xyz.larkyy.inventorylibrary.nms.nms1_19_2.RenderHandler;

public class InventoryHandler {

    private static InventoryHandler instance;

    private final JavaPlugin plugin;
    private final IRenderHandler renderHandler;
    private final IItemHandler itemHandler;

    public static InventoryHandler init(JavaPlugin plugin) {
        if (instance != null) {
            return instance;
        }
        instance = new InventoryHandler(plugin);
        return instance;
    }

    private InventoryHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        renderHandler = new RenderHandler();
        itemHandler = new ItemHandler();
    }

    public static InventoryHandler getInstance() {
        return instance;
    }

    public IRenderHandler getRenderHandler() {
        return renderHandler;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
