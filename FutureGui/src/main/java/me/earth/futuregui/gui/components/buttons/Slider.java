package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.Component;
import me.earth.futuregui.gui.FutureGuiScreen;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;

public class Slider<N extends Number> extends Button {
    private final NumberSetting<N> setting;
    private final Number min;
    private final Number max;
    private final int difference;
    private boolean mouseDown = false;

    public Slider(NumberSetting<N> setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? FutureColorUtil.RGBtoHEXColor(0, 0, 0, 90) : FutureColorUtil.RGBtoHEXColor(85, 85, 85, 130));
        Render2DUtil.drawRect(context.getMatrices(), x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7.4F) * partialMultiplier(), y + height, !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170));
        Managers.TEXT.drawStringWithShadow(context, getName() + " " + TextColor.GRAY + (setting.getValue() instanceof Float ? (setting.getValue()) : (setting.getValue()).doubleValue()), x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            setSettingFromX(mouseX);
            if (mouseButton == 0)
                mouseDown = true;
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
            mouseDown = false;
    }

    @Override
    public boolean isHovering(float mouseX, float mouseY) {
        for (Component component : FutureGuiScreen.getInstance().getComponents()) {
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

    private void dragSetting(float mouseX, float mouseY) {
        if (isHovering(mouseX, mouseY) && mouseDown)
            setSettingFromX(mouseX);
    }

    private void setSettingFromX(float mouseX) {
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
