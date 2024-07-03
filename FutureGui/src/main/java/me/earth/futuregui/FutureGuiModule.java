package me.earth.futuregui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.futuregui.gui.FutureGui;

public class FutureGuiModule extends Module
{
    private static final FutureGuiModule INSTANCE = new FutureGuiModule();
    public Setting<Integer> hue =
            register(new NumberSetting<>("Hue",100, 0,360));
    public Setting<Integer> saturation =
            register(new NumberSetting<>("Saturation",90, 0,100));
    public Setting<Integer> lightness =
            register(new NumberSetting<>("Lightness",45, 0,100));


    public FutureGuiModule()
    {
        super("FutureGui", Category.Client);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent tickEvent)
            {
                if (!(mc.currentScreen instanceof FutureGui))
                {
                    disable();
                }
            }
        });
    }

    public static FutureGuiModule getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void onEnable()
    {
        FutureGui gui = new FutureGui();
        System.out.println(mc + "");
        mc.displayGuiScreen(gui);
    }

    @Override
    protected void onDisable()
    {
        if (mc.currentScreen instanceof FutureGui)
        {
            mc.displayGuiScreen(null);
        }
    }

}
