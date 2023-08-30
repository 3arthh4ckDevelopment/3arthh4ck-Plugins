package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class Manual32k extends Module {

    private static final Manual32k INSTANCE = new Manual32k();

    private static final ModuleCache<SecretClose> SECRET_CLOSE =
            Caches.getModule(SecretClose.class);

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Hopper));
    private final Setting<Boolean> bypass =
            register(new BooleanSetting("Illegal-Bypass", false));
    private final Setting<Boolean> msg =
            register(new BooleanSetting("Info-Messages", true));
    private final Setting<Boolean> autodisable =
            register(new BooleanSetting("Auto-Disable", true));
    private final Setting<Double> timeout =
            register(new NumberSetting<>("Disable-Ticks", 70.0, 30.0, 100.0));

    public BlockPos target;
    public int redstone;
    public static int stage;
    public static BlockPos targetFront;
    public int hopper;
    public int shulker;
    public int dispenser;
    public int obsidian;
    public int tickDelay;
    
    public Manual32k() {
        super("Manual32k", Category.Combat);
        this.setData(new SimpleData(this, "Places a 32k where you aim"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            onUpdate();
        }));
    }

    public static Manual32k getInstance() {
        return INSTANCE;
    }

    private void sendClientMessage(String message) {
        Managers.CHAT.sendDeleteMessage(message, this.getName(), ChatIDs.MODULE);
    }
    
    @Override
    public void onEnable() {
        targetFront = null;
        stage = -1;
        tickDelay = 0;
        if (msg.getValue()) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GREEN + "Enabled!");
        }
        hopper = InventoryUtil.findHotbarBlock(Blocks.HOPPER);
        shulker = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (CheckUtil.shulkers.contains(block)) {
                        shulker = i;
                    }
                }
            }
        }
        if (mode.getValue() == Mode.Dispenser) {
            obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            dispenser = InventoryUtil.findHotbarBlock(Blocks.DISPENSER);
            redstone = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        }
        target = null;
        if (obsidian == -1) {
            if (mode.getValue() == Mode.Dispenser) {
                sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Missing Obsidian!");
                disable();
                return;
            }
        }
        if (dispenser == -1 && mode.getValue() == Mode.Dispenser) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Missing Dispenser!");
            disable();
            return;
        }
        if (redstone == -1) {
            if (mode.getValue() == Mode.Dispenser) {
                sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Missing Redstone Block!");
                disable();
                return;
            }
        }
        if (hopper == -1) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Missing Hopper!");
            disable();
            return;
        }
        if (shulker == -1) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Missing Shulker Box!");
            disable();
            return;
        }
        if (mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver.getBlockPos().up() == null) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.GRAY + "Not a valid target!");
            disable();
            return;
        }
        target = mc.objectMouseOver.getBlockPos();
        targetFront = target.offset(mc.player.getHorizontalFacing().getOpposite());
        if (mode.getValue() == Mode.Dispenser) {
            stage = 0;
        }
        else if (mode.getValue() == Mode.Hopper) {
            stage = 5;
        }
    }

    public void onUpdate() {
        ++tickDelay;
        if (stage == 0) {
            if (mc.world.getBlockState(targetFront).getMaterial().isReplaceable()) {
                mc.player.inventory.currentItem = dispenser;
                CheckUtil.placeBlock(target.add(0, 1, 0), EnumFacing.DOWN, true);
                mc.player.inventory.currentItem = shulker;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(1.4598103E38f) ^ 0x7EDBA5D9), Float.intBitsToFloat(Float.floatToIntBits(1.8464406E38f) ^ 0x7F0AE927), Float.intBitsToFloat(Float.floatToIntBits(2.3622866E38f) ^ 0x7F31B7FC)));
                target = target.add(0, -1, 0);
                targetFront = targetFront.add(0, -1, 0);
                stage = 1;
                return;
            }
            if (!mc.world.getBlockState(targetFront).getMaterial().isReplaceable()) {
                mc.player.inventory.currentItem = obsidian;
                CheckUtil.placeBlock(target.add(0, 1, 0), EnumFacing.DOWN, true);
                mc.player.inventory.currentItem = dispenser;
                CheckUtil.placeBlock(target.add(0, 2, 0), EnumFacing.DOWN, true);
                mc.player.inventory.currentItem = shulker;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target.add(0, 2, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(2.5178906E38f) ^ 0x7F3D6CCF), Float.intBitsToFloat(Float.floatToIntBits(1.707817E38f) ^ 0x7F007B5C), Float.intBitsToFloat(Float.floatToIntBits(1.8566009E38f) ^ 0x7F0BACD5)));
                stage = 1;
                return;
            }
        }
        if (stage == 1) {
            if (!(mc.currentScreen instanceof GuiDispenser)) {
                if (tickDelay > timeout.getValue() && autodisable.getValue()) {
                    sendClientMessage("Tick Timeout!");
                    disable();
                }
                return;
            }
            else {
                mc.playerController.windowClick(mc.player.openContainer.windowId, 4, shulker, ClickType.SWAP, mc.player);
                mc.player.closeScreen();
                final BlockPos attempt1 = target.add(0, 3, 0);
                final BlockPos attempt2 = target.add(0, 2, 0).offset(mc.player.getHorizontalFacing().rotateY());
                final BlockPos attempt3 = target.add(0, 2, 0).offset(mc.player.getHorizontalFacing().rotateYCCW());
                final BlockPos attempt4 = target.add(0, 2, 0).offset(mc.player.getHorizontalFacing());
                final EnumFacing towardplayer = mc.player.getHorizontalFacing().getOpposite();
                final EnumFacing rightside = mc.player.getHorizontalFacing().rotateY().getOpposite();
                final EnumFacing leftside = mc.player.getHorizontalFacing().rotateYCCW().getOpposite();
                final Material block1 = mc.world.getBlockState(attempt1).getMaterial();
                final Material block2 = mc.world.getBlockState(attempt2).getMaterial();
                final Material block3 = mc.world.getBlockState(attempt3).getMaterial();
                final Material block4 = mc.world.getBlockState(attempt4).getMaterial();
                mc.player.inventory.currentItem = redstone;
                if (block1.isReplaceable()) {
                    CheckUtil.placeBlock(attempt1, EnumFacing.DOWN, true);
                    stage = 2;
                }
                if (block2.isReplaceable() && !block1.isReplaceable()) {
                    CheckUtil.placeBlock(attempt2, rightside, true);
                    stage = 2;
                }
                if (block3.isReplaceable()) {
                    if (!block1.isReplaceable()) {
                        if (!block2.isReplaceable()) {
                            CheckUtil.placeBlock(attempt3, leftside, true);
                            stage = 2;
                        }
                    }
                }
                if (block4.isReplaceable() && !block1.isReplaceable() && !block2.isReplaceable() && !block3.isReplaceable()) {
                    CheckUtil.placeBlock(attempt4, towardplayer, true);
                    stage = 2;
                }
                if (!block1.isReplaceable() && !block2.isReplaceable() && !block3.isReplaceable() && !block4.isReplaceable()) {
                    sendClientMessage("No viable redstone place targets!");
                    disable();
                    return;
                }
            }
        }
        if (stage == 2) {
            mc.player.inventory.currentItem = hopper;
            if (!(mc.world.getBlockState(targetFront.add(0, 2, 0)).getBlock() instanceof BlockShulkerBox)) {
                if (tickDelay > (double)timeout.getValue()) {
                    disable();
                }
                return;
            }
            else {
                CheckUtil.placeBlock(targetFront.add(0, 1, 0), mc.player.getHorizontalFacing(), false);
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetFront.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(3.2959972E38f) ^ 0x7F77F699), Float.intBitsToFloat(Float.floatToIntBits(1.1577955E38f) ^ 0x7EAE34A9), Float.intBitsToFloat(Float.floatToIntBits(3.247007E38f) ^ 0x7F744715)));
                mc.player.inventory.currentItem = shulker;
                stage = 3;
            }
        }
        if (stage == 3) {
            if (tickDelay > timeout.getValue()) {
                disable();
                return;
            }
            if (!(mc.currentScreen instanceof GuiHopper)) {
                return;
            }
            if (((GuiContainer)mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty()) {
                return;
            }
            mc.playerController.windowClick(mc.player.openContainer.windowId, 0, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)mc.player);
            stage = 4;
        }
        if (stage == 4) {
            if (!(mc.currentScreen instanceof GuiHopper)) {
                disable();
                return;
            }
            if (bypass.getValue()) {
                if (!SECRET_CLOSE.isEnabled()) {
                    SECRET_CLOSE.enable();
                }
                mc.player.closeScreen();
                if (!SECRET_CLOSE.isEnabled()) {
                    SECRET_CLOSE.enable();
                }
                disable();
            }
        }
        if (stage == 5) {
            mc.player.inventory.currentItem = hopper;
            if (mc.world.getBlockState(target).getMaterial().isReplaceable()) {
                CheckUtil.placeBlock(target, EnumFacing.DOWN, true);
            }
            else {
                CheckUtil.placeBlock(target = target.add(0, 1, 0), EnumFacing.DOWN, true);
            }
            stage = 6;
        }
        if (stage == 6) {
            mc.player.inventory.currentItem = shulker;
            CheckUtil.placeBlock(target.add(0, 1, 0), EnumFacing.DOWN, false);
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, EnumFacing.DOWN, EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(1.3574728E38f) ^ 0x7ECC3FF3), Float.intBitsToFloat(Float.floatToIntBits(2.7638736E37f) ^ 0x7DA65837), Float.intBitsToFloat(Float.floatToIntBits(3.1492177E38f) ^ 0x7F6CEBBA)));
            if (!(mc.currentScreen instanceof GuiHopper) && tickDelay > timeout.getValue()) {
                disable();
                return;
            }
            stage = 7;
        }
        if (stage == 7) {
            if (tickDelay > timeout.getValue()) {
                disable();
                return;
            }
            if (!(mc.currentScreen instanceof GuiHopper)) {
                return;
            }
            if (((GuiContainer)mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty()) {
                return;
            }
            mc.playerController.windowClick(mc.player.openContainer.windowId, 0, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            stage = 4;
        }
    }
    
    @Override
    public void onDisable() {
        if (msg.getValue()) {
            sendClientMessage(TextFormatting.BLUE + "[Auto32k] " + TextFormatting.RED + "Disabled!");
        }
        stage = -1;
        tickDelay = 0;
    }

    private enum Mode {
        Hopper,
        Dispenser
    }

}
