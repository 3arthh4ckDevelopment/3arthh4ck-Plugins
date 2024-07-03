package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.autothirtytwok.util.GuinnessRotationUtil;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NewAuto32k extends Module {

    private static final NewAuto32k INSTANCE = new NewAuto32k();

    private static final ModuleCache<SecretClose> SECRET_CLOSE =
            Caches.getModule(SecretClose.class);
    private static final ModuleCache<Auto32kReset> AUTO_32K_RESET =
            Caches.getModule(Auto32kReset.class);

    private final Setting<PlaceMode> placeMode =
            register(new EnumSetting<>("PlaceMode", PlaceMode.Auto));
    private final Setting<PlaceType> placeType =
            register(new EnumSetting<>("PlaceMode", PlaceType.Hopper));
    private final Setting<Float> range =
            register(new NumberSetting<>("Range", 3.0f, 1.0f, 3.0f));
    private final Setting<Boolean> timeOut =
            register(new BooleanSetting("Timeout", true));
    private final Setting<Integer> timeout =
            register(new NumberSetting<>("Timeout Ticks", 80, 60, 100));
    private final Setting<Boolean> secretClose =
            register(new BooleanSetting("SecretClose", false));
    private final Setting<Boolean> autoRetry =
            register(new BooleanSetting("AutoRetry", true));
    private final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", false));
    private final Setting<RotateMode> rotateMode =
            register(new EnumSetting<>("RotateMode", RotateMode.Server));
    private final Setting<Boolean> sidePlace =
            register(new BooleanSetting("ChinesePlace", false));

    public static BlockPos obbyPos;
    public static boolean placedDispenser;
    public static boolean skipping;
    public float pitch;
    public static int totalTicks;
    public EnumFacing direction;
    public static int phaseTicks = 0;
    public static BlockPos dispenserPos;
    public static BlockPos shulkerPos;
    public boolean dispenserFuckedUp;
    public static BlockPos rotationPos;
    public static boolean movedSword;
    public static int furnace = -1;
    public static int hopper = -1;
    public static BlockPos hopperPos;
    public static int dispenser = -1;
    public float yaw;
    public static int shulker = -1;
    public static boolean shulkerDispensed;
    public static boolean togglePitch = false;
    public static BlockPos redstonePos;
    public static boolean isDispenser;
    public static BlockPos placeTarget = new BlockPos(0,0,0);
    public static int block = -1;
    public static int redstone = -1;
    public PlacePhase phase;
    public static boolean isRotating;
    
    public NewAuto32k() {
        super("NewAuto32k", Category.Combat);
        this.setData(new SimpleData(this, "Automatically dispenses a 32k"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            onUpdate();

            if (isRotating) {
                mc.player.rotationYaw = this.yaw + (mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? 90 : (mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? 0 : (mc.player.getHorizontalFacing().equals(EnumFacing.WEST) ? -90 : 180)));
                mc.player.rotationPitch = (float) (this.pitch + 10.0);
            }
        }));

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, e -> {
            if (mc.player == null || mc.world == null) {
                return;
            }
            if (e.getPacket() instanceof CPacketPlayer) {
                if (isRotating && rotate.getValue() && rotateMode.getValue() == RotateMode.Server) {
                    final CPacketPlayer packet = (CPacketPlayer) e.getPacket();
                    ((ICPacketPlayer)packet).setYaw(this.yaw);
                    ((ICPacketPlayer)packet).setPitch(this.pitch);
                }
            }
        }));
    }

    public static NewAuto32k getInstance() {
        return INSTANCE;
    }
    
    public void disableSaying(final String s) {
        Managers.CHAT.sendDeleteMessage(s, this.getName(), ChatIDs.MODULE);
        disable();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.resetRotation();
        if (AUTO_32K_RESET.isEnabled()) {
            AUTO_32K_RESET.disable();
        }
        this.phase = PlacePhase.STARTING;
        block = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        redstone = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        dispenser = InventoryUtil.findHotbarBlock(Blocks.DISPENSER);
        hopper = InventoryUtil.findHotbarBlock(Blocks.HOPPER);
        furnace = InventoryUtil.findHotbarBlock(Blocks.FURNACE);
        phaseTicks = 0;
        totalTicks = 0;
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
        isDispenser = placeType.getValue() == PlaceType.Dispenser;
        placeTarget = null;
        obbyPos = null;
        dispenserPos = null;
        redstonePos = null;
        shulkerPos = null;
        hopperPos = null;
        rotationPos = null;
        movedSword = false;
        skipping = false;
        this.dispenserFuckedUp = false;
        shulkerDispensed = false;
        placedDispenser = false;
        Label_1061: {
            if (block == -1 && isDispenser) {
                this.disableSaying("Missing obsidian.");
            }
            else {
                if (redstone == -1) {
                    if (isDispenser) {
                        this.disableSaying("Missing redstone.");
                        break Label_1061;
                    }
                }
                if (dispenser == -1) {
                    if (isDispenser) {
                        this.disableSaying("Missing dispenser.");
                        break Label_1061;
                    }
                }
                if (hopper == -1) {
                    this.disableSaying("Missing hopper.");
                }
                else if (shulker == -1) {
                    this.disableSaying("Missing shulker.");
                }
            }
        }
        if (hopper != -1 && (dispenser != -1 || !isDispenser) && (redstone != -1 || !isDispenser)) {
            if (furnace != -1 || placeType.getValue() != PlaceType.Furnace) {
                if (block != -1 || !isDispenser) {
                    if (shulker != -1) {
                        if (placeMode.getValue() == PlaceMode.Aim) {
                            this.beginAimPlacement();
                        }
                        else {
                            this.beginAutoPlacement();
                        }
                    }
                }
            }
        }
    }
    
    public void beginAimPlacement() {
        this.direction = mc.player.getHorizontalFacing().getOpposite();
        placeTarget = mc.player.rayTrace(5.0, mc.getRenderPartialTicks()).getBlockPos();
        if (placeType.getValue() == PlaceType.Hopper) {
            if (CheckUtil.canPlaceBlock(placeTarget.up()) && CheckUtil.isBlockEmpty(placeTarget.add(0, 2, 0))) {
                this.putHopper32k();
            }
            else {
                this.disableSaying("Unable to place 32k.");
            }
        }
        else {
            if (this.cannotPlace(placeTarget) && this.cannotPlace(placeTarget.up())) {
                this.disableSaying("Unable to place 32k.");
                return;
            }
            if (this.canSkip(placeTarget)) {
                skipping = true;
            }
            else if (this.canSkip(placeTarget.up())) {
                placeTarget = placeTarget.up();
                skipping = true;
            }
            this.startDispenser32k();
        }
    }
    
    public void beginAutoPlacement() {
        final int range = this.range.getValue().intValue();
        for (int tries = 1; tries <= 4; ++tries) {
            for (int y = -1; y < ((mc.player.isAirBorne || mc.player.isElytraFlying()) ? 2 : 1); ++y) {
                for (int x = -range; x < range; ++x) {
                    for (int z = -range; z < range; ++z) {
                        placeTarget = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                        if (placeType.getValue() != PlaceType.Hopper && placeType.getValue() != PlaceType.Furnace) {
                            this.direction = ((tries == 1) ? EnumFacing.SOUTH : ((tries == 2) ? EnumFacing.EAST : ((tries == 3) ? EnumFacing.NORTH : EnumFacing.WEST)));
                            if (!this.cannotPlace(placeTarget)) {
                                if (this.canSkip(placeTarget)) {
                                    skipping = true;
                                }
                                this.startDispenser32k();
                                return;
                            }
                        }
                        else if (CheckUtil.canPlaceBlock(placeTarget.up()) && CheckUtil.isBlockEmpty(placeTarget.add(0, 2, 0))) {
                            this.putHopper32k();
                            return;
                        }
                    }
                }
            }
        }
        this.disableSaying("No viable placetargets.");
    }
    
    public void startDispenser32k() {
        mc.player.setSprinting(false);
        if (!skipping) {
            placeTarget = placeTarget.up();
            obbyPos = placeTarget;
            rotationPos = obbyPos;
            CheckUtil.placeBlock(obbyPos, block);
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.direction.equals(EnumFacing.EAST) ? 90 : (this.direction.equals(EnumFacing.NORTH) ? 0 : (this.direction.equals(EnumFacing.WEST) ? -90 : 180)), 0, mc.player.onGround));
        dispenserPos = placeTarget.up();
        redstonePos = this.redstoneTarget(dispenserPos);
        hopperPos = placeTarget.offset(this.direction);
        shulkerPos = hopperPos.up();
        rotationPos = dispenserPos;
        CheckUtil.placeBlock(dispenserPos, dispenser);
        placedDispenser = true;
        mc.player.setSneaking(false);
        CheckUtil.openBlock(dispenserPos);
        this.phase = PlacePhase.DISPENSERGUI;
    }
    
    public void putHopper32k() {
        hopperPos = placeTarget.up();
        shulkerPos = hopperPos.up();
        rotationPos = hopperPos;
        CheckUtil.placeBlock(hopperPos, hopper);
        rotationPos = shulkerPos;
        if (placeType.getValue() == PlaceType.Furnace) {
            CheckUtil.placeBlock(shulkerPos, furnace);
        }
        else {
            CheckUtil.placeBlock(shulkerPos, shulker);
        }
        rotationPos = hopperPos;
        mc.player.setSneaking(false);
        CheckUtil.openBlock(hopperPos);
        this.phase = PlacePhase.HOPPERGUI;
    }
    
    public boolean cannotSkip(final BlockPos blockPos) {
        if (CheckUtil.canPlaceBlock(blockPos) && CheckUtil.isBlockEmpty(blockPos.up()) && !CheckUtil.canPlaceBlock(blockPos.up())) {
            if (this.redstoneTarget(blockPos.up()) != null) {
                return CheckUtil.isBlockEmpty(blockPos.offset(this.direction)) && CheckUtil.isBlockEmpty(blockPos.offset(this.direction).up());
            }
        }
        return false;
    }
    
    public boolean canSkip(final BlockPos blockPos) {
        if (CheckUtil.canPlaceBlock(blockPos.up())) {
            if (this.redstoneTarget(blockPos.up()) != null) {
                if (CheckUtil.isBlockEmpty(blockPos.offset(this.direction))) {
                    return CheckUtil.isBlockEmpty(blockPos.offset(this.direction).up());
                }
            }
        }
        return false;
    }

    private void sendClientMessage(String message) {
        Managers.CHAT.sendDeleteMessage(message, this.getName(), ChatIDs.MODULE);
    }
    
    public boolean cannotPlace(final BlockPos blockPos) {
        return !this.cannotSkip(blockPos.up()) && !this.canSkip(blockPos);
    }

    public void onUpdate() {
        if (!isEnabled()) {
            return;
        }
        if (safetyCheck()) {
            mc.player.closeScreen();
            return;
        }
        if (placeTarget == null) {
            return;
        }
        ++phaseTicks;
        ++totalTicks;
        if (placeType.getValue() == PlaceType.Dispenser) {
            if (this.phase == PlacePhase.DISPENSERGUI) {
                if (mc.currentScreen instanceof GuiDispenser) {
                    if (this.dispenserFuckedUp) {
                        final boolean autoRetry = this.autoRetry.getValue();
                        mc.player.closeScreen();
                        this.disableSaying("Dispenser fucked up" + (autoRetry ? ", retrying." : "."));
                        if (!AUTO_32K_RESET.isEnabled()) {
                            if (autoRetry) {
                                AUTO_32K_RESET.enable();
                            }
                        }
                        return;
                    }
                    mc.playerController.windowClick(mc.player.openContainer.windowId, 4, shulker, ClickType.SWAP, mc.player);
                    mc.player.closeScreen();
                    this.phase = PlacePhase.REDSTONE;
                    phaseTicks = 0;
                    return;
                }
            }
            if (this.phase == PlacePhase.REDSTONE) {
                if (redstonePos == null) {
                    this.disableSaying("No viable redstone placements.");
                }
                else {
                    if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(redstonePos)).isEmpty()) {
                        sendClientMessage(TextFormatting.AQUA + "[Auto32k] " + TextFormatting.RED + "MOOOOOOOOOOOOOOOOOOVE");
                        return;
                    }
                    rotationPos = redstonePos;
                    CheckUtil.placeBlock(redstonePos, redstone);
                    this.phase = PlacePhase.HOPPER;
                    phaseTicks = 0;
                    rotationPos = hopperPos;
                }
                return;
            }
            if (this.phase == PlacePhase.HOPPER) {
                if (mc.world.getBlockState(shulkerPos).getBlock() instanceof BlockShulkerBox) {
                    shulkerDispensed = true;
                    CheckUtil.placeBlock(hopperPos, hopper);
                    CheckUtil.openBlock(hopperPos);
                    this.phase = PlacePhase.HOPPERGUI;
                    phaseTicks = 0;
                    mc.player.inventory.currentItem = shulker;
                    return;
                }
            }
        }
        if (this.phase == PlacePhase.HOPPERGUI) {
            if (!movedSword && mc.currentScreen instanceof GuiHopper) {
                if (((GuiContainer) mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty()) {
                    return;
                }
                mc.playerController.windowClick(mc.player.openContainer.windowId, 0, shulker, ClickType.SWAP, mc.player);
                movedSword = true;
                if (rotate.getValue()) {
                    this.resetRotation();
                }
                return;
            }
            else if (movedSword) {
                if (secretClose.getValue()) {
                    if (!SECRET_CLOSE.isEnabled()) {
                        SECRET_CLOSE.enable();
                    }
                    mc.player.closeScreen();
                }
                this.phase = PlacePhase.FINISHED;
                return;
            }
        }
        if (this.phase == PlacePhase.FINISHED) {
            phaseTicks = 0;
            if (!(mc.currentScreen instanceof GuiHopper)) {
                this.disable();
                if (rotateMode.getValue() == RotateMode.Client) {
                    if (rotate.getValue()) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.cameraYaw, mc.player.cameraPitch, mc.player.onGround));
                    }
                }
            }
        }
    }
    
    public boolean safetyCheck() {
        if (mc.player.getDistance(placeTarget.getX(), placeTarget.getY(), placeTarget.getZ()) > 8.0) {
            return true;
        }
        if (phaseTicks > (double) timeout.getValue() && timeOut.getValue()) {
            this.disableSaying("Timed out.");
            return true;
        }
        if (totalTicks > (double) timeout.getValue() * 2.0 && timeOut.getValue()) {
            this.disableSaying("Timed out.");
            return true;
        }
        if (placedDispenser && !shulkerDispensed && mc.world.getBlockState(dispenserPos).getBlock() instanceof BlockAir) {
            this.disableSaying("Dispenser was destroyed.");
            return true;
        }
        if (isDispenser && (dispenserPos.offset((EnumFacing) mc.world.getBlockState(dispenserPos).getValue((IProperty)PropertyDirection.create("facing"))).equals(dispenserPos.down()) || dispenserPos.offset((EnumFacing) mc.world.getBlockState(dispenserPos).getValue((IProperty)PropertyDirection.create("facing"))).equals(dispenserPos.up()))) {
            this.dispenserFuckedUp = true;
        }
        return false;
    }
    
    public BlockPos redstoneTarget(final BlockPos blockPos) {
        final EnumFacing[] facings = { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
        final EnumFacing[] sideFacings = { EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP };
        for (final EnumFacing f : sidePlace.getValue() ? sideFacings : facings) {
            if (!f.equals(this.direction)) {
                if (mc.world.getBlockState(blockPos.offset(f)).getMaterial().isReplaceable() && mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(blockPos.offset(f))).isEmpty()) {
                    if (mc.player.getDistance(blockPos.offset(f).getX(), blockPos.offset(f).getY(), blockPos.offset(f).getZ()) <= 5.0) {
                        return blockPos.offset(f);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    public void setRotations(final BlockPos blockPos) {
        final double[] rots = GuinnessRotationUtil.calculateLookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player);
        if (rotateMode.getValue() == RotateMode.Client) {
            if (this.phase != PlacePhase.FINISHED) {
                if (this.phase != PlacePhase.HOPPERGUI) {
                    GuinnessRotationUtil.rotateClient(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    return;
                }
            }
        }
        this.yaw = (float)rots[0];
        this.pitch = (float)rots[1];
        isRotating = true;
    }
    
    public void resetRotation() {
        this.yaw = mc.player.cameraYaw;
        this.pitch = mc.player.cameraPitch;
        isRotating = false;
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent tickEvent) {
        if (!this.isEnabled()) {
            return;
        }
        if (rotate.getValue()) {
            if (rotationPos != null) {
                this.setRotations(rotationPos);
            }
        }
        if (!isRotating) {
            return;
        }
        if (togglePitch) {
            final EntityPlayerSP player = mc.player;
            player.rotationPitch += Float.intBitsToFloat(Float.floatToIntBits(80354.22f) ^ 0x7E4D460B);
            togglePitch = false;
        }
        else {
            final EntityPlayerSP player2 = mc.player;
            player2.rotationPitch -= Float.intBitsToFloat(Float.floatToIntBits(25395.58f) ^ 0x7F17D03E);
            togglePitch = true;
        }
    }

    private enum PlaceMode {
        Auto,
        Aim
    }

    private enum PlaceType {
        Hopper,
        Dispenser,
        Furnace
    }

    private enum RotateMode {
        Server,
        Client
    }

    public enum PlacePhase
    {
        STARTING("STARTING", 0),
        DISPENSERGUI("DISPENSERGUI", 1),
        REDSTONE("REDSTONE", 2),
        HOPPER("HOPPER", 3),

        HOPPERGUI("HOPPERGUI", 4),
        FINISHED("FINISHED", 5);

        public static PlacePhase[] VALUES;


        PlacePhase(final String name, final int ordinal) {}

        static {
            PlacePhase.VALUES = new PlacePhase[] { PlacePhase.STARTING, PlacePhase.DISPENSERGUI, PlacePhase.REDSTONE, PlacePhase.HOPPER, PlacePhase.HOPPERGUI, PlacePhase.FINISHED };
        }
    }

}
