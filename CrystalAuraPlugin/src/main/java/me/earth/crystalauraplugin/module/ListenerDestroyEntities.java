package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities
        extends ModuleListener<CrystalAura, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerDestroyEntities(CrystalAura module) {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        mc.execute(() -> {
            if (ListenerDestroyEntities.mc.world != null && !this.module.isPingBypass()) {
                SPacketDestroyEntities packet = event.getPacket();
                for (int id : packet.getEntityIDs()) {
                    this.module.killed.remove(id);
                    this.module.attacked.remove(id);
                    Entity entity = ListenerDestroyEntities.mc.world.getEntityByID(id);
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.module.getPositions().remove(PositionUtil.getPosition(entity));
                }
            }
        });
    }
}

