package me.earth.crystalauraplugin.module;

import me.earth.crystalauraplugin.module.modes.AutoSwitch;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;

final class ListenerKeys
        extends ModuleListener<CrystalAura, KeyboardEvent> {
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);

    public ListenerKeys(CrystalAura module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getEventState() && this.module.autoSwitch.getValue() == AutoSwitch.Bind && event.getKey() == this.module.switchBind.getValue().getKey()) {
            if (this.module.isPingBypass()) {
                OFFHAND.computeIfPresent(o -> {
                    if (OffhandMode.CRYSTAL.equals(o.getMode())) {
                        o.setMode(OffhandMode.TOTEM);
                    } else {
                        o.setMode(OffhandMode.CRYSTAL);
                    }
                });
                return;
            }
            this.module.setSwitching(!this.module.isSwitching());
            if (!this.module.isSwitching() && this.module.switchBack.getValue().booleanValue() && ListenerKeys.mc.player != null && ListenerKeys.mc.player.getOffHandStack().getItem() == net.minecraft.item.Items.END_CRYSTAL) {
                OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.TOTEM));
            }
        }
    }
}

