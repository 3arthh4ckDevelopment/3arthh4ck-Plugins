package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {
    private final StringSetting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(StringSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200)) : (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        if (isListening) {
            Managers.TEXT.drawStringWithShadow(currentString.getString() + "_", x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);
        } else {
            Managers.TEXT.drawStringWithShadow((setting.getName().equals("Buttons") ? "Buttons " : (setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "")) + setting.getValue(), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (isListening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    enterString();
                }
                case 14: {
                    setString(removeLastChar(currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                setString(currentString.getString() + typedChar);
            }
        }
    }

    @Override
    public void update() {
        setHidden(!setting.getVisibility());
    }

    private void enterString() {
        if (currentString.getString().isEmpty()) {
            setting.setValue(setting.getInitial());
        } else {
            setting.setValue(currentString.getString());
        }
        setString("");
        onMouseClick();
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    @Override
    public void toggle() {
        isListening = !isListening;
    }

    @Override
    public boolean getState() {
        return !isListening;
    }

    public void setString(String newString) {
        currentString = new CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }
}

