package me.ai24.voicecontrol;

import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;
import me.ai24.voicecontrol.module.VoiceControlModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class VoiceControl implements Plugin {

    public static final Logger LOGGER = LogManager.getLogger("VoiceControl");

    @Override
    public void load() {
        // nothing
    }

    @Override
    public void loadRuntime() {
        try {
            Managers.MODULES.register(new VoiceControlModule());
        } catch (AlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }
}
