package xyz.larkyy.inventorylibrary.api;

import xyz.larkyy.inventorylibrary.api.packet.PlayerPacketInjector;

public interface NMSHandler {

    IRenderHandler getRenderHandler();
    IItemHandler getItemHandler();
    PlayerPacketInjector getPlayerPacketInjector();

}
