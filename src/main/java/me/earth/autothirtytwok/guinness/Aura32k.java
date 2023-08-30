package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

public class Aura32k extends Module {

    private static final Aura32k INSTANCE = new Aura32k();

    private final Setting<Integer> range =
            register(new NumberSetting<>("Range", 10, 1, 15));

    public Aura32k() {
        super("32kAura", Category.Combat);
        this.setData(new SimpleData(this, "High CPS killaura for 32k weapons"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            if (!CheckUtil.is32k(mc.player.inventory.getCurrentItem())) {
                return;
            }
            for (final EntityPlayer ep : mc.world.playerEntities) {
                if (ep == mc.player) {
                    continue;
                }
                if (Managers.FRIENDS.contains(ep.getName())) {
                    continue;
                }
                if (mc.player.getDistance(ep) > range.getValue()) {
                    continue;
                }
                if (ep.getHealth() <= 0.0) {
                    continue;
                }
                mc.player.connection.sendPacket(new CPacketUseEntity(ep));
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }));
    }

    public static Aura32k getInstance() {
        return INSTANCE;
    }

}
