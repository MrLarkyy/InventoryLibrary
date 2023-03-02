package xyz.larkyy.inventorylibrary;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.nms.nms1_19_2.NMSHandlerImpl;

public final class InventoryLibrary extends JavaPlugin {

    private InventoryHandler inventoryHandler;

    @Override
    public void onEnable() {
        inventoryHandler = InventoryHandler.init(this, new NMSHandlerImpl());

    }

    @Override
    public void onDisable() {
        inventoryHandler.closeAll();
        inventoryHandler.onDisable();
    }
}
