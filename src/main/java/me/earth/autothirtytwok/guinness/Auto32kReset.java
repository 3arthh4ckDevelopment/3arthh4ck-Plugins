package me.earth.autothirtytwok.guinness;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;

public class Auto32kReset extends Module {

    private static final Auto32kReset INSTANCE = new Auto32kReset();

    private static final ModuleCache<NewAuto32k> AUTO_32K =
            Caches.getModule(NewAuto32k.class);


    public int ticks;
    
    public Auto32kReset() {
        super("Auto32kReset", Category.Combat);
        this.ticks = 0;

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            onUpdate();
        }));
    }

    public static Auto32kReset getInstance() {
        return INSTANCE;
    }

    public void onUpdate() {
        if (AUTO_32K.get().dispenserFuckedUp) {
            ++ticks;
            if (ticks == 20) {
                AUTO_32K.get().dispenserFuckedUp = false;
                AUTO_32K.enable();
            }
        }
    }
    
    @Override
    public void onDisable() {
        this.ticks = 0;
    }
}
