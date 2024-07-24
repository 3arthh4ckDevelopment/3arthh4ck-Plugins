package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.AxeItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;

final class ListenerAnimation
        extends ModuleListener<CrystalAura, PacketEvent.Receive<EntityAnimationS2CPacket>> {
    public ListenerAnimation(CrystalAura module) {
        super(module, PacketEvent.Receive.class, EntityAnimationS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityAnimationS2CPacket> event) {
        EntityAnimationS2CPacket packet;
        if (this.module.noParticles.getValue().booleanValue() && (packet = event.getPacket()).getAnimationId() == 5 && (ListenerAnimation.mc.player.getMainHandStack().getItem() instanceof AxeItem || ListenerAnimation.mc.player.getMainHandStack().getItem() instanceof SwordItem) && ListenerAnimation.mc.player.getOffHandStack().getItem() instanceof EndCrystalItem && !this.module.getBreakTimer().passed(500L)) {
            event.setCancelled(true);
        }
    }
}

