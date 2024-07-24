package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.Attack;
import me.earth.crystalauraplugin.module.modes.AutoSwitch;
import me.earth.crystalauraplugin.module.modes.Rotate;
import me.earth.crystalauraplugin.module.util.*;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.Wrapper;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

final class Calculation
        extends Wrapper<CrystalAura>
        implements Runnable,
        Globals {
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);
    private final List<Packet<?>> packets = new CopyOnWriteArrayList();
    private final List<PlayerEntity> players;
    private final List<Entity> crystals;
    private final HelperBreak breakHelper;
    private final HelperPlace placeHelper;
    private final Random random = new Random();
    private BlockPos pos;
    private PlayerEntity target;
    private Entity crystal;
    private float[] rotations;
    private boolean attacking;
    private BreakData breakData;
    private boolean doneRotating;

    public Calculation(CrystalAura module, List<PlayerEntity> players, List<Entity> crystals) {
        super(module);
        this.players = players;
        this.crystals = crystals;
        this.breakHelper = module.breakHelper;
        this.placeHelper = module.placeHelper;
    }

    @Override
    public void run() {
        if ((this.value.attack.getValue().shouldCalc() || this.value.isSwitching()) && Managers.SWITCH.getLastSwitch() >= (long) this.value.cooldown.getValue().intValue()) {
            float damage;
            PlaceData data;
            boolean flag = false;
            this.value.setSafe = false;
            int count = this.explode();
            if (count != 6 && this.value.place.getValue().booleanValue() && this.value.getPlaceTimer().passed(this.value.placeDelay.getValue().intValue()) && (count < this.value.multiPlace.getValue() || this.value.antiSurr.getValue().booleanValue()) && this.shouldPlaceCalc()) {
                float damage2;
                PlaceData data2 = this.placeHelper.createData(this.players, this.crystals);
                if (this.checkPos(data2.getPos(), count) && ((damage2 = data2.getDamage()) > this.value.minDamage.getValue().floatValue() || EntityUtil.getHealth(data2.getTarget()) <= this.value.facePlace.getValue().floatValue() && (!this.value.noFaceSpam.getValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) || this.value.shouldFacePlace() && (!this.value.noFaceSpam.getValue().booleanValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) && (double) damage2 > 2.0)) {
                    this.target = data2.getTarget();
                    this.place(data2);
                    flag = true;
                }
            } else if (this.value.place.getValue().booleanValue() && this.value.useForPlace.getValue().booleanValue() && !flag && this.shouldPlaceCalc() && this.checkPos((data = this.placeHelper.createData(this.players, this.crystals)).getPos(), count) && ((damage = data.getDamage()) > this.value.minDamage.getValue().floatValue() || EntityUtil.getHealth(data.getTarget()) <= this.value.facePlace.getValue().floatValue() && (!this.value.noFaceSpam.getValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) || this.value.shouldFacePlace() && (!this.value.noFaceSpam.getValue().booleanValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) && (double) damage > 2.0)) {
                this.target = data.getTarget();
                this.updatePlaceRotations(data);
            }
        }
    }

    public boolean isRotating() {
        return this.rotations != null;
    }

    public float[] getRotations() {
        return this.rotations;
    }

    public List<Packet<?>> getPackets() {
        return this.packets;
    }

    public PlayerEntity getTarget() {
        return this.target;
    }

    private int explode() {
        this.breakData = this.breakHelper.createData(this.players, this.crystals);
        this.crystal = this.breakData.getCrystal();
        if (!(!this.attack(this.crystal, this.breakData.getDamage()) || this.value.rotate.getValue().noRotate(Rotate.Place) && this.value.multiTask.getValue().booleanValue())) {
            return 6;
        }
        return this.breakData.getCount();
    }

    private boolean attack(Entity crystal, float damage) {
        block13:
        {
            block18:
            {
                HandSwingC2SPacket animation;
                PlayerInteractEntityC2SPacket useEntity;
                block16:
                {
                    block17:
                    {
                        block15:
                        {
                            block14:
                            {
                                int delay = damage <= this.value.slowBreak.getValue().floatValue() ? this.value.slowDelay.getValue().intValue() : this.value.breakDelay.getValue().intValue();
                                PlayerEntity closest = EntityUtil.getClosestEnemy();
                                if (crystal != null && closest != null && this.value.getTarget() != null && this.value.slowLegBreak.getValue().booleanValue() && PlayerUtil.isValidFootCrystal(crystal, closest) && PlayerUtil.isInHole(closest)) {
                                    delay = this.value.legDelay.getValue();
                                }
                                if (!this.value.explode.getValue().booleanValue() || crystal == null || !this.value.attack.getValue().shouldAttack() || !this.value.getBreakTimer().passed(delay))
                                    break block13;
                                mc.execute(() -> this.value.setCurrentCrystal(crystal));
                                useEntity = new PlayerInteractEntityC2SPacket(crystal);
                                animation = new HandSwingC2SPacket(Hand.MAIN_HAND);
                                this.value.getBreakTimer().reset(delay);
                                this.attacking = true;
                                if (this.value.multiThread.getValue().booleanValue() && this.value.rotate.getValue() == Rotate.None) {
                                    mc.execute(((CrystalAura) this.value)::swing);
                                }
                                if (this.value.setDead.getValue().booleanValue() && !this.value.useYawLimit.getValue().booleanValue()) {
                                    crystal.remove(Entity.RemovalReason.KILLED);
                                    if (this.value.dangerous.getValue().booleanValue()) {
                                        Calculation.mc.world.removeEntity(crystal.getId(), Entity.RemovalReason.KILLED);
                                    }
                                    this.value.killed.put(crystal.getId(), new EntityTime(crystal, System.nanoTime()));
                                }
                                if (this.value.rotate.getValue().noRotate(Rotate.Break) || RotationUtil.isLegit(crystal) || RotationUtil.isLegitRaytrace(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()))
                                    break block14;
                                if (!Arrays.equals(new int[]{Math.round(Managers.ROTATION.getServerYaw()), Math.round(Managers.ROTATION.getServerPitch())}, new int[]{Math.round(MathHelper.wrapDegrees(RotationUtil.getRotations(crystal)[0])), Math.round(RotationUtil.getRotations(crystal)[1])}))
                                    break block15;
                            }
                            boolean flag = false;
                            int toolSlot = InventoryUtil.findHotbarItem(net.minecraft.item.Items.DIAMOND_SWORD, net.minecraft.item.Items.DIAMOND_PICKAXE);
                            int lastSlot = Calculation.mc.player.getInventory().selectedSlot;
                            if (!DamageUtil.canBreakWeakness(true) && toolSlot != -1) {
                                InventoryUtil.switchTo(toolSlot);
                                flag = true;
                            }
                            Calculation.mc.player.networkHandler.sendPacket(useEntity);
                            Calculation.mc.player.networkHandler.sendPacket(animation);
                            if (flag) {
                                InventoryUtil.switchTo(lastSlot);
                            }
                            return !this.value.multiTask.getValue();
                        }
                        if (!this.value.useYawLimit.getValue().booleanValue()) break block16;
                        float[] rotation = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
                        float[] target = RotationUtil.getRotations(crystal);
                        this.rotations = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
                        if (Arrays.equals(this.rotations, RotationUtil.getRotations(crystal)) || RotationUtil.isLegitRaytrace(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()) || RotationUtil.isLegit(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()))
                            break block17;
                        if (!Arrays.equals(new int[]{Math.round(Managers.ROTATION.getServerYaw()), Math.round(Managers.ROTATION.getServerPitch())}, new int[]{Math.round(MathHelper.wrapDegrees(RotationUtil.getRotations(crystal)[0])), Math.round(RotationUtil.getRotations(crystal)[1])}))
                            break block18;
                    }
                    this.packets.add(useEntity);
                    this.packets.add(animation);
                    break block18;
                }
                this.rotations = RotationUtil.getRotations(crystal);
                this.packets.add(useEntity);
                this.packets.add(animation);
            }
            return true;
        }
        if (this.value.explode.getValue().booleanValue() && crystal != null && !this.value.rotate.getValue().noRotate(Rotate.Break) && this.value.useYawLimit.getValue().booleanValue()) {
            this.rotations = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
            return false;
        }
        return false;
    }

    private void place(PlaceData data) {
        if (InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL) && this.value.shouldPlace) {
            BlockHitResult result;
            this.pos = data.getPos();
            if (!this.value.rotate.getValue().noRotate(Rotate.Place)) {
                float[] rotation = RotationUtil.getRotations(this.pos.up(), Direction.UP);
                if (this.value.useForPlace.getValue().booleanValue()) {
                    this.rotations = RotationUtil.getRotationsMaxYaw(this.pos.up(), (float) this.value.limit.getValue().intValue(), Managers.ROTATION.getServerYaw());
                    result = new BlockHitResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, data.getPos(), false);
                } else {
                    this.rotations = RotationUtil.getRotations(this.pos.up(), Direction.UP);
                    result = RayTraceUtil.getBlockHitResult(this.rotations[0], this.rotations[1], this.value.placeRange.getValue().floatValue());
                }
            } else {
                result = new BlockHitResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, data.getPos(), false);
            }
            if (data.getDamage() < this.value.minDamage.getValue().floatValue() && this.value.shouldFacePlace()) {
                this.value.slow.add(this.pos.up());
            }
            BlockHitResult hitResult = new BlockHitResult(new Vec3d((float) result.getPos().x, (float) result.getPos().y, (float) result.getPos().z), result.getSide(), this.pos, false);
            HandSwingC2SPacket animation = new HandSwingC2SPacket(this.getHand());
            this.value.getPlaceTimer().reset(this.value.placeDelay.getValue().intValue());
            this.value.getPositions().add(this.pos.up());
            if ((this.value.rotate.getValue().noRotate(Rotate.Place) || RotationUtil.isLegit(this.pos)) && this.packets.isEmpty()) {
                InventoryUtil.syncItem();
                NetworkUtil.sendSequenced(sequence -> new PlayerInteractBlockC2SPacket(this.getHand(), hitResult, sequence));
                Calculation.mc.player.networkHandler.sendPacket(animation);
            } else if (this.value.useForPlace.getValue().booleanValue() && RotationUtil.isLegit(this.pos)) {
                NetworkUtil.sendSequenced(sequence -> {
                    PlayerInteractBlockC2SPacket place =  new PlayerInteractBlockC2SPacket(this.getHand(), hitResult, sequence);
                    this.packets.add(place);
                    return place;
                });
                this.packets.add(animation);
            } else if (!this.value.useForPlace.getValue().booleanValue()) {
                NetworkUtil.sendSequenced(sequence -> {
                    PlayerInteractBlockC2SPacket place =  new PlayerInteractBlockC2SPacket(this.getHand(), hitResult, sequence);
                    this.packets.add(place);
                    return place;
                });
                this.packets.add(animation);
            }
            this.setRenderPos(data);
        } else if (this.value.isSwitching()) {
            Runnable runnable = () -> {
                if (this.value.mainHand.getValue().booleanValue()) {
                    int slot = InventoryUtil.findHotbarItem(net.minecraft.item.Items.END_CRYSTAL);
                    InventoryUtil.switchTo(slot);
                } else {
                    OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                }
                this.value.setSwitching(false);
            };
            mc.execute(() -> {
                this.value.postRunnable = runnable;
                // return this.value.postRunnable;
            });
        }
    }

    private void updatePlaceRotations(PlaceData data) {
        if (InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL) && BlockUtil.canPlaceCrystal(data.getPos(), false, false)) {
            this.pos = data.getPos();
            if (!this.value.rotate.getValue().noRotate(Rotate.Place)) {
                float[] rotation = RotationUtil.getRotations(this.pos.up(), Direction.UP);
                if (this.value.useForPlace.getValue().booleanValue()) {
                    this.rotations = RotationUtil.getRotationsMaxYaw(this.pos.up(), (float) this.value.limit.getValue().intValue(), Managers.ROTATION.getServerYaw());
                    BlockHitResult result = new BlockHitResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, data.getPos(), false);
                } else {
                    this.rotations = RotationUtil.getRotations(this.pos.up(), Direction.UP);
                    BlockHitResult result = RayTraceUtil.getBlockHitResult(this.rotations[0], this.rotations[1], this.value.placeRange.getValue().floatValue());
                }
            } else {
                BlockHitResult result = new BlockHitResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, data.getPos(), false);
            }
            if (data.getDamage() < this.value.minDamage.getValue().floatValue() && this.value.shouldFacePlace()) {
                this.value.slow.add(this.pos.up());
            }
        }
    }

    private boolean checkPos(BlockPos pos, int count) {
        boolean rotating = false;
        if (!this.attacking && this.value.fallBack.getValue().booleanValue()) {
            Entity fallBack = this.breakData.getFallBack();
            if (this.value.antiSurr.getValue().booleanValue()) {
                if (pos != null) {
                    for (Entity entity : this.crystals) {
                        BlockPos entityPos;
                        if (!(entity instanceof EndCrystalEntity) || !entity.isAlive() || !entity.getBoundingBox().intersects(new Box(pos.up())) && (this.value.newerVer.getValue().booleanValue() || !entity.getBoundingBox().intersects(new Box(pos.up(2)))) || (entityPos = PositionUtil.getPosition(entity)).equals(pos.up()))
                            continue;
                        if (fallBack != null) {
                            boolean bl = rotating = this.attack(fallBack, this.breakData.getFallBackDamage()) && !this.value.rotate.getValue().noRotate(Rotate.Place) && !RotationUtil.isLegit(pos);
                        }
                        if (fallBack == null || !this.attacking) {
                            return false;
                        }
                        break;
                    }
                }
            } else if (pos == null && fallBack != null) {
                this.attack(fallBack, this.breakData.getFallBackDamage());
                return false;
            }
        }
        return pos != null && !rotating && (count < this.value.multiPlace.getValue() || this.value.antiSurr.getValue() && !BlockUtil.canPlaceCrystal(pos, false, this.value.newerVer.getValue()));
    }

    private boolean shouldPlaceCalc() {
        return InventoryUtil.isHolding(Items.END_CRYSTAL) || this.value.attack.getValue() == Attack.Calc || this.value.autoSwitch.getValue() == AutoSwitch.Always || this.value.isSwitching();
    }

    private Hand getHand() {
        return Calculation.mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void setRenderPos(PlaceData data) {
        if (this.value.multiThread.getValue().booleanValue() && this.value.rotate.getValue() == Rotate.None) {
            mc.execute(() -> {
                this.value.setRenderPos(data.getPos());
                this.value.setTarget(data.getTarget());
            });
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    public Entity getCrystal() {
        return this.crystal;
    }
}

