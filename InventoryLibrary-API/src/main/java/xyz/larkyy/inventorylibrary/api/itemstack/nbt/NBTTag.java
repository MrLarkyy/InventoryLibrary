package xyz.larkyy.inventorylibrary.api.itemstack.nbt;

public record NBTTag<T>(T value) {

    public Class<T> getValueClass() {
        return (Class<T>) value.getClass();
    }
}
