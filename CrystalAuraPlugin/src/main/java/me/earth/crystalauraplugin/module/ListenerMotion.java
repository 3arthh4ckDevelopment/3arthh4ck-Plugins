package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.Rotate;
import me.earth.crystalauraplugin.module.modes.ThreadMode;
import me.earth.crystalauraplugin.module.util.PlayerUtil;
import me.earth.crystalauraplugin.module.util.ThreadUtil;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.cevbreaker.CrystalBomber;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.network.packet.Packet;

final class ListenerMotion
        extends ModuleListener<CrystalAura, MotionUpdateEvent> {
    private static final ModuleCache<CrystalBomber> BOMBER = Caches.getModule(CrystalBomber.class);

    public ListenerMotion(CrystalAura module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (this.module.isPingBypass()) {
            return;
        }
        Calculation calc = this.module.getCurrentCalc();
        if (event.getStage() == Stage.PRE) {
            if (this.module.flooder.getValue().booleanValue()) {
                if (this.module.floodService == null && InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL) && EntityUtil.getClosestEnemy() != null && PlayerUtil.isInHole(EntityUtil.getClosestEnemy()) && PlayerUtil.isFeetPlaceable(EntityUtil.getClosestEnemy(), true, false, this.module.placeRange.getValue().floatValue()) && !BOMBER.isEnabled()) {
                    Earthhack.getLogger().info("help me");
                    this.module.currentRunnable = new FloodThread(PlayerUtil.getFeetPos(EntityUtil.getClosestEnemy(), true, false, this.module.placeRange.getValue().floatValue()), this.module);
                    this.module.floodService = ThreadUtil.keepRunning(this.module.currentRunnable);
                    this.module.shouldPlace = false;
                } else if (this.module.floodService != null && (this.module.floodService.isTerminated() || this.module.floodService.isShutdown())) {
                    this.module.floodService = null;
                    this.module.shouldPlace = true;
                } else if (this.module.floodService != null && this.module.currentRunnable != null && (ListenerMotion.mc.player.squaredDistanceTo(this.module.currentRunnable.getTarget()) >= (double) MathUtil.square(this.module.targetRange.getValue().floatValue()) || ListenerMotion.mc.player.squaredDistanceTo(this.module.currentRunnable.getPos().toCenterPos()) >= (double) MathUtil.square(this.module.placeRange.getValue().floatValue()) || !BlockUtil.canPlaceCrystalFuture(this.module.currentRunnable.getPos(), true, false) || !PlayerUtil.isFootPlace(this.module.currentRunnable.getPos(), this.module.currentRunnable.getTarget(), true, false, this.module.placeRange.getValue().floatValue()) || DamageUtil.calculate(this.module.currentRunnable.getPos()) >= this.module.maxSelfP.getValue().floatValue() || !InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL))) {
                    this.module.floodService.shutdownNow();
                    this.module.floodService = null;
                    this.module.shouldPlace = true;
                    this.module.currentRunnable = null;
                    this.module.shouldStop = true;
                }
            } else {
                if (this.module.floodService != null && (this.module.floodService.isTerminated() || this.module.floodService.isShutdown())) {
                    this.module.floodService = null;
                    this.module.shouldPlace = true;
                }
                if (this.module.floodService != null) {
                    this.module.floodService.shutdown();
                    this.module.floodService = null;
                    this.module.shouldPlace = true;
                }
            }
            if (InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL)) {
                this.module.setSwitching(false);
            }
            if (this.module.rotate.getValue() != Rotate.None && this.module.stay.getValue().booleanValue() && this.module.rotations != null && !Managers.ROTATION.isBlocking() && !this.module.getBreakTimer().passed(600L) && Managers.SWITCH.getLastSwitch() > (long) this.module.cooldown.getValue().intValue()) {
                event.setYaw(this.module.rotations[0]);
                event.setPitch(this.module.rotations[1]);
            }
            if (!this.module.multiThread.getValue().booleanValue()) {
                calc = new Calculation(this.module, Managers.ENTITIES.getPlayers(), Managers.ENTITIES.getEntities());
                this.module.setCurrentCalc(calc);
                calc.run();
            }
            if (calc != null && calc.isRotating()) {
                this.module.rotations = calc.getRotations();
                event.setYaw(this.module.rotations[0]);
                event.setPitch(this.module.rotations[1]);
            }
            this.module.runNonRotateThread(ThreadMode.Pre);
        } else {
            if (calc != null) {
                this.module.setRenderPos(calc.getPos());
                if (calc.getTarget() == null && this.module.targetTimer.passed(250L)) {
                    this.module.setTarget(calc.getTarget());
                }
                for (Packet<?> packet : calc.getPackets()) {
                    if (packet instanceof CPacketPlayerTryUseItemOnBlock) {
                        InventoryUtil.syncItem();
                    }
                    ListenerMotion.mc.player.networkHandler.sendPacket(packet);
                }
                if (calc.isAttacking()) {
                    this.module.swing();
                }
            }
            this.module.runNonRotateThread(ThreadMode.Post);
            this.module.setCurrentCalc(null);
            if (!InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL) || this.module.renderTimer.passed(300L)) {
                this.module.setRenderPos(null);
            }
            if (this.module.postRunnable != null) {
                this.module.postRunnable.run();
                this.module.postRunnable = null;
            }
        }
    }
}

