package me.earth.futuregui;
        
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FutureGui implements Plugin {
    private final List<Module> moduleList = new ArrayList<>();

    @Override
    public void load() {
        try {
            Managers.MODULES.register(FutureGuiModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
