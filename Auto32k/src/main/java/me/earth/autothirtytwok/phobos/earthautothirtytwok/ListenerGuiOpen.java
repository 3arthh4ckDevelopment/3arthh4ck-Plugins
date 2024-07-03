package me.earth.autothirtytwok.phobos.earthautothirtytwok;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGuiOpen extends ModuleListener<EarthAuto32k, GuiScreenEvent<?>> {

    public ListenerGuiOpen(EarthAuto32k module) {
        super(module, GuiScreenEvent.class);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        module.onGui(event);
    }

}
