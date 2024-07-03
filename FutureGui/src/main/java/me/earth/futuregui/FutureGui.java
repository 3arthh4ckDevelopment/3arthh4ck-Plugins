package me.earth.futuregui;

import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class FutureGui implements Plugin {

    @Override
    public void load() {
        // Nothing
    }

    @Override
    public void loadRuntime() {
        try {
            Managers.MODULES.register(FutureGuiModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }
}
