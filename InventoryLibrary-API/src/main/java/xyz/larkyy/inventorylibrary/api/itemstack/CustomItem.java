package xyz.larkyy.inventorylibrary.api.itemstack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.larkyy.inventorylibrary.api.InventoryHandler;
import xyz.larkyy.inventorylibrary.api.itemstack.nbt.NBTCompound;
import xyz.larkyy.inventorylibrary.api.itemstack.nbt.NBTTag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomItem implements Cloneable {

    private Material material;
    int amount = 1;
    private String displayName = "";
    private List<String> lore = new ArrayList<>();
    private NBTCompound nbtCompound = new NBTCompound();

    public CustomItem(@Nonnull Material material) {
        this.material = material;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setNbtCompound(NBTCompound nbtCompound) {
        this.nbtCompound = nbtCompound;
    }

    public NBTCompound getNbtCompound() {
        return nbtCompound;
    }

    public void addNbt(String key, NBTTag<?> tag) {
        nbtCompound.put(key,tag);
    }

    public void removeNbt(String key) {
        nbtCompound.removeTag(key);
    }

    public void addLoreLines(List<String> lines) {
        lore.addAll(lines);
    }

    public void addLoreLine(String line) {
        lore.add(line);
    }

    public ItemStack toItemStack() {
        return InventoryHandler.getInstance().getItemHandler().buildItemStack(this);
    }

    @Override
    public CustomItem clone() {
        var item = new CustomItem(material);
        item.amount = amount;
        item.lore = lore;
        item.nbtCompound = new NBTCompound(new HashMap<>(nbtCompound.getTags()));
        return item;
    }

    public static CustomItemBuilder builder(Material material) {
        return new CustomItemBuilder(material);
    }

    public static class CustomItemBuilder {

        private final CustomItem customItem;

        public CustomItemBuilder(Material material) {
            this.customItem = new CustomItem(material);
        }

        public CustomItemBuilder setDisplayName(String name) {
            customItem.setDisplayName(name);
            return this;
        }

        public CustomItemBuilder setLore(List<String> lore) {
            customItem.setLore(lore);
            return this;
        }
        public CustomItemBuilder setLore(String... lore) {
            customItem.setLore(List.of(lore));
            return this;
        }
        public CustomItemBuilder addLoreLine(String line) {
            customItem.addLoreLine(line);
            return this;
        }
        public CustomItemBuilder addLoreLines(List<String> lines) {
            customItem.addLoreLines(lines);
            return this;
        }
        public CustomItemBuilder addLoreLines(String... lines) {
            customItem.addLoreLines(List.of(lines));
            return this;
        }
        public CustomItemBuilder addNbt(String key, NBTTag<?> nbtTag) {
            customItem.addNbt(key,nbtTag);
            return this;
        }

        public CustomItemBuilder setAmount(int amount) {
            customItem.setAmount(amount);
            return this;
        }

        public CustomItem build() {
            return customItem;
        }
    }
}
