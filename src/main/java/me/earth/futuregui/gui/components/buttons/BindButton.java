package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button
{
    private final BindSetting setting;
    public boolean isListening;

    public BindButton(BindSetting setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + width + 7.4f, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130));
        if (isListening)
        {
            Managers.TEXT.drawStringWithShadow("Listening...", x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        else
        {
            Managers.TEXT.drawStringWithShadow(setting.getName() + " " + TextColor.GRAY + setting.getValue(), x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
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
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isListening)
        {
            Bind bind = Bind.fromKey(keyCode);
            if(bind.toString().equalsIgnoreCase("Escape"))
            {
                return;
            }
            else if(bind.toString().equalsIgnoreCase("Delete"))
            {
                bind = Bind.none();
            }

            setting.setValue(bind);
            super.onMouseClick();
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
        isListening = !isListening;
    }

    @Override
    public boolean getState()
    {
        return !isListening;
    }

}
