package xyz.larkyy.inventorylibrary.api.packet;

import org.bukkit.entity.Player;

public interface PlayerPacketInjector {

    void inject(Player player);
    void eject(Player player);

}
