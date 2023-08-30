package me.earth.autothirtytwok.guinness;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class SecretClose extends Module {

    private static final SecretClose INSTANCE = new SecretClose();

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Hopper));

    public static GuiScreen lastGui;
    
    public SecretClose() {
        super("SecretClose", Category.Misc);
        this.setData(new SimpleData(this, "Doesn't tell the server when you exit a GUI"));

        /*
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.currentScreen != null) {
                lastGui = mc.currentScreen;
            }
        }));
         */

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, e -> {
            if (e.getPacket() instanceof CPacketCloseWindow) {
                Label_0236: {
                    if (mode.getValue() == Mode.Hopper) {
                        if (!(lastGui instanceof GuiHopper)) {
                            if (!(mc.currentScreen instanceof GuiHopper)) {
                                break Label_0236;
                            }
                        }
                        e.setCancelled(true);
                        return;
                    }
                }
                if (mode.getValue() == Mode.All) {
                    e.setCancelled(true);
                }
            }
        }));
    }

    public static SecretClose getInstance() {
        return INSTANCE;
    }

    private enum Mode {
        Hopper,
        All
    }

}
