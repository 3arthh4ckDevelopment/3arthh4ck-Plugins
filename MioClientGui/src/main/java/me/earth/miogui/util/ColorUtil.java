package me.earth.miogui.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.click.Component;
import me.earth.miogui.gui.MioClickGui;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ColorUtil implements Globals {

    public static int fps = mc.gameSettings.limitFramerate;
    private static final Color current = new Color(-1);

    public static boolean isRainbow() {
        return Managers.COLOR.getRainbowSpeed().getValue() != 0;
    }
    
    public static Color getCurrent() {
        if (isRainbow()) {
            return getRainbow();
        }
        return current;
    }

    public static int getCurrentWithAlpha(int alpha) {
        if (isRainbow()) {
            return ColorUtil.toRGBA(ColorUtil.injectAlpha(getRainbow(), alpha));
        }
        return ColorUtil.toRGBA(ColorUtil.injectAlpha(getCurrent(), alpha));
    }

    public static int getCurrentGui(int alpha) {
        if (isRainbow()) {
            return ColorUtil.rainbow(Component.counter1[0] * Managers.COLOR.getRainbowSpeed().getValue()).getRGB();
        }
        return ColorUtil.toRGBA(ColorUtil.injectAlpha(current, alpha));
    }

    public static Color getRainbow() {
        return ColorUtil.rainbow(Managers.COLOR.getRainbowSpeed().getValue());
    }

    public static int toRGBA(Color color) {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);

        if (MioClickGui.CLICKGUI.get().rainbowMode.getValue() == ClickGuiModule.Rainbow.DOUBLE) {
            return gradientColor(MioClickGui.CLICKGUI.get().color.getValue(),
                    MioClickGui.CLICKGUI.get().secondColor.getValue(),
                    Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) 20 / (float) (((delay / 15) * 2) + 10) * 2.0F) % 2.0F - 1.0F));

        } else if (MioClickGui.CLICKGUI.get().rainbowMode.getValue() == ClickGuiModule.Rainbow.PLAIN) {
            return pulseColor(MioClickGui.CLICKGUI.get().color.getValue(), 50, delay);

        } else {
            return Color.getHSBColor((float) (rainbowState % 360.0 / 360.0), MioClickGui.CLICKGUI.get().rainbowSaturation.getValue() / 255.0f, MioClickGui.CLICKGUI.get().rainbowBrightness.getValue() / 255.0f);
        }
    }

    public static Color pulseColor(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs((System.currentTimeMillis() % ((long)1230675006 ^ 0x495A9BEEL) / Float.intBitsToFloat(Float.floatToIntBits(0.0013786979f) ^ 0x7ECEB56D) + index / (float)count * Float.intBitsToFloat(Float.floatToIntBits(0.09192204f) ^ 0x7DBC419F)) % Float.intBitsToFloat(Float.floatToIntBits(0.7858098f) ^ 0x7F492AD5) - Float.intBitsToFloat(Float.floatToIntBits(6.46708f) ^ 0x7F4EF252));
        brightness = Float.intBitsToFloat(Float.floatToIntBits(18.996923f) ^ 0x7E97F9B3) + Float.intBitsToFloat(Float.floatToIntBits(2.7958195f) ^ 0x7F32EEB5) * brightness;
        hsb[2] = brightness % Float.intBitsToFloat(Float.floatToIntBits(0.8992331f) ^ 0x7F663424);
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color gradientColor(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }

        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static void drawModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
    }

}

