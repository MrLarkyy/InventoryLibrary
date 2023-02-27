package xyz.larkyy.inventorylibrary.api.itemstack.nbt;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class NBTCompound {

    private final Map<String,NBTTag<?>> tags;

    public NBTCompound() {
        tags = new HashMap<>();
    }

    public NBTCompound(Map<String,NBTTag<?>> tags) {
        this.tags = tags;
    }

    public void put(String key, NBTTag<?> tag) {
        tags.put(key,tag);
    }

    public Map<String, NBTTag<?>> getTags() {
        return tags;
    }

    public @Nullable NBTTag<?> getTag(String key) {
        return tags.get(key);
    }

    public void removeTag(String key) {
        tags.remove(key);
    }

}
