package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.autothirtytwok.util.GuinnessRenderUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.awt.*;

public class ThreadAura extends Module {

    private static final ThreadAura INSTANCE = new ThreadAura();

    private final Setting<Boolean> renderTarget =
            register(new BooleanSetting("RenderTarget", true));
    private final Setting<Integer> threads =
            register(new NumberSetting<>("Threads", 3, 1, 10));
    private final Setting<Integer> attackIterations =
            register(new NumberSetting<>("AttackIterations", 5, 1, 30));

    public static EntityPlayer target;

    public ThreadAura() {
        super("ThreadAura", Category.Combat);

        this.setData(new SimpleData(this, "Aura that runs in a seperate thread to rip through larpers"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            onUpdate();
        }));

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, e -> {
            if (renderTarget.getValue() && target != null) {
                GuinnessRenderUtil.drawCsgoEspOutline(target, new Color(0, 250, 183, 255));
            }
        }));

        this.listeners.add(new LambdaListener<>(TotemPopEvent.class, e -> {
            if (target == null) {
                return;
            }
            if (e.getEntity() == target) {
                for (int t = 0; t < threads.getValue(); ++t) {
                    new Thread(() -> {
                        for (int i = 0; i < attackIterations.getValue(); ++i) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(target));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }).start();
                }
            }
        }));
    }

    public static ThreadAura getInstance() {
        return INSTANCE;
    }
    

    public void onUpdate() {
        if (!CheckUtil.holding32k(mc.player)) {
            return;
        }
        for (final EntityPlayer ep : mc.world.playerEntities) {
            if (ep == mc.player) {
                continue;
            }
            if (Managers.ENEMIES.contains(ep.getName())) {
                continue;
            }
            if (mc.player.getDistance(ep) <= 10.0 && ep.getHealth() > 0.0) {
                target = ep;
                mc.player.connection.sendPacket(new CPacketUseEntity(ep));
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            else {
                target = null;
            }
        }
    }

}
