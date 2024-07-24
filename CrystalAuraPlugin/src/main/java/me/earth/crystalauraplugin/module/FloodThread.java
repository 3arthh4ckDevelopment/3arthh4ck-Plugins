package me.earth.crystalauraplugin.module;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class FloodThread
        implements Runnable,
        Globals {
    private final CrystalAura module;
    private final PlayerEntity target;
    private BlockPos pos;
    private long last;

    public FloodThread(BlockPos pos, CrystalAura module) {
        this.pos = pos;
        this.module = module;
        this.target = EntityUtil.getClosestEnemy();
        this.last = System.currentTimeMillis();
        Earthhack.getLogger().info("wowza");
        module.shouldPlace = false;
    }

    @Override
    public void run() {
        while (true) {
            HandSwingC2SPacket animation = new HandSwingC2SPacket(this.getHand());
            BlockHitResult hit = new BlockHitResult(new Vec3d(0.5f, 1.0f, 0.5f), Direction.UP, this.pos, false);
            NetworkUtil.sendSequenced(sequence -> new PlayerInteractBlockC2SPacket(this.getHand(), hit, sequence));
            FloodThread.mc.player.networkHandler.sendPacket(animation);
            this.last = System.currentTimeMillis();
            if (!this.module.isEnabled() || this.target == null || this.module.shouldStop) {
                this.module.shouldPlace = true;
                this.module.floodService.shutdownNow();
                Earthhack.getLogger().info("unfortunate");
                this.module.floodService = null;
                this.module.currentRunnable = null;
                this.module.shouldStop = false;
                return;
            }
            try {
                Thread.sleep(this.module.floodDelay.getValue().intValue(), this.module.floodDelayNs.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Hand getHand() {
        return FloodThread.mc.player.getOffHandStack().getItem() == net.minecraft.item.Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public PlayerEntity getTarget() {
        return this.target;
    }
}

