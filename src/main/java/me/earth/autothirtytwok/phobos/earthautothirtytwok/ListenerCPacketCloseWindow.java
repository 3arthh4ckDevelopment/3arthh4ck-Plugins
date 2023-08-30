package me.earth.autothirtytwok.phobos.earthautothirtytwok;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketCloseWindow;

final class ListenerCPacketCloseWindow extends ModuleListener<EarthAuto32k, PacketEvent.Send<CPacketCloseWindow>> {

    public ListenerCPacketCloseWindow(EarthAuto32k module) {
        super(module, PacketEvent.Send.class, CPacketCloseWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketCloseWindow> event) {
        module.onCPacketCloseWindow(event);
    }

}
