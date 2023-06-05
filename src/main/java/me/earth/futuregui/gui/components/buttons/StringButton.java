package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class StringButton extends Button
{

    private StringSetting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(StringSetting setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + width + 7.4F, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130));
        if(isListening)
            Managers.TEXT.drawStringWithShadow(currentString.getString() + getIdleSign(), x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        else
            Managers.TEXT.drawStringWithShadow((/*setting.shouldRenderName() ? setting.getName() + " " + TextColor.GRAY :*/ "") + setting.getValue(), x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
        {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isListening) {
            if (keyCode == Keyboard.KEY_ESCAPE)
                return;
            else if (keyCode == Keyboard.KEY_RETURN)
                enterString();
            else if (keyCode == Keyboard.KEY_BACK)
                setString(removeLastChar(currentString.getString()));
            else if (keyCode == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) {
                try {
                    setString(currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
                if(ChatAllowedCharacters.isAllowedCharacter(typedChar))
                    setString(currentString.getString() + typedChar);
        }
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    private void enterString()
    {
        if(currentString.getString().isEmpty())
            setting.setValue(setting.getInitial());
        else
            setting.setValue(currentString.getString());

        setString("");
        super.onMouseClick();
    }

    @Override
    public int getHeight()
    {
        return 14;
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

    public static String removeLastChar(String str)
    {
        String output = "";
        if (str != null && str.length() > 0)
        {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString
    {
        private final String string;

        public CurrentString(String string)
        {
            this.string = string;
        }

        public String getString()
        {
            return this.string;
        }
    }

    private final StopWatch idleTimer = new StopWatch();
    private boolean idling;
    public String getIdleSign()
    {
        if(idleTimer.passed(500))
        {
            idling = !idling;
            idleTimer.reset();
        }

        return idling ? "_" : "";
    }

}
