package me.earth.futuregui.gui.components;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.Component;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item
{

    private boolean state;

    public Button(String name)
    {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(x, y, x + width, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(245) : FutureColorUtil.getClientColorCustomAlpha(215)) : (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130)));
        Managers.TEXT.drawStringWithShadow(getName(), x + 2.0f, y + 4.0f, getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            onMouseClick();
        }
    }

    public void onMouseClick()
    {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void toggle() {}

    public boolean getState()
    {
        return state;
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY)
    {
        for (Component component : FutureGui.getInstance().getComponents())
        {
            if (component.drag)
            {
                return false;
            }
        }

        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
