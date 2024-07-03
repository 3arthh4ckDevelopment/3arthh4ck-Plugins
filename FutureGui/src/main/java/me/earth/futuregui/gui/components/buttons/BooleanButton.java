package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.LocalRandom;

import static me.earth.futuregui.util.FutureColorUtil.RGBtoHEXColor;

public class BooleanButton extends Button
{
    private final Setting<Boolean> setting;

    public BooleanButton(Setting<Boolean> setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + (float) width + 7.4f, y + (float) height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? RGBtoHEXColor(0, 0, 0, 90)  : RGBtoHEXColor(85, 85, 85, 130));
        Managers.TEXT.drawStringWithShadow(context, getName(), x + 2.0f, y + 4.0f, getState() ? -1 : -5592406);
    }

    @Override
    public void update()
    {
        setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
        {
            mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        }
    }

    @Override
    public void toggle()
    {
        setting.setValue(!setting.getValue());
    }

    @Override
    public boolean getState()
    {
        return setting.getValue();
    }

}
