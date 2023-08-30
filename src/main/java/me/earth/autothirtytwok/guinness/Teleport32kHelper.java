package me.earth.autothirtytwok.guinness;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;

public class Teleport32kHelper extends Module {

    private static final Teleport32kHelper INSTANCE = new Teleport32kHelper();

    private final Setting<Integer> delay =
            register(new NumberSetting<>("Ticks-Delay", 4));

    public int ticks;
    public boolean flag;
    
    public Teleport32kHelper() {
        super("Teleport32kHelper", Category.Combat);
        this.setData(new SimpleData(this, "Helper for 32kTeleport"));
        ticks = 0;
        
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            if (flag) {
                ++ticks;
                if (ticks == (double) delay.getValue()) {
                    ++ticks;
                    Teleport32kHelper.mc.player.motionY = 4.0;
                    disable();
                }
            }
        }));
    }

    public static Teleport32kHelper getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void onDisable() {
        ticks = 0;
        flag = false;
    }
}
