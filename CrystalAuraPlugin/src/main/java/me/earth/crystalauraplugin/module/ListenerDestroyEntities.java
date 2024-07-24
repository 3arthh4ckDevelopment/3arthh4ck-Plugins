package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

final class ListenerDestroyEntities
        extends ModuleListener<CrystalAura, PacketEvent.Receive<EntitiesDestroyS2CPacket>> {
    public ListenerDestroyEntities(CrystalAura module) {
        super(module, PacketEvent.Receive.class, EntitiesDestroyS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitiesDestroyS2CPacket> event) {
        mc.execute(() -> {
            if (ListenerDestroyEntities.mc.world != null && !this.module.isPingBypass()) {
                EntitiesDestroyS2CPacket packet = event.getPacket();
                for (int id : packet.getEntityIds()) {
                    this.module.killed.remove(id);
                    this.module.attacked.remove(id);
                    Entity entity = Managers.ENTITIES.getEntity(id);
                    if (!(entity instanceof EndCrystalEntity)) continue;
                    this.module.getPositions().remove(PositionUtil.getPosition(entity));
                }
            }
        });
    }
}

