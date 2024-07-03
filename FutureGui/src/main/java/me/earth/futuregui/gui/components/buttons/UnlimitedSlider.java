package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider<N extends Number> extends Button
{
    public NumberSetting<N> setting;

    public UnlimitedSlider(NumberSetting<N> setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(170) : FutureColorUtil.getClientColorCustomAlpha(230));
        Managers.TEXT.drawStringWithShadow(" - " + setting.getName() + " " + TextColor.GRAY + setting.getValue() + TextColor.RESET + " +", x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(isRight(mouseX)) {
                if (setting.getValue() instanceof Double || setting.getValue() instanceof Float) {
                    Double d = setting.getValue().doubleValue() + 0.1;
                    setting.setValue(setting.numberToValue(d));
                }
                else {
                    long l = setting.getValue().longValue() + 1;
                    setting.setValue(setting.numberToValue(l));
                }
            }
            else {
                if (setting.getValue() instanceof Double || setting.getValue() instanceof Float) {
                    Double d = setting.getValue().doubleValue() - 0.1;
                    setting.setValue(setting.numberToValue(d));
                }
                else {
                    long l = setting.getValue().longValue() - 1;
                    setting.setValue(setting.numberToValue(l));
                }
            }
        }
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    @Override
    public boolean getState()
    {
        return true;
    }

    public boolean isRight(int x)
    {
        return x > this.x + ((width + 7.4F) / 2);
    }

}

