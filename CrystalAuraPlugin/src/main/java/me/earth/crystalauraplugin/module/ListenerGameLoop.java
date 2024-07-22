package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.Rotate;
import me.earth.crystalauraplugin.module.modes.ThreadMode;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerGameLoop
        extends ModuleListener<CrystalAura, GameLoopEvent> {
    public ListenerGameLoop(CrystalAura module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (this.module.multiThread.getValue().booleanValue() && !this.module.isPingBypass()) {
            if (this.module.rotate.getValue() != Rotate.None) {
                if (mc.getTickDelta() >= this.module.partialT.getValue().floatValue() && this.module.canTick()) {
                    this.module.setTick(true);
                    this.module.runThread();
                }
            } else if (this.module.threadMode.getValue() == ThreadMode.Delay && this.module.threadTimer.passed(this.module.threadDelay.getValue().intValue())) {
                this.module.runNonRotateThread(ThreadMode.Delay);
                this.module.threadTimer.reset();
            } else if (this.module.threadMode.getValue() == ThreadMode.Server && Managers.TICK.getTickTimeAdjustedForServerPackets() >= (long) this.module.tickThreshold.getValue().intValue() && Managers.TICK.getTickTimeAdjustedForServerPackets() <= (long) this.module.maxTick.getValue().intValue() && this.module.serverTimer.passed(this.module.serverDelay.getValue().intValue())) {
                this.module.runNonRotateThread(ThreadMode.Server);
                this.module.serverTimer.reset();
            }
        }
    }
}

