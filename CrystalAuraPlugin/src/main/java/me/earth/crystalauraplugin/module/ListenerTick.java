package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.ThreadMode;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick
        extends ModuleListener<CrystalAura, TickEvent> {
    public ListenerTick(CrystalAura module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        this.module.setTick(false);
        this.module.checkKilled();
        this.module.runNonRotateThread(ThreadMode.Tick);
    }
}

