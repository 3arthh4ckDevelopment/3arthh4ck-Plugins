package me.earth.autothirtytwok.guinness;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class Teleport32k extends Module {

    private static final Teleport32k INSTANCE = new Teleport32k();

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Normal));
    
    public boolean hasbow;
    public boolean flag;
    
    public Teleport32k() {
        super("32kTeleport", Category.Combat);
        this.setData(new SimpleData(this, "Forces a rubberband in combination with longjump and blink"));
        flag = false;
        hasbow = false;
        
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            if (mode.getValue() == Mode.Bow && hasbow) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarItem(Items.BOW);
                if (mc.player.getItemInUseMaxCount() >= 5) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    mc.player.stopActiveHand();
                    hasbow = false;
                }
                else {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
            }
        }));
    }

    public static Teleport32k getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void onEnable() {
        flag = false;
        hasbow = false;
        if (mode.getValue() == Mode.Bow) {
            final int bow = InventoryUtil.findHotbarItem(Items.BOW);
            if (bow == -1) {
                Managers.CHAT.sendDeleteMessage("No bow found!", this.getName(), ChatIDs.MODULE);
                return;
            }
            hasbow = true;
        }
    }

    @Override
    public void onDisable() {
        flag = true;
    }
    
    private enum Mode {
        Normal,
        Bow
    }

}
