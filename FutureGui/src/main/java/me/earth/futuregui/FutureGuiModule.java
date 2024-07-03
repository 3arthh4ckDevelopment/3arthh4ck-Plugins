package me.earth.futuregui;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.futuregui.gui.FutureGuiScreen;

public class FutureGuiModule extends Module {
    private static final FutureGuiModule INSTANCE = new FutureGuiModule();

    public final Setting<Integer> hue =
            register(new NumberSetting<>("Hue",100, 0,360));
    public final Setting<Integer> saturation =
            register(new NumberSetting<>("Saturation",90, 0,100));
    public final Setting<Integer> lightness =
            register(new NumberSetting<>("Lightness",45, 0,100));

    public FutureGuiModule() {
        super("FutureGui", Category.Client);
        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (!(mc.currentScreen instanceof FutureGuiScreen)) {
                disable();
            }
        }));
    }

    public static FutureGuiModule getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onEnable() {
        FutureGuiScreen gui = new FutureGuiScreen();
        mc.setScreen(gui);
    }

    @Override
    protected void onDisable() {
        if (mc.currentScreen instanceof FutureGuiScreen) {
            mc.setScreen(null);
        }
    }

}
