package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath
        extends ModuleListener<CrystalAura, DeathEvent> {
    public ListenerDeath(CrystalAura module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (event.getEntity().equals(ListenerDeath.mc.player)) {
            this.module.reset();
        }
    }
}

