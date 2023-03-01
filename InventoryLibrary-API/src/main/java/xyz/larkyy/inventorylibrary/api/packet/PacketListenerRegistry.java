package xyz.larkyy.inventorylibrary.api.packet;

import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedPacket;

import java.util.HashSet;
import java.util.function.Consumer;

public class PacketListenerRegistry<T extends WrappedPacket> {

    private final HashSet<Consumer<T>> consumers = new HashSet<>();


    public void call(T packet) {
        consumers.forEach(c -> {
            c.accept(packet);
        });
    }

    public void register(Consumer<T> consumer) {
        if (consumers.contains(consumer)) {
            return;
        }
        consumers.add(consumer);
    }

    public void unregister(Consumer<T> consumer) {
        consumers.remove(consumer);
    }

}
