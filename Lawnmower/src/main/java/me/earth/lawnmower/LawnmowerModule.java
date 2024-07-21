package me.earth.lawnmower;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;

public class LawnmowerModule extends Module {

    // Types
    protected final Setting<Boolean> tallGrass =
            register(new BooleanSetting("Tall-Grass", true));
    protected final Setting<Boolean> shortGrass =
            register(new BooleanSetting("Short-Grass", true));
    protected final Setting<Boolean> flowers =
            register(new BooleanSetting("Flowers", true));
    // Settings
    protected final Setting<Double> range =
            register(new NumberSetting<>("Range", 6.0, 0.1, 10.0));
    protected final Setting<Integer> ticksDelay =
            register(new NumberSetting<>("TicksDelay", 20, 1, 100));
    protected final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> render =
            register(new BooleanSetting("Render", true));

    private BlockPos pos;
    private int ticks;

    public LawnmowerModule() {
        super("Lawnmower", Category.Misc);
        this.setData(new LawnmowerData(this));

        this.listeners.add(new LambdaListener<>(TickEvent.class, event -> {
            if (mc.player == null || mc.world == null) return;

            ticks++;
            if (pos != null) {
                // waiting for the delay
                if (ticks >= ticksDelay.getValue()) {
                    mc.interactionManager.attackBlock(pos, RayTraceUtil.getFacing(mc.player, pos, true));
                    mc.player.swingHand(Hand.MAIN_HAND);
                    pos = null;
                    ticks = 0;
                }
            } else {
                BlockPos middle = PositionUtil.getPosition(mc.player);
                int maxRadius = Sphere.getRadius(range.getValue());
                BlockPos.Mutable mutPos = new BlockPos.Mutable();
                for (int i = 1; i < maxRadius; i++) {
                    Vec3i vec3i = Sphere.get(i);
                    mutPos.set(middle.getX() + vec3i.getX(), middle.getY() + vec3i.getY(), middle.getZ() + vec3i.getZ());
                    if (isBreakableBlock(mutPos)) {
                        if (rotate.getValue()) {
                            float[] rotations = RotationUtil.getRotationsToTopMiddle(mutPos);
                            mc.player.setYaw(rotations[0]);
                            mc.player.setPitch(rotations[1]);
                        }
                        pos = mutPos.toImmutable();
                        break;
                    }
                }
            }
        }));

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
            if (mc.player == null || mc.world == null || !render.getValue()) return;

            BlockPos middle = PositionUtil.getPosition(mc.player);
            int maxRadius = Sphere.getRadius(range.getValue());
            BlockPos.Mutable mutPos = new BlockPos.Mutable();
            for (int i = 1; i < maxRadius; i++) {
                Vec3i vec3i = Sphere.get(i);
                mutPos.set(middle.getX() + vec3i.getX(), middle.getY() + vec3i.getY(), middle.getZ() + vec3i.getZ());
                if (isBreakableBlock(mutPos)) {
                    Color c1 = new Color(0, 255, 0, 100);
                    Color c2 = new Color(0, 255, 0, 180);
                    if (pos != null && pos.equals(mutPos.toImmutable())) {
                        c1 = new Color(255, 0, 0, 100);
                        c2 = new Color(255, 0, 0, 180);
                    }
                    Block block = mc.world.getBlockState(mutPos).getBlock();
                    if (block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN) {
                        RenderUtil.renderBox(event.getStack(), Interpolation.interpolatePos(mutPos, (mc.world.getBlockState(mutPos.up()).getBlock() != Blocks.AIR ? 2 : 1)), c1, c2, 2);
                    } else {
                        RenderUtil.renderBox(event.getStack(), Interpolation.interpolatePos(mutPos, 1), c1, c2, 2);
                    }
                }
            }
        }));
    }

    @Override
    public void onEnable() {
        pos = null;
        ticks = 0;
    }

    private boolean isBreakableBlock(BlockPos blockPos) {
        Block block = mc.world.getBlockState(blockPos).getBlock();

        if (block == Blocks.AIR) {
            return false;
        }

        if ((tallGrass.getValue() && (block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN))
                || (shortGrass.getValue() && (block == Blocks.SHORT_GRASS || block == Blocks.FERN))
                || (flowers.getValue() &&
                (block == Blocks.FLOWERING_AZALEA
                        || block == Blocks.AZALEA
                        || block == Blocks.LILY_OF_THE_VALLEY
                        || block == Blocks.DANDELION
                        || block == Blocks.POPPY
                        || block == Blocks.BLUE_ORCHID
                        || block == Blocks.ALLIUM
                        || block == Blocks.AZURE_BLUET
                        || block == Blocks.RED_TULIP
                        || block == Blocks.ORANGE_TULIP
                        || block == Blocks.WHITE_TULIP
                        || block == Blocks.PINK_TULIP
                        || block == Blocks.OXEYE_DAISY
                        || block == Blocks.CORNFLOWER
                        || block == Blocks.WITHER_ROSE
                        || block == Blocks.SUNFLOWER
                        || block == Blocks.LILAC
                        || block == Blocks.ROSE_BUSH
                        || block == Blocks.PEONY))) {
            return !isBreakableBlock(blockPos.down());
        } else {
            return false;
        }
    }

}
