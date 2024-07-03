package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.Component;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import org.lwjgl.input.Mouse;

import static me.earth.futuregui.util.FutureColorUtil.RGBtoHEXColor;

public class Slider<N extends Number> extends Button
{

    public NumberSetting<N> setting;
    private final Number min;
    private final Number max;
    private final int difference;

    public Slider(NumberSetting<N> setting)
    {
        super(setting.getName());
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        dragSetting(mouseX, mouseY);
        Render2DUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? RGBtoHEXColor(0, 0, 0, 90)  : RGBtoHEXColor(85, 85, 85, 130));
        Render2DUtil.drawRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7.4F) * partialMultiplier(), y + height, !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170));
        Managers.TEXT.drawStringWithShadow(getName() + " " + TextColor.GRAY + (setting.getValue() instanceof Float ? (setting.getValue()) : (setting.getValue()).doubleValue()), x + 2.3F, y - 1.7F - FutureGui.getInstance().getTextOffset(), 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
            setSettingFromX(mouseX);
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : FutureGui.getInstance().getComponents()) {
            if (component.drag)
                return false;
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8 && mouseY >= getY() && mouseY <= getY() + height;
    }

    @Override
    public void update()
    {
        setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    private void dragSetting(int mouseX, int mouseY)
    {
        if(isHovering(mouseX, mouseY) && Mouse.isButtonDown(0))
            setSettingFromX(mouseX);
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    private void setSettingFromX(int mouseX)
    {
        double percent = (mouseX - x) / (width + 7.4F);
        double result = setting.getMin().doubleValue() + (difference * percent);
        setting.setValue(setting.numberToValue(result));
    }

    private float middle()
    {
        return max.floatValue() - min.floatValue();
    }

    private float part()
    {
        return setting.getValue().floatValue() - min.floatValue();
    }

    private float partialMultiplier()
    {
        return part() / middle();
    }
}
