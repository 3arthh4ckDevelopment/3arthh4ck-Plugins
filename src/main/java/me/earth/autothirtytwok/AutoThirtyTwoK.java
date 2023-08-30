package me.earth.autothirtytwok;

import me.earth.autothirtytwok.guinness.*;
import me.earth.autothirtytwok.phobos.phoboscriticals.PhobosCriticals;
import me.earth.autothirtytwok.phobos.earthautothirtytwok.EarthAuto32k;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class AutoThirtyTwoK implements Plugin {

    @Override
    public void load() {
        try {
            Managers.MODULES.register(EarthAuto32k.getInstance());
            Managers.MODULES.register(PhobosCriticals.getInstance());

            Managers.MODULES.register(Anti32k.getInstance());
            Managers.MODULES.register(Aura32k.getInstance());
            Managers.MODULES.register(NewAuto32k.getInstance());
            Managers.MODULES.register(Auto32kReset.getInstance());
            Managers.MODULES.register(Block32k.getInstance());
            Managers.MODULES.register(HopperNuker.getInstance());
            Managers.MODULES.register(HopperRadius.getInstance());
            Managers.MODULES.register(SecretClose.getInstance());
            Managers.MODULES.register(Info32k.getInstance());
            Managers.MODULES.register(Manual32k.getInstance());
            Managers.MODULES.register(Teleport32k.getInstance());
            Managers.MODULES.register(Teleport32kHelper.getInstance());
            Managers.MODULES.register(ThreadAura.getInstance());
            Managers.MODULES.register(Throw32k.getInstance());

        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
