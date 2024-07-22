package me.earth.crystalauraplugin.module.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class RotationUtil
        implements Globals {
    public static float[] getRotations(BlockPos pos, Direction facing) {
        Box bb = RotationUtil.mc.world.getBlockState(pos).getBoundingBox(RotationUtil.mc.world, pos);
        double x = (double) pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = (double) pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = (double) pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;
        if (facing != null) {
            x += (double) facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += (double) facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += (double) facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }
        return RotationUtil.getRotations(x, y, z);
    }

    public static float[] getRotations(BlockPos pos) {
        return RotationUtil.getRotations((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5);
    }

    public static float[] getRotationsMaxYaw(BlockPos pos, float max, float current) {
        return new float[]{RotationUtil.updateRotation(current, RotationUtil.getRotations(pos)[0], max), RotationUtil.getRotations(pos)[1]};
    }

    public static float[] getRotations(Entity entity) {
        return RotationUtil.getRotations(entity.getX(), entity.getY() + (double) entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static float[] getRotationsMaxYaw(Entity entity, float max, float current) {
        return new float[]{MathHelper.wrapDegrees(RotationUtil.updateRotation(current, RotationUtil.getRotations(entity)[0], max)), RotationUtil.getRotations(entity)[1]};
    }

    public static float[] getRotations(Vec3d vec3d) {
        return RotationUtil.getRotations(vec3d.x, vec3d.y, vec3d.z);
    }

    public static float[] getRotations(double x, double y, double z) {
        return RotationUtil.getRotations(x, y, z, RotationUtil.mc.player);
    }

    public static float[] getRotations(double x, double y, double z, Entity from) {
        double xDiff = x - from.getX();
        double yDiff = y - PositionUtil.getEyeHeight(from);
        double zDiff = z - from.getZ();
        double dist = MathHelper.sqrt((float) (xDiff * xDiff + zDiff * zDiff));
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float diff = yaw - from.getHeadYaw();
        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - 360.0f * round;
        }
        return new float[]{from.getHeadYaw() + diff, pitch};
    }

    public static boolean inFov(Entity entity) {
        return RotationUtil.inFov(entity.getX(), entity.getY() + (double) (entity.getEyeHeight(entity.getPose()) / 2.0f), entity.getZ());
    }

    public static boolean inFov(BlockPos pos) {
        return RotationUtil.inFov((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
    }

    public static boolean inFov(double x, double y, double z) {
        Vec3d vec3d = MathUtil.fromTo(Interpolation.interpolatedEyePos(), x, y, z);
        if (vec3d.lengthSquared() < 1.0) {
            return true;
        }
        return MathUtil.angle(vec3d, Interpolation.interpolatedEyeVec()) <= (double) (RotationUtil.mc.options.getFov().getValue() / 2.0f);
    }

    public static boolean inFov(PlayerEntity entity, BlockPos pos) {
        Vec3d vec3d = MathUtil.fromTo(Interpolation.interpolatedEyeVec(), pos.getX(), pos.getY(), pos.getZ());
        if (vec3d.lengthSquared() < 1.0) {
            return true;
        }
        return MathUtil.angle(vec3d, Interpolation.interpolatedEyeVec()) <= 65.0;
    }

    public static double getAngle(Entity entity, double yOffset) {
        Vec3d vec3d = MathUtil.fromTo(Interpolation.interpolatedEyePos(), entity.getX(), entity.getY() + yOffset, entity.getZ());
        return MathUtil.angle(vec3d, Interpolation.interpolatedEyeVec());
    }

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public static boolean isLegit(Vec3d vec3d) {
        return RotationUtil.isLegit(vec3d.x, vec3d.y, vec3d.z);
    }

    public static boolean isLegit(Entity entity) {
        return RotationUtil.isLegit(entity.getX(), entity.getY() + (double) (entity.getEyeHeight(entity.getPose()) / 2.0f), entity.getZ());
    }

    public static boolean isLegit(Entity entity, float yaw, float pitch) {
        return RotationUtil.isLegit(entity.getX(), entity.getY() + (double) (entity.getEyeHeight(entity.getPose()) / 2.0f), entity.getZ(), yaw, pitch);
    }

    public static boolean isLegit(BlockPos pos) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        return RotationUtil.isLegit(x, y, z);
    }

    public static boolean isLegit(double x, double y, double z) {
        return RotationUtil.isLegit(x, y, z, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch());
    }

    public static boolean isLegit(double x, double y, double z, float yaw, float pitch) {
        Vec3d vec3d = MathUtil.fromTo(PositionUtil.getEyePos(), x, y, z);
        double dist = vec3d.length();
        if (dist < 0.5) {
            return true;
        }
        return MathUtil.angle(vec3d, RotationUtil.getVec3d(yaw, pitch)) * dist < 35.0;
    }

    public static boolean isLegitRaytrace(Entity entity, float yaw, float pitch) {
        BlockHitResult result = RayTraceUtil.getBlockHitResult(yaw, pitch, 6.0f);
        return result.getType() == HitResult.Type.ENTITY && result.getBlockPos().equals(entity.getBlockPos());
    }

    public static float[] faceEntitySmooth(double curYaw, double curPitch, double intendedYaw, double intendedPitch, double yawSpeed, double pitchSpeed) {
        float yaw = RotationUtil.updateRotation((float) curYaw, (float) intendedYaw, (float) yawSpeed);
        float pitch = RotationUtil.updateRotation((float) curPitch, (float) intendedPitch, (float) pitchSpeed);
        return new float[]{yaw, pitch};
    }

    public static float updateRotation(float current, float intended, float factor) {
        float updated = MathHelper.wrapDegrees(intended - current);
        if (updated > factor) {
            updated = factor;
        }
        if (updated < -factor) {
            updated = -factor;
        }
        return current + updated;
    }
}

