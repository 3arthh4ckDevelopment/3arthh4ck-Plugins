package me.earth.lawnmower;
        
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class Lawnmower implements Plugin {
    @Override
    public void load() {
        //nothing
    }

    @Override
    public void loadRuntime() {
        try {
            Managers.MODULES.register(new LawnmowerModule());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
