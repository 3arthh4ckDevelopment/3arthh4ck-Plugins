package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton<E extends Enum<E>> extends Button
{
    private final EnumSetting<E> setting;

    public EnumButton(EnumSetting<E> setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130));
        Managers.TEXT.drawStringWithShadow(setting.getName() + " " + TextColor.GRAY + setting.getValue().name(), x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
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
        setting.setValue((E) EnumHelper.next(setting.getValue()));
    }

    @Override
    public boolean getState()
    {
        return true;
    }

}
