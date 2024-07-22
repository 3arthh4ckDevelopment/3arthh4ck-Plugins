package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.Target;
import me.earth.crystalauraplugin.module.util.PlaceData;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.Wrapper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

final class HelperPlace
        extends Wrapper<CrystalAura>
        implements Globals {
    public HelperPlace(CrystalAura module) {
        super(module);
    }

    public PlaceData createData(List<PlayerEntity> players, List<Entity> crystals) {
        PlaceData data = new PlaceData(this.value.target.getValue().getTarget(players, this.value.targetRange.getValue().floatValue()), crystals);
        if (data.getTarget() != null || this.value.target.getValue() == Target.Damage) {
            this.evaluate(data, players);
        }
        return data;
    }

    private void evaluate(PlaceData data, List<PlayerEntity> players) {
        BlockUtil.sphere(this.value.placeRange.getValue().floatValue(), pos -> {
            if (this.isValid(data, pos)) {
                float self;
                float f = self = this.value.suicide.getValue() ? -1.0f : DamageUtil.calculate(pos);
                if (self < EntityUtil.getHealth(HelperPlace.mc.player) - 1.0f) {
                    if (self > this.value.maxSelfP.getValue().floatValue() && !this.value.override.getValue() || this.calcFriends(pos, players)) {
                        return false;
                    }
                    if (this.value.target.getValue() == Target.Damage) {
                        for (PlayerEntity player : players) {
                            this.calc(pos, data, player, self);
                        }
                    } else {
                        this.calc(pos, data, data.getTarget(), self);
                    }
                } else {
                    this.value.setUnsafe();
                }
            }
            return false;
        });
    }

    private void calc(BlockPos pos, PlaceData data, PlayerEntity player, float self) {
        if (!Managers.FRIENDS.contains(player) && this.isValid(player, pos)) {
            float damage = DamageUtil.calculate(pos, this.getPlayer(player));
            if ((self <= this.value.maxSelfP.getValue().floatValue() || this.value.override.getValue().booleanValue() && (double) damage > (double) EntityUtil.getHealth(player) + 1.0) && (damage > data.getDamage() || damage >= data.getDamage() && data.getSelfDamage() > self)) {
                data.setDamage(damage);
                data.setSelfDamage(self);
                data.setTarget(player);
                data.setPos(pos);
            }
        }
    }

    private boolean calcFriends(BlockPos pos, List<PlayerEntity> players) {
        if (this.value.noFriendP.getValue().booleanValue()) {
            for (PlayerEntity player : players) {
                float damage;
                if (!this.isValid(player, pos) || !Managers.FRIENDS.contains(player) || !((double) (damage = DamageUtil.calculate(pos, this.getPlayer(player))) > (double) EntityUtil.getHealth(player) + 1.0))
                    continue;
                return true;
            }
        }
        return false;
    }

    private boolean isValid(PlayerEntity player, BlockPos pos) {
        if (player != null && !EntityUtil.isDead(player) && !player.equals(HelperPlace.mc.player)) {
            return player.squaredDistanceTo(pos.toCenterPos()) <= (double) MathUtil.square(this.value.range.getValue().floatValue());
        }
        return false;
    }

    private boolean isValid(PlaceData data, BlockPos pos) {
        if (BlockUtil.getDistanceSq(pos) <= (double) MathUtil.square(this.value.placeRange.getValue().floatValue()) && BlockUtil.canPlaceCrystal(pos, this.value.antiSurr.getValue(), this.value.newerVer.getValue(), this.getEntityList(data))) {
            if (HelperPlace.mc.player.squaredDistanceTo(pos.toCenterPos()) > (double) MathUtil.square(this.value.placeTrace.getValue().floatValue()) && !RayTraceUtil.raytracePlaceCheck(HelperPlace.mc.player, pos)) {
                return false;
            }
            return this.combinedTraceCheck(pos);
        }
        return false;
    }

    private boolean combinedTraceCheck(BlockPos pos) {
        if (BlockUtil.getDistanceSq(pos) <= (double) MathUtil.square(this.value.pbTrace.getValue().floatValue())) {
            return true;
        }
        return RayTraceUtil.canBeSeen(new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + (double) 2.7f, (double) pos.getZ() + 0.5), HelperPlace.mc.player);
    }

    private List<Entity> getEntityList(PlaceData data) {
        return this.value.multiThread.getValue() ? data.getEntities() : null;
    }

    private PlayerEntity getPlayer(PlayerEntity player) {
        if (this.value.interpolate.getValue() > 0) {
            return this.interpolate(player);
        }
        return player;
    }

    private PlayerEntity interpolate(PlayerEntity player) {
        Vec3d last = new Vec3d(player.prevX, player.prevY, player.prevZ);
        Vec3d current = player.getPos();
        Vec3d diff = last.subtract(current);
        if (diff.lengthSquared() < 0.001) {
            return player;
        }
        ClientPlayerEntity out = new ClientPlayerEntity(mc,
                HelperPlace.mc.world,
                mc.player.networkHandler,
                mc.player.getStatHandler(),
                mc.player.getRecipeBook(),
                player.isSneaking(),
                player.isSprinting());
        // out inventory == player inventory
        out.setHealth(EntityUtil.getHealth(player));
        out.setAbsorptionAmount(player.getAbsorptionAmount());
        Vec3d pos = current.add(diff);
        for (int i = 0; i < this.value.interpolate.getValue() && HelperPlace.mc.world.getBlockState(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)).isReplaceable(); ++i) {
            current = current.add(diff);
        }
        out.setPosition(current.x, current.y, current.z);
        return out;
    }
}

