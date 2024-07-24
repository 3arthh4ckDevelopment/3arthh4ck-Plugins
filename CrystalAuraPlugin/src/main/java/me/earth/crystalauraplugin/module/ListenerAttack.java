package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.util.ThreadUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class ListenerAttack
        extends ModuleListener<CrystalAura, PacketEvent.Post<PlayerInteractEntityC2SPacket>> {
    public ListenerAttack(CrystalAura module) {
        super(module, PacketEvent.Post.class, PlayerInteractEntityC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractEntityC2SPacket> event) {
        if (event.getPacket().getAction() == CPacketUseEntity.Action.ATTACK && !this.module.isPingBypass()) {
            ICPacketUseEntity packet = (ICPacketUseEntity) event.getPacket();
            this.module.attacked.add(packet.getEntityID());
            if (event.getPacket().getEntityFromWorld(ListenerAttack.mc.world) instanceof EndCrystalEntity && this.module.antiFeetPlace.getValue().booleanValue()) {
                BlockPos antiPos = event.getPacket().getEntityFromWorld(ListenerAttack.mc.world).getPosition().down();
                ThreadUtil.run(() -> {
                    CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(antiPos, Direction.UP, this.getHand(), 0.5f, 1.0f, 0.5f);
                    HandSwingC2SPacket animation1 = new HandSwingC2SPacket(this.getHand());
                    ListenerAttack.mc.player.networkHandler.sendPacket(place);
                    ListenerAttack.mc.player.networkHandler.sendPacket(animation1);
                }, (long) (Managers.TICK.getServerTickLengthMS() + 11) - Managers.TICK.getTickTimeAdjusted());
            }
        }
    }

    private Hand getHand() {
        return ListenerAttack.mc.player.getOffHandStack().getItem() == net.minecraft.item.Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}

