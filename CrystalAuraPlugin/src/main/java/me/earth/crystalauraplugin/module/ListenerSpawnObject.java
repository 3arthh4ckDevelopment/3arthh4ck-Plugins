package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.Rotate;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerSpawnObject
        extends ModuleListener<CrystalAura, PacketEvent.Receive<EntitySpawnS2CPacket>> {
    public ListenerSpawnObject(CrystalAura module) {
        super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event) {
        if (this.module.instant.getValue().booleanValue() && !this.module.isPingBypass()) {
            EntitySpawnS2CPacket packet = event.getPacket();
            PlayerEntity target = this.module.getTarget();
            if (packet.getEntityData() == 51 && target != null && !EntityUtil.isDead(target) && ListenerSpawnObject.mc.player != null) {
                float damage;
                BlockPos pos = new BlockPos((int) packet.getX(), (int) packet.getY(), (int) packet.getZ());
                if (this.module.getPositions().contains(pos) && this.isValid(pos) && this.rotationCheck(pos) && (damage = DamageUtil.calculate(pos.down())) <= this.module.maxSelfB.getValue().floatValue() && (double) damage < (double) EntityUtil.getHealth(ListenerSpawnObject.mc.player) + 1.0) {
                    this.attack(packet, this.module.slow.remove(pos) ? this.module.slowDelay.getValue().intValue() : this.module.breakDelay.getValue().intValue());
                }
            }
        }
    }

    private void attack(EntitySpawnS2CPacket packetIn, int delay) {
        if (this.module.getBreakTimer().passed(delay)) {
            IPlayerInteractEntityC2SPacket useEntity = (IPlayerInteractEntityC2SPacket) new PlayerInteractEntityC2SPacket();
            useEntity.setAction(PlayerInteractEntityC2SPacket.Action.ATTACK);
            useEntity.setEntityId(packetIn.getId());
            ListenerSpawnObject.mc.player.networkHandler.sendPacket((Packet) useEntity);
            ListenerSpawnObject.mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            mc.execute(((CrystalAura) this.module)::swing);
            this.module.getBreakTimer().reset(delay);
        }
    }

    private boolean isValid(BlockPos pos) {
        if (ListenerSpawnObject.mc.player.squaredDistanceTo((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5) > (double) MathUtil.square(this.module.breakRange.getValue().floatValue())) {
            return false;
        }
        if (ListenerSpawnObject.mc.player.squaredDistanceTo((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5) > (double) MathUtil.square(this.module.breakTrace.getValue().floatValue())) {
            return RayTraceUtil.canBeSeen(new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + 1.700000047683716, (double) pos.getZ() + 0.5), ListenerSpawnObject.mc.player);
        }
        return true;
    }

    private boolean rotationCheck(BlockPos pos) {
        return this.module.rotate.getValue().noRotate(Rotate.Break) || RotationUtil.isLegit(pos) || RotationUtil.isLegit(pos.up());
    }
}

