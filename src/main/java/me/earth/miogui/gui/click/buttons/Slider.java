package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import me.earth.miogui.gui.click.Component;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class Slider extends Button {

    private final Number min;
    private final Number max;
    private final int difference;
    public NumberSetting<Number> setting;
    private float renderWidth;
    private float prevRenderWidth;

    public Slider(NumberSetting<Number> setting) {
        super(setting.getName());
        this.setting = setting;
        min = setting.getMin();
        max = setting.getMax();
        difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;
        boolean dotgod = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        dragSetting(mouseX, mouseY);
        setRenderWidth(x + ((float) width + 7.4f) * partialMultiplier());
        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, !isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);

        if (future) {
            Render2DUtil.drawRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : getRenderWidth(), y + (float) height - 0.5f, (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(99) : ColorUtil.getCurrentWithAlpha(120)));

        } else if (dotgod) {
            Render2DUtil.drawRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : getRenderWidth(), y + (float) height - 0.5f, (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(65) : ColorUtil.getCurrentWithAlpha(90)));

        } else {
            if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
                drawHGradientRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : getRenderWidth(), y + (float) height - 0.5f, ColorUtil.pulseColor(new Color(MioClickGui.CLICKGUI.get().color.getValue().getRed(), MioClickGui.CLICKGUI.get().color.getValue().getGreen(), MioClickGui.CLICKGUI.get().color.getValue().getBlue(), 200), 50, 1).getRGB(), ColorUtil.pulseColor(new Color(MioClickGui.CLICKGUI.get().color.getValue().getRed(), MioClickGui.CLICKGUI.get().color.getValue().getGreen(), MioClickGui.CLICKGUI.get().color.getValue().getBlue(), 200), 50, 1000).getRGB());

            } else {
                Render2DUtil.drawRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : getRenderWidth(), y + (float) height - 0.5f, !isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200));
            }
            Render2DUtil.drawLine(x + 1, y, x + 1, y + (float) height - 0.5f, 0.9f, ColorUtil.getCurrentWithAlpha(255));
        }

        if (dotgod) {
            Managers.TEXT.drawStringWithShadow((getName().toLowerCase() + ":") + " " + ChatFormatting.GRAY + (setting.getValue() instanceof Float ? setting.getValue() : Double.valueOf((setting.getValue()).doubleValue())), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), ColorUtil.getCurrentGui(240));

        } else {
            Managers.TEXT.drawStringWithShadow((newStyle ? getName().toLowerCase() + ":" : getName()) + " " + ChatFormatting.GRAY + (setting.getValue() instanceof Float ? setting.getValue() : Double.valueOf((setting.getValue()).doubleValue())), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : MioClickGui.INSTANCE.getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= getX() && (float) mouseX <= getX() + (float) getWidth() + 8.0f && (float) mouseY >= getY() && (float) mouseY <= getY() + (float) height;
    }

    @Override
    public void update() {
        setHidden(!setting.getVisibility());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - x) / ((float) width + 7.4f);
        if (setting.getValue() instanceof Double) {
            double result = (Double) setting.getMin() + (double) ((float) difference * percent);
            setting.setValue((double) Math.round(10.0 * result) / 10.0);
        } else if (setting.getValue() instanceof Float) {
            float result = (setting.getMin()).floatValue() + (float) difference * percent;
            setting.setValue(Float.valueOf((float) Math.round(10.0f * result) / 10.0f));
        } else if (setting.getValue() instanceof Integer) {
            setting.setValue((Integer) setting.getMin() + (int) ((float) difference * percent));
        }
    }

    private float middle() {
        return max.floatValue() - min.floatValue();
    }

    private float part() {
        return (setting.getValue()).floatValue() - min.floatValue();
    }

    private float partialMultiplier() {
        return part() / middle();
    }

    /**
     * @credit cattyn
     */

    public void setRenderWidth(float renderWidth) {
        if (this.renderWidth == renderWidth) return;
        prevRenderWidth = this.renderWidth;
        this.renderWidth = renderWidth;
    }

    public float getRenderWidth() {
        if (ColorUtil.fps < 20) {
            return renderWidth;
        }
        renderWidth = prevRenderWidth + (renderWidth - prevRenderWidth) * mc.getRenderPartialTicks() / (8 * (Math.min(240, ColorUtil.fps) / 240f));
        return renderWidth;
    }

    public static void drawHGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, top, 0).color(f1, f2, f3, f4).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(f1, f2, f3, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, top, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

}

