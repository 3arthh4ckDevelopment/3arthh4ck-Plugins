package me.earth.crystalauraplugin.module.util;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerUtil implements Globals {
    private static final List<ClientPlayerEntity> fakePlayers = new ArrayList<>();

    public static ClientPlayerEntity createFakePlayerAndAddToWorld(GameProfile profile) {
        ClientPlayerEntity fakePlayer = PlayerUtil.createFakePlayer(profile);
        PlayerUtil.mc.world.addEntity(fakePlayer);
        return fakePlayer;
    }

    public static ClientPlayerEntity createFakePlayer(GameProfile profile) {
        ClientPlayerEntity fakePlayer = new ClientPlayerEntity(mc,
                        mc.world,
                        mc.player.networkHandler,
                        mc.player.getStatHandler(),
                        mc.player.getRecipeBook(),
                        mc.player.isSneaking(),
                        mc.player.isSprinting());
        //fakePlayer.inventory.copyInventory(PlayerUtil.mc.player.inventory); //TODO: add inventory copy
        fakePlayer.setPosition(PlayerUtil.mc.player.getX(), PlayerUtil.mc.player.getBoundingBox().minY, PlayerUtil.mc.player.getZ());
        fakePlayer.setHeadYaw(PlayerUtil.mc.player.headYaw);
        fakePlayer.setPitch(PlayerUtil.mc.player.getPitch());
        fakePlayer.headYaw = PlayerUtil.mc.player.headYaw;
        fakePlayer.setOnGround(mc.player.isOnGround());
        fakePlayer.setSneaking(PlayerUtil.mc.player.isSneaking());
        fakePlayer.setHealth(PlayerUtil.mc.player.getHealth());
        return fakePlayer;
    }

    public static void removeFakePlayer(ClientPlayerEntity fakePlayer) {
        if (fakePlayer != null && fakePlayers.stream().anyMatch(x -> x.equals(fakePlayer))) {
            fakePlayers.remove(fakePlayer);
            if (PlayerUtil.mc.world != null) {
                PlayerUtil.mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && fakePlayers.stream().anyMatch(x -> x.equals(entity));
    }

    public static boolean isOtherFakePlayer(Entity entity) {
        return entity != null && entity.getId() < 0;
    }

    public static boolean isCreative(PlayerEntity player) {
        return player != null && (player.isCreative() || player.isCreative());
    }

    public static boolean isFeetPlaceable(PlayerEntity player, boolean ignoreCrystals, boolean noBoost2, double range) {
        return PlayerUtil.getFeetPos(player, ignoreCrystals, noBoost2, range) != null;
    }

    public static boolean isFootBlock(PlayerEntity player, BlockPos pos) {
        ArrayList<BlockPos> posList = new ArrayList<BlockPos>();
        for (Direction facing : BlockUtil.getHorizontal()) {
            posList.add(PositionUtil.getPosition(player).offset(facing));
        }
        return posList.contains(pos);
    }

    public static BlockPos getFeetPos(PlayerEntity player, boolean ignoreCrystals, boolean noBoost2, double range) {
        BlockPos off1;
        BlockPos off;
        BlockPos origin = new BlockPos(player.getBlockPos()).down();
        List<Object> valid = new ArrayList();
        for (Direction face : Direction.values()) {
            if (face == Direction.DOWN || face == Direction.UP) continue;
            off = origin.offset(face);
            off1 = origin.offset(face).offset(face);
            if (PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.OBSIDIAN || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.ENDER_CHEST || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.BEDROCK) {
                return null;
            }
            if (BlockUtil.canPlaceCrystal(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystal(off1, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        if (!(valid = valid.stream().filter(pos -> PlayerUtil.mc.player.squaredDistanceTo(((BlockPos) pos).toCenterPos()) <= range * range).sorted().sorted(Comparator.comparing(pos -> Float.valueOf(DamageUtil.calculate((BlockPos) pos) * -1.0f))).collect(Collectors.toList())).isEmpty()) {
            return (BlockPos) valid.get(0);
        }
        for (Direction face : Direction.values()) {
            if (face == Direction.DOWN || face == Direction.UP) continue;
            off = origin.offset(face);
            off1 = origin.offset(face).offset(face);
            if (BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystalFuture(off1, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        return (valid = valid.stream().filter(pos -> PlayerUtil.mc.player.squaredDistanceTo(((BlockPos) pos).toCenterPos()) <= range * range).sorted().sorted(Comparator.comparing(pos -> Float.valueOf(DamageUtil.calculate((BlockPos) pos) * -1.0f))).collect(Collectors.toList())).isEmpty() ? null : (BlockPos) valid.get(0);
    }

    public static boolean isFootPlace(BlockPos pos, PlayerEntity player, boolean ignoreCrystals, boolean noBoost2, double range) {
        BlockPos origin = new BlockPos(player.getBlockPos()).down();
        ArrayList<BlockPos> valid = new ArrayList<BlockPos>();
        for (Direction face : Direction.values()) {
            if (face == Direction.DOWN || face == Direction.UP) continue;
            BlockPos off = origin.offset(face);
            BlockPos off1 = origin.offset(face).offset(face);
            if (PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.OBSIDIAN || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.ENDER_CHEST || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.BEDROCK) {
                return false;
            }
            if (BlockUtil.canPlaceCrystal(off, ignoreCrystals, noBoost2) || BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystal(off1, ignoreCrystals, noBoost2) && !BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        return valid.contains(pos);
    }

    public static boolean isSafe(float maxDamage) {
        return BlockUtil.sphere(6.0, blockPos -> DamageUtil.calculate(blockPos) >= maxDamage);
    }

    public static BlockPos getBestPlace(BlockPos pos, PlayerEntity player) {
        Direction facing = PlayerUtil.getSide(player, pos);
        if (facing == Direction.UP) {
            Block block = PlayerUtil.mc.world.getBlockState(pos).getBlock();
            Block block2 = PlayerUtil.mc.world.getBlockState(pos.offset(Direction.UP)).getBlock();
            if (block2 instanceof AirBlock && (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK)) {
                return pos;
            }
        } else {
            BlockPos blockPos = pos.offset(facing);
            Block block = PlayerUtil.mc.world.getBlockState(blockPos).getBlock();
            BlockPos blockPos2 = blockPos.down();
            Block block2 = PlayerUtil.mc.world.getBlockState(blockPos2).getBlock();
            if (block instanceof AirBlock && (block2 == Blocks.OBSIDIAN || block2 == Blocks.BEDROCK)) {
                return blockPos2;
            }
        }
        return null;
    }

    public static Direction getSide(PlayerEntity player, BlockPos blockPos) {
        BlockPos playerPos = PositionUtil.getPosition(player);
        for (Direction facing : BlockUtil.getHorizontal()) {
            if (!playerPos.offset(facing).equals(blockPos)) continue;
            return facing;
        }
        if (playerPos.offset(Direction.UP).offset(Direction.UP).equals(blockPos)) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    public static boolean isValidFootCrystal(Entity crystal, PlayerEntity player) {
        Box bb = player.getBoundingBox().contract(0.0, 1.0, 0.0).expand(2.0, 0.0, 2.0).expand(-2.0, 0.0, -2.0);
        return PlayerUtil.mc.world.getEntitiesWithinAABB(Entity.class, bb).contains(crystal) && player.isOnGround();
    }

    public static boolean isInHole(PlayerEntity player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (Direction face : Direction.values()) {
            if (face == Direction.UP || face == Direction.DOWN || BlockUtil.isReplaceable(position.offset(face)))
                continue;
            ++count;
        }
        return count >= 3;
    }

    public static boolean willBlockBlockCrystal(BlockPos pos, BlockPos crystalPos) {
        return pos.toCenterPos().equals(crystalPos.up().toCenterPos());
    }

    public static Direction getOppositePlayerFace(PlayerEntity player, BlockPos pos) {
        for (Direction face : BlockUtil.getHorizontal()) {
            BlockPos off = pos.offset(face);
            Box bb = PlayerUtil.mc.world.getBlockState(off).getCollisionShape(PlayerUtil.mc.world, off).getBoundingBox();
            if (!PlayerUtil.mc.world.getEntitiesWithinAABB(Entity.class, bb).contains(player)) continue;
            return face;
        }
        return null;
    }

    public static Direction getOppositePlayerFaceBetter(PlayerEntity player, BlockPos pos) {
        for (Direction face : BlockUtil.getHorizontal()) {
            BlockPos off = pos.offset(face);
            BlockPos off1 = pos.offset(face).offset(face);
            BlockPos playerOff = PositionUtil.getPosition(player);
            if (!off.equals(playerOff) && !off1.equals(off1))
                continue;
            return face.getOpposite();
        }
        return null;
    }

    public static List<BlockPos> crystalPosFromLegBlock(PlayerEntity player, BlockPos pos) {
        ArrayList<BlockPos> valid = new ArrayList<>();
        Direction facing = Objects.requireNonNull(PlayerUtil.getOppositePlayerFaceBetter(player, pos)).getOpposite();
        Direction rotated = facing.rotateClockwise(Direction.Axis.Y);
        Direction inverse = facing.rotateClockwise(Direction.Axis.Y).rotateClockwise(Direction.Axis.Y).rotateClockwise(Direction.Axis.Y);
        if (BlockUtil.canPlaceCrystal(pos.offset(facing).offset(rotated).down(), true, false)) {
            valid.add(pos.offset(facing).offset(rotated).down());
        }
        if (BlockUtil.canPlaceCrystal(pos.offset(facing).offset(inverse).down(), true, false)) {
            valid.add(pos.offset(facing).offset(inverse).down());
        }
        return valid;
    }
}

