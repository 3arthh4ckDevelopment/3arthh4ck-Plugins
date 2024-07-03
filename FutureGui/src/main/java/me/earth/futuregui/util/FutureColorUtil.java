package me.earth.futuregui.util;

import me.earth.futuregui.FutureGuiModule;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FutureColorUtil
{

    public static int toRGBA(int r, int g, int b, int a)
    {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static int toRGBA(float r, float g, float b, float a)
    {
        return toRGBA((int) (r * 255.f), (int) (g * 255.f), (int) (b * 255.f), (int) (a * 255.f));
    }
    public static int RGBtoHEXColor(int r, int g, int b, int a)
    {
        return ((a & 0xff) << 24) |
                ((r & 0xff) << 16) |
                ((g & 0xff) << 8) |
                (b & 0xff);
    }

    public static int toRGBA(float[] colors)
    {
        if(colors.length != 4) throw new IllegalArgumentException("colors[] must have a length of 4!");
        return toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(double[] colors)
    {
        if(colors.length != 4) throw new IllegalArgumentException("colors[] must have a length of 4!");
        return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static int toRGBA(Color color)
    {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int[] toRGBAArray(int colorBuffer)
    {
        return new int[]
        {
                (colorBuffer >> 16 & 255),
                (colorBuffer >> 8 & 255),
                (colorBuffer & 255),
                (colorBuffer >> 24 & 255)
        };
    }

    public static class HueCycler
    {

        public int index = 0;
        public int[] cycles;

        public HueCycler(int cycles)
        {
            if (cycles<=0) throw new IllegalArgumentException("cycles <= 0");
            this.cycles = new int[cycles];
            double hue = 0;
            double add = 1/(double)cycles;
            for (int i = 0; i < cycles; i++)
            {
                this.cycles[i] = Color.HSBtoRGB((float) hue, 1,1);
                hue += add;
            }
        }

        public void reset()
        {
            index = 0;
        }

        public void reset(int index)
        {
            this.index = index;
        }

        public int next()
        {
            int a = cycles[index];
            index++;
            if (index >= cycles.length) index = 0;
            return a;
        }

        public void setNext()
        {
            int rgb = next();
        }

        public void set()
        {
            int rgb = cycles[index];
            float red = ((rgb >> 16) & 0xFF)/255f;
            float green = ((rgb >> 8) & 0xFF)/255f;
            float blue = (rgb & 0xFF)/255f;
            GL11.glColor3f(red, green, blue);
        }

        public void setNext(float alpha)
        {
            int rgb = next();
            float red = ((rgb >> 16) & 0xFF)/255f;
            float green = ((rgb >> 8) & 0xFF)/255f;
            float blue = (rgb & 0xFF)/255f;
            GL11.glColor4f(red, green, blue, alpha);
        }

        public int current()
        {
            return cycles[index];
        }
    }

    public static int getClientColorCustomAlpha(int alpha) {
        Color color = setAlpha(new Color(Color.HSBtoRGB((FutureGuiModule.getInstance().hue.getValue() / 360.f), FutureGuiModule.getInstance().saturation.getValue() / 100f, FutureGuiModule.getInstance().lightness.getValue() / 100f)), alpha);
        return color.getRGB();
    }

    public static void glColor(Color color) {
        GL11.glColor4f(((float)color.getRed() / 255.0f), ((float)color.getGreen() / 255.0f), ((float)color.getBlue() / 255.0f), ((float)color.getAlpha() / 255.0f));
    }

    public static Color setAlpha(Color color, int alpha) {
        alpha = MathHelper.clamp(alpha, 0, 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

}
