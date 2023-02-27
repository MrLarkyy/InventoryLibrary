package xyz.larkyy.inventorylibrary.api;

import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;

public interface IItemHandler {
    org.bukkit.inventory.ItemStack buildItemStack(CustomItem customItem);
    org.bukkit.inventory.ItemStack updateItemStack(ItemStack itemStack, CustomItem customItem);
}
