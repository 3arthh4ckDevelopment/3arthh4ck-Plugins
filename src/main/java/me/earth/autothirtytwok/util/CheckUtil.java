package me.earth.autothirtytwok.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class CheckUtil implements Globals {
    public static boolean holding32k(EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (stack.getTagCompound() == null) {
            return false;
        }

        NBTBase nbt = stack.getTagCompound().getTag("ench");

        if (nbt != null) {
            NBTTagList enchants = (NBTTagList) nbt;

            for (int i = 0; i < enchants.tagCount(); i++) {
                NBTTagCompound enchant = enchants.getCompoundTagAt(i);
                if (enchant.getInteger("id") == 16) {
                    int lvl = enchant.getInteger("lvl");
                    if (lvl >= 42) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public static boolean simpleIs32k(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 254;
    }

    public static List<Block> shulkers =
            Arrays.asList(Blocks.BLACK_SHULKER_BOX,
                    Blocks.BLUE_SHULKER_BOX,
                    Blocks.BROWN_SHULKER_BOX,
                    Blocks.CYAN_SHULKER_BOX,
                    Blocks.GRAY_SHULKER_BOX,
                    Blocks.GREEN_SHULKER_BOX,
                    Blocks.LIGHT_BLUE_SHULKER_BOX,
                    Blocks.LIME_SHULKER_BOX,
                    Blocks.MAGENTA_SHULKER_BOX,
                    Blocks.ORANGE_SHULKER_BOX,
                    Blocks.PINK_SHULKER_BOX,
                    Blocks.PURPLE_SHULKER_BOX,
                    Blocks.RED_SHULKER_BOX,
                    Blocks.SILVER_SHULKER_BOX,
                    Blocks.WHITE_SHULKER_BOX,
                    Blocks.YELLOW_SHULKER_BOX);

    private static List<Block> emptyBlocks =
            Arrays.asList(Blocks.AIR,
                    Blocks.FLOWING_LAVA,
                    Blocks.LAVA,
                    Blocks.FLOWING_WATER,
                    Blocks.WATER,
                    Blocks.VINE,
                    Blocks.SNOW_LAYER,
                    Blocks.TALLGRASS,
                    Blocks.FIRE);

    public static List<Block> rightclickableBlocks =
            Arrays.asList(Blocks.CHEST,
                    Blocks.TRAPPED_CHEST,
                    Blocks.ENDER_CHEST,
                    Blocks.WHITE_SHULKER_BOX,
                    Blocks.ORANGE_SHULKER_BOX,
                    Blocks.MAGENTA_SHULKER_BOX,
                    Blocks.LIGHT_BLUE_SHULKER_BOX,
                    Blocks.YELLOW_SHULKER_BOX,
                    Blocks.LIME_SHULKER_BOX,
                    Blocks.PINK_SHULKER_BOX,
                    Blocks.GRAY_SHULKER_BOX,
                    Blocks.SILVER_SHULKER_BOX,
                    Blocks.CYAN_SHULKER_BOX,
                    Blocks.PURPLE_SHULKER_BOX,
                    Blocks.BLUE_SHULKER_BOX,
                    Blocks.BROWN_SHULKER_BOX,
                    Blocks.GREEN_SHULKER_BOX,
                    Blocks.RED_SHULKER_BOX,
                    Blocks.BLACK_SHULKER_BOX,
                    Blocks.ANVIL,
                    Blocks.WOODEN_BUTTON,
                    Blocks.STONE_BUTTON,
                    Blocks.UNPOWERED_COMPARATOR,
                    Blocks.UNPOWERED_REPEATER,
                    Blocks.POWERED_REPEATER,
                    Blocks.POWERED_COMPARATOR,
                    Blocks.OAK_FENCE_GATE,
                    Blocks.SPRUCE_FENCE_GATE,
                    Blocks.BIRCH_FENCE_GATE,
                    Blocks.JUNGLE_FENCE_GATE,
                    Blocks.DARK_OAK_FENCE_GATE,
                    Blocks.ACACIA_FENCE_GATE,
                    Blocks.BREWING_STAND,
                    Blocks.DISPENSER,
                    Blocks.DROPPER,
                    Blocks.LEVER,
                    Blocks.NOTEBLOCK,
                    Blocks.JUKEBOX,
                    Blocks.BEACON,
                    Blocks.BED,
                    Blocks.FURNACE,
                    Blocks.OAK_DOOR,
                    Blocks.SPRUCE_DOOR,
                    Blocks.BIRCH_DOOR,
                    Blocks.JUNGLE_DOOR,
                    Blocks.ACACIA_DOOR,
                    Blocks.DARK_OAK_DOOR,
                    Blocks.CAKE,
                    Blocks.ENCHANTING_TABLE,
                    Blocks.DRAGON_EGG,
                    Blocks.HOPPER,
                    Blocks.REPEATING_COMMAND_BLOCK,
                    Blocks.COMMAND_BLOCK,
                    Blocks.CHAIN_COMMAND_BLOCK,
                    Blocks.CRAFTING_TABLE);

    public static boolean canPlaceBlock(final BlockPos blockPos) {
        if (isBlockEmpty(blockPos)) {
            final EnumFacing[] values;
            final EnumFacing[] facings = values = EnumFacing.values();
            for (final EnumFacing f : values) {
                if (!emptyBlocks.contains(mc.world.getBlockState(blockPos.offset(f)).getBlock())) {
                    if (mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(new Vec3d(blockPos.getX() + Double.longBitsToDouble(Double.doubleToLongBits(3.355300705956062) ^ 0x7FEAD7A7E5829DB1L) + f.getXOffset() * Double.longBitsToDouble(Double.doubleToLongBits(3.8028349362654716) ^ 0x7FEE6C34B91AC585L), blockPos.getY() + Double.longBitsToDouble(Double.doubleToLongBits(24.734424233592687) ^ 0x7FD8BC033A00ABA3L) + f.getYOffset() * Double.longBitsToDouble(Double.doubleToLongBits(3.295540950308987) ^ 0x7FEA5D4492E1A59AL), blockPos.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(10.491274617913197) ^ 0x7FC4FB8858C2958BL) + f.getZOffset() * Double.longBitsToDouble(Double.doubleToLongBits(109.21717605272146) ^ 0x7FBB4DE63662FA6FL))) <= Double.longBitsToDouble(Double.doubleToLongBits(1.6815268924336415) ^ 0x7FEBE788BE258D50L)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBlockEmpty(final BlockPos blockPos) {
        if (emptyBlocks.contains(mc.world.getBlockState(blockPos).getBlock())) {
            final AxisAlignedBB box = new AxisAlignedBB(blockPos);
            for (final Entity e : mc.world.loadedEntityList) {
                if (e instanceof EntityLivingBase && box.intersects(e.getEntityBoundingBox())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void placeBlock(final BlockPos blockPos, final int currentItem) {
        if (isBlockEmpty(blockPos)) {
            if (currentItem != mc.player.inventory.currentItem) {
                mc.player.inventory.currentItem = currentItem;
            }
            final EnumFacing[] values;
            final EnumFacing[] facings = values = EnumFacing.values();
            for (final EnumFacing f : values) {
                final Block neighborBlock = mc.world.getBlockState(blockPos.offset(f)).getBlock();
                final Vec3d vec = new Vec3d(blockPos.getX() + Double.longBitsToDouble(Double.doubleToLongBits(24.84391999719043) ^ 0x7FD8D80B24145F91L) + f.getXOffset() * Double.longBitsToDouble(Double.doubleToLongBits(25.804433879706213) ^ 0x7FD9CDEF60F521F1L), blockPos.getY() + Double.longBitsToDouble(Double.doubleToLongBits(31.85579280715992) ^ 0x7FDFDB153CC6E765L) + f.getZOffset() * Double.longBitsToDouble(Double.doubleToLongBits(3.843876318233424) ^ 0x7FEEC0423A257399L), blockPos.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(3.1206447371100756) ^ 0x7FE8F7149682940EL) + f.getZOffset() * Double.longBitsToDouble(Double.doubleToLongBits(39.13394912622493) ^ 0x7FA391253EB63B5FL));
                if (!emptyBlocks.contains(neighborBlock)) {
                    if (mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= Double.longBitsToDouble(Double.doubleToLongBits(1.4487694414316052) ^ 0x7FE62E28DDA64973L)) {
                        if (rightclickableBlocks.contains(neighborBlock)) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        }
                        mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.offset(f), f.getOpposite(), new Vec3d(blockPos), EnumHand.MAIN_HAND);
                        if (rightclickableBlocks.contains(neighborBlock)) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        return;
                    }
                }
            }
        }
    }

    public static void openBlock(final BlockPos blockPos) {
        final EnumFacing[] facings = EnumFacing.values();
        int checkedFacings = 0;
        for (final EnumFacing f : facings) {
            final Block neighborBlock = mc.world.getBlockState(blockPos.offset(f)).getBlock();
            if (emptyBlocks.contains(neighborBlock)) {
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, f.getOpposite(), new Vec3d(blockPos), EnumHand.MAIN_HAND);
                return;
            }
            ++checkedFacings;
        }
        if (checkedFacings == 6) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos, EnumFacing.DOWN, EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(1.2073655E38f) ^ 0x7EB5AA07), Float.intBitsToFloat(Float.floatToIntBits(2.7395858E38f) ^ 0x7F4E1A81), Float.intBitsToFloat(Float.floatToIntBits(2.6831478E38f) ^ 0x7F49DB8C)));
        }
    }

    public static void placeBlock(final BlockPos blockPos, final EnumFacing enumFacing, final boolean b) {
        final BlockPos adj = blockPos.offset(enumFacing);
        final EnumFacing opposite = enumFacing.getOpposite();
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        final Vec3d hitVec = new Vec3d(adj).add(Double.longBitsToDouble(Double.doubleToLongBits(16.38754870084008) ^ 0x7FD063366443B727L), Double.longBitsToDouble(Double.doubleToLongBits(23.98061988014231) ^ 0x7FD7FB09E78B04C7L), Double.longBitsToDouble(Double.doubleToLongBits(446.17607630769265) ^ 0x7F9BE2D13563F23FL)).add(new Vec3d(opposite.getDirectionVec()).scale(Double.longBitsToDouble(Double.doubleToLongBits(124.16788424340699) ^ 0x7FBF0ABE9D8DBC97L)));
        mc.playerController.processRightClickBlock(mc.player, mc.world, adj, opposite, hitVec, EnumHand.MAIN_HAND);
        if (b) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

}
