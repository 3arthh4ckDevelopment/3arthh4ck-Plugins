package me.earth.autothirtytwok.phobos.earthautothirtytwok;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerKeyPress extends ModuleListener<EarthAuto32k, KeyboardEvent> {

    public ListenerKeyPress(EarthAuto32k module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        module.onKeyInput(event);
    }

}
