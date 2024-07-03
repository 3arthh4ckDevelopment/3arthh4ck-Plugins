package me.earth.hitboxdesync;
        
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class HitboxDesync implements Plugin {
    private final List<Module> moduleList = new ArrayList<>();

    @Override
    public void load() {
        try {
            Managers.MODULES.register(HitboxDesyncModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
