package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

final class ListenerPlace
        extends ModuleListener<CrystalAura, PacketEvent.Post<PlayerInteractBlockC2SPacket>> {
    public ListenerPlace(CrystalAura module) {
        super(module, PacketEvent.Post.class, PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event) {
        if (this.module.predict.getValue().booleanValue() && !this.module.isPingBypass() && ListenerPlace.mc.player.getStackInHand((event.getPacket()).getHand()).getItem() == net.minecraft.item.Items.END_CRYSTAL) {
            mc.execute(() -> {
                int id = this.getID();
                if (id != -1 && this.module.getBreakTimer().passed(this.module.breakDelay.getValue().intValue())) {
                    IPlayerInteractEntityC2S useEntity = (IPlayerInteractEntityC2S) new PlayerInteractEntityC2SPacket();
                    useEntity.setAction(PlayerInteractEntityC2SPacket.InteractType.ATTACK);
                    useEntity.setEntityId(id);
                    ListenerPlace.mc.player.networkHandler.sendPacket((Packet) useEntity);
                    ListenerPlace.mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    this.module.getBreakTimer().reset(this.module.breakDelay.getValue().intValue());
                }
            });
        }
    }

    private int getID() {
        if (ListenerPlace.mc.world == null || ListenerPlace.mc.player == null) {
            return -1;
        }
        for (PlayerEntity player : Managers.ENTITIES.getPlayers()) {
            if (player == null || player.isAlive() && !InventoryUtil.isHolding(player, net.minecraft.item.Items.BOW) && !InventoryUtil.isHolding(player, net.minecraft.item.Items.EXPERIENCE_BOTTLE))
                continue;
            return -1;
        }
        int highest = -1;
        for (Entity entity : Managers.ENTITIES.getEntities()) {
            if (entity == null || entity.getId() <= highest) continue;
            highest = entity.getId();
        }
        return highest + 1;
    }
}

