package me.earth.miogui;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.configs.ConfigModule;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.miogui.gui.MioClickGui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGuiModule extends Module {

    private static final ClickGuiModule INSTANCE = new ClickGuiModule();

    //General settings

    public final Setting<Boolean> guiMove =
            register(new BooleanSetting("GuiMove", true));

    //Appearance

    public final Setting<Style> style =
            register(new EnumSetting<>("Style", Style.NEW));

    public final Setting<Integer> height =
            register(new NumberSetting<>("ButtonHeight", 4, 1, 5));

    public final Setting<Boolean> blur =
            register(new BooleanSetting("Blur", false));

    public final Setting<Boolean> line =
            register(new BooleanSetting("Line", true));
    public final Setting<Boolean> rollingLine =
            register(new BooleanSetting("RollingLine", true));

    public final Setting<Boolean> rect =
            register(new BooleanSetting("Rect", true));
    public final Setting<Boolean> colorRect =
            register(new BooleanSetting("ColorRect", false));

    public final Setting<Boolean> gear =
            register(new BooleanSetting("Gear", true));

    //All guis things

    public final Setting<Boolean> particles =
            register(new BooleanSetting("Particles", true));
    public final Setting<Boolean> colorParticles =
            register(new BooleanSetting("ColorParticles", true));

    public final Setting<Boolean> background =
            register(new BooleanSetting("Background", true));

    //Colors

    public final Setting<Color> color =
            register(new ColorSetting("Color", new Color(125, 125, 213)));

    public final Setting<Boolean> rainbow =
            register(new BooleanSetting("Rainbow", false));
    public final Setting<Rainbow> rainbowMode =
            register(new EnumSetting<>("Mode", Rainbow.NORMAL));
    public final Setting<Float> rainbowBrightness =
            register(new NumberSetting<>("Brightness ", 150.0f, 1.0f, 255.0f));
    public final Setting<Float> rainbowSaturation =
            register(new NumberSetting<>("Saturation", 150.0f, 1.0f, 255.0f));
    public final Setting<Color> secondColor =
            register(new ColorSetting("SecondColor", new Color(255, 255, 255)));
    public final Setting<Integer> rainbowDelay =
            register(new NumberSetting<>("Delay", 240, 0, 600));

    private final KeyBinding[] keys;
    private GuiScreen screen;

    public ClickGuiModule() {
        super("MioGui", Category.Client);

        keys = new KeyBinding[] {
                        mc.gameSettings.keyBindForward,
                        mc.gameSettings.keyBindBack,
                        mc.gameSettings.keyBindLeft,
                        mc.gameSettings.keyBindRight,
                        mc.gameSettings.keyBindJump,
                        mc.gameSettings.keyBindSprint
        };

        this.listeners.add(new LambdaListener<>(Render2DEvent.class, e -> {
            if (!(mc.currentScreen instanceof MioClickGui))
                disable();
        }));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (guiMove.getValue() && !(mc.currentScreen instanceof GuiChat)) {
                for (KeyBinding key : keys) {
                    if (key.getKeyCode() < 0) continue;

                    KeyBinding.setKeyBindState(key.getKeyCode(), Keyboard.isKeyDown(key.getKeyCode()));
                }

            } else {
                for (KeyBinding key : keys) {
                    if(key.getKeyCode() < 0) continue;

                    if (!Keyboard.isKeyDown(key.getKeyCode())) {
                        KeyBinding.setKeyBindState(key.getKeyCode(), false);
                    }
                }
            }
        }));
    }

    public static ClickGuiModule getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        disableOtherGuis();
        screen = mc.currentScreen instanceof MioClickGui ? ((MioClickGui) mc.currentScreen).screen : mc.currentScreen;
        MioClickGui gui = newClick();
        mc.displayGuiScreen(gui);
        if (blur.getValue()) {
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }

    protected void disableOtherGuis() {
        for (Module module : Managers.MODULES.getRegistered()) {
            if (module instanceof ClickGui || module instanceof HudEditor) {
                module.disable();
            }
        }
    }

    public void onDisable() {
        mc.displayGuiScreen(screen);
    }

    protected MioClickGui newClick() {
        return new MioClickGui(screen);
    }

    public int getButtonHeight() {
        return 11 + height.getValue();
    }

    public enum Rainbow {
        NORMAL,
        PLAIN,
        DOUBLE
    }

    public enum Style {
        OLD,
        NEW,
        FUTURE,
        DOTGOD
    }

 }


