package xyz.larkyy.inventorylibrary.nms.nms1_19_2;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.IItemHandler;
import xyz.larkyy.inventorylibrary.api.itemstack.CustomItem;
import xyz.larkyy.inventorylibrary.api.itemstack.nbt.NBTCompound;

import java.util.UUID;

public class ItemHandler implements IItemHandler {

    public org.bukkit.inventory.ItemStack buildItemStack(CustomItem customItem) {
        var item = CraftMagicNumbers.getItem(customItem.getMaterial());
        if (item == null) {
            return new ItemStack(Material.AIR);
        }
        var stack = new net.minecraft.world.item.ItemStack(item,customItem.getAmount());
        handleNBT(stack,customItem.getNbtCompound());
        var meta = CraftItemStack.getItemMeta(stack);
        meta.setLore(customItem.getLore());
        meta.setDisplayName(customItem.getDisplayName());
        CraftItemStack.setItemMeta(stack,meta);
        stack.setCount(customItem.getAmount());
        return CraftItemStack.asBukkitCopy(stack);
    }

    @Override
    public ItemStack updateItemStack(ItemStack itemStack, CustomItem customItem) {
        var stack = CraftItemStack.asNMSCopy(itemStack);
        var meta = CraftItemStack.getItemMeta(stack);
        meta.setLore(customItem.getLore());
        meta.setDisplayName(customItem.getDisplayName());
        CraftItemStack.setItemMeta(stack,meta);
        stack.setCount(customItem.getAmount());
        return CraftItemStack.asBukkitCopy(stack);

    }

    private void handleNBT(net.minecraft.world.item.ItemStack itemStack, NBTCompound nbtCompound) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        for (var entry : nbtCompound.getTags().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            var clazz = value.getValueClass();
            if (int.class.isInstance(clazz)) {
                compoundTag.putInt(key, (int) value.value());
            } else if (byte.class.isInstance(clazz)) {
                compoundTag.putByte(key, (byte) value.value());
            } else if (short.class.isInstance(clazz)) {
                compoundTag.putShort(key, (short) value.value());
            } else if (long.class.isInstance(clazz)) {
                compoundTag.putLong(key, (long) value.value());
            } else if (float.class.isInstance(clazz)) {
                compoundTag.putFloat(key, (float) value.value());
            } else if (double.class.isInstance(clazz)) {
                compoundTag.putDouble(key, (double) value.value());
            } else if (clazz == String.class) {
                compoundTag.putString(key, value.value().toString());
            } else if (clazz == UUID.class) {
                compoundTag.putUUID(key, (UUID) value.value());
            } else if (clazz == byte[].class) {
                compoundTag.putByteArray(key, (byte[]) value.value());
            } else if (clazz == int[].class) {
                compoundTag.putIntArray(key, (int[]) value.value());
            } else if (clazz == long[].class) {
                compoundTag.putLongArray(key, (long[]) value.value());
            } else if (boolean.class.isInstance(clazz)) {
                compoundTag.putBoolean(key, (boolean) value.value());
            }
        }
        itemStack.setTag(compoundTag);
    }


}
