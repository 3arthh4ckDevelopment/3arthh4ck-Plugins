package me.earth.autocat;
        
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class AutoCat implements Plugin {
    @Override
    public void load() {
        try {
            Managers.MODULES.register(AutoCatModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
