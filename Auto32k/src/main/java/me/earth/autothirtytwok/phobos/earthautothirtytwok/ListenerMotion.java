package me.earth.autothirtytwok.phobos.earthautothirtytwok;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMotion extends ModuleListener<EarthAuto32k, MotionUpdateEvent> {

    public ListenerMotion(EarthAuto32k module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        module.onUpdateWalkingPlayer(event);
    }

}
