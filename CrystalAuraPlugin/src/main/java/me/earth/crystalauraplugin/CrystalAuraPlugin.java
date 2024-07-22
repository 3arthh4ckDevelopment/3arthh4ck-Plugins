package me.earth.crystalauraplugin;

import me.earth.crystalauraplugin.module.CrystalAura;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class CrystalAuraPlugin implements Plugin {

    @Override
    public void load() {
        //nothing
    }

    @Override
    public void loadRuntime() {
        try {
            Managers.MODULES.register(new CrystalAura());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
