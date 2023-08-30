package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.GuinnessRotationUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class HopperNuker extends Module {

    private static final HopperNuker INSTANCE = new HopperNuker();

    private final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", false));
    private final Setting<Mode> mode =
            register(new EnumSetting<>("Break-Mode", Mode.Vanilla));
    private final Setting<Double> range =
            register(new NumberSetting<>("Range", 6.0, 1.0, 9.0));

    public boolean mining;
    public int prevSlot;
    public BlockPos pos;
    
    public HopperNuker() {
        super("HopperNuker", Category.Combat);
        this.setData(new SimpleData(this, "Automatically mines hoppers around you"));
        this.prevSlot = -1;
        this.mining = false;

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            onUpdate();
        }));
    }

    public static HopperNuker getInstance() {
        return INSTANCE;
    }

    public void onUpdate() {
        final BlockPos pos = this.findHoppers();
        if (pos != null) {
            if (!this.mining) {
                this.prevSlot = mc.player.inventory.currentItem;
                this.mining = true;
            }
            if (rotate.getValue()) {
                GuinnessRotationUtil.rotateClient(pos.getX(), pos.getY(), pos.getZ());
            }
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack != ItemStack.EMPTY) {
                    if (stack.getItem() instanceof ItemPickaxe) {
                        newSlot = i;
                        break;
                    }
                }
            }
            if (newSlot != -1) {
                mc.player.inventory.currentItem = newSlot;
            }
            if (mode.getValue() != Mode.Packet) {
                mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            else {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, mc.player.getHorizontalFacing()));
            }
        }
        else if (this.prevSlot != -1) {
            mc.player.inventory.currentItem = this.prevSlot;
            this.prevSlot = -1;
            this.mining = false;
        }
    }
    
    public BlockPos findHoppers() {
        pos = null;
        mc.world.loadedTileEntityList.stream()
                .filter(tileEntity -> tileEntity instanceof TileEntityHopper)
                .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= range.getValue())
                .sorted(Comparator.comparing(HopperNuker::getDistance))
                .forEach(this::setBlock);
        return pos;
    }
    
    public void setBlock(final TileEntity tileEntity) {
        pos = new BlockPos(tileEntity.getPos());
    }
    
    public static double getDistance(final TileEntity tileEntity) {
        return mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
    }

    private enum Mode {
        Packet,
        Vanilla
    }

}
