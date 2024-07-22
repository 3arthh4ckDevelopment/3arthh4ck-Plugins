package me.earth.crystalauraplugin.module;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLogout
        extends ModuleListener<CrystalAura, DisconnectEvent> {
    public ListenerLogout(CrystalAura module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        this.module.reset();
    }
}

