package me.earth.autothirtytwok.phobos.earthautothirtytwok;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacketPlayer extends CPacketPlayerListener {

    private final EarthAuto32k module;

    public ListenerCPacketPlayer(EarthAuto32k module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        module.onCPacketPlayer(event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        module.onCPacketPlayer(event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        module.onCPacketPlayer(event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        module.onCPacketPlayer(event.getPacket());
    }

}
