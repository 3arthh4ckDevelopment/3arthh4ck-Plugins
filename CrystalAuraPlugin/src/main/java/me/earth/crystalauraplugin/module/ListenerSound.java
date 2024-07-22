package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

final class ListenerSound
        extends ModuleListener<CrystalAura, PacketEvent.Receive<PlaySoundS2CPacket>> {
    public ListenerSound(CrystalAura module) {
        super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlaySoundS2CPacket> event) {
        PlaySoundS2CPacket packet = event.getPacket();
        if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            BlockPos pos = new BlockPos((int) packet.getX(), (int) packet.getY(), (int) packet.getZ());
            if (this.module.getPositions().remove(pos) && !this.module.isPingBypass()) {
                this.module.confirmed = true;
            }
            if (this.module.soundR.getValue().booleanValue()) {
                this.killEntities(pos);
            }
        }
    }

    private void killEntities(BlockPos pos) {
        mc.execute(() -> {
            for (Entity entity : Managers.ENTITIES.getEntities()) {
                if (!(entity instanceof EndCrystalEntity) || !(entity.squaredDistanceTo(pos.toCenterPos()) <= 36.0)) continue;
                this.module.getPositions().remove(PositionUtil.getPosition(entity));
                entity.remove(Entity.RemovalReason.KILLED);
            }
        });
    }
}

