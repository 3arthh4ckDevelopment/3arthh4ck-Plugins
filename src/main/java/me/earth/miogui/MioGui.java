package me.earth.miogui;
        
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class MioGui implements Plugin {
    @Override
    public void load() {
        try {
            Managers.MODULES.register(ClickGuiModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
