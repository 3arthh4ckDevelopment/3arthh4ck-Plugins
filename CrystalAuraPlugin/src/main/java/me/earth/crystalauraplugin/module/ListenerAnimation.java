package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerAnimation
        extends ModuleListener<CrystalAura, PacketEvent.Receive<SPacketAnimation>> {
    public ListenerAnimation(CrystalAura module) {
        super(module, PacketEvent.Receive.class, SPacketAnimation.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketAnimation> event) {
        SPacketAnimation packet;
        if (this.module.noParticles.getValue().booleanValue() && (packet = event.getPacket()).getAnimationType() == 5 && (ListenerAnimation.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe || ListenerAnimation.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && ListenerAnimation.mc.player.getOffHandStack().getItem() instanceof ItemEndCrystal && !this.module.getBreakTimer().passed(500L)) {
            event.setCancelled(true);
        }
    }
}

