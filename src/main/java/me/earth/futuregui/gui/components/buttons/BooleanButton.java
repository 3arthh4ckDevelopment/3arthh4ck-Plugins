package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

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
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? RGBtoHEXColor(0, 0, 0, 90)  : RGBtoHEXColor(85, 85, 85, 130));
        Managers.TEXT.drawStringWithShadow(getName(), x + 2.0f, y + 4.0f, getState() ? -1 : -5592406);
    }

    @Override
    public void update()
    {
        setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
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
    public int getHeight()
    {
        return 14;
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
