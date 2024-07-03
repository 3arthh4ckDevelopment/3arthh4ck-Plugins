package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.FutureGuiScreen;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.LocalRandom;

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
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(170) : FutureColorUtil.getClientColorCustomAlpha(230));
        Managers.TEXT.drawStringWithShadow(context, " - " + setting.getName() + " " + TextColor.GRAY + setting.getValue() + TextColor.RESET + " +", x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
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
    public boolean getState()
    {
        return true;
    }

    public boolean isRight(float x)
    {
        return x > this.x + ((width + 7.4F) / 2);
    }

}

