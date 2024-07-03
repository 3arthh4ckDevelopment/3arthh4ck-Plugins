package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import me.earth.futuregui.gui.FutureGuiScreen;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.LocalRandom;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class StringButton extends Button {
    private final StringSetting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(StringSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + width + 7.4F, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130));
        if (isListening)
            Managers.TEXT.drawStringWithShadow(context, currentString.string() + getIdleSign(), x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        else
            Managers.TEXT.drawStringWithShadow(context, (/*setting.shouldRenderName() ? setting.getName() + " " + TextColor.GRAY :*/ "") + setting.getValue(), x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (isListening && StringComponent.isAllowedCharacter(chr)) {
            setString(currentString.string() + chr);
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (isListening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE)
                return;
            else if (keyCode == GLFW.GLFW_KEY_ENTER)
                enterString();
            else if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
                setString(removeLastChar(currentString.string()));
            else if (keyCode == GLFW.GLFW_KEY_V && (Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL) || Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))) {
                try {
                    setString(currentString.string() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    private void enterString() {
        if(currentString.string().isEmpty())
            setting.setValue(setting.getInitial());
        else
            setting.setValue(currentString.string());

        setString("");
        super.onMouseClick();
    }

    @Override
    public void toggle()
    {
        isListening = !isListening;
    }

    @Override
    public boolean getState()
    {
        return !isListening;
    }

    public void setString(String newString)
    {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && !str.isEmpty())
        {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private final StopWatch idleTimer = new StopWatch();
    private boolean idling;
    public String getIdleSign() {
        if(idleTimer.passed(500))
        {
            idling = !idling;
            idleTimer.reset();
        }

        return idling ? "_" : "";
    }

    public record CurrentString(String string) { }
}
