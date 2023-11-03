package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button {
    public Setting setting;

    public UnlimitedSlider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;

        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, !isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200));
        Render2DUtil.drawLine(x + 1, y, x + 1, y + (float) height - 0.5f, 0.9f, ColorUtil.getCurrentWithAlpha(255));
        Managers.TEXT.drawStringWithShadow(" - " + (newStyle ? setting.getName().toLowerCase() + ":" : setting.getName()) + " " + ChatFormatting.GRAY + setting.getValue() + ChatFormatting.WHITE + " +", x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (isRight(mouseX)) {
                if (setting.getValue() instanceof Double) {
                    setting.setValue((Double) setting.getValue() + 1.0);
                } else if (setting.getValue() instanceof Float) {
                    setting.setValue(Float.valueOf(((Float) setting.getValue()).floatValue() + 1.0f));
                } else if (setting.getValue() instanceof Integer) {
                    setting.setValue((Integer) setting.getValue() + 1);
                }
            } else if (setting.getValue() instanceof Double) {
                setting.setValue((Double) setting.getValue() - 1.0);
            } else if (setting.getValue() instanceof Float) {
                setting.setValue(Float.valueOf(((Float) setting.getValue()).floatValue() - 1.0f));
            } else if (setting.getValue() instanceof Integer) {
                setting.setValue((Integer) setting.getValue() - 1);
            }
        }
    }

    @Override
    public void update() {
        setHidden(!setting.getVisibility());
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
    }

    @Override
    public boolean getState() {
        return true;
    }

    public boolean isRight(int x) {
        return (float) x > this.x + ((float) width + 7.4f) / 2.0f;
    }
}

