package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.ThreadMode;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPostKeys
        extends ModuleListener<CrystalAura, KeyboardEvent.Post> {
    public ListenerPostKeys(CrystalAura module) {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event) {
        this.module.runNonRotateThread(ThreadMode.Keys);
    }
}

