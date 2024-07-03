package me.earth.futuregui.gui.components;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.gui.DrawContext;

public class Item implements Globals {
    private final String name;
    protected float x, y;
    protected int width, height;
    private boolean hidden;

    public Item(String name)
    {
        this.name = name;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {}

    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {}

    public void mouseReleased(float mouseX, float mouseY, int releaseButton) {}

    public void update() {}

    public void charTyped(char chr, int modifiers) {}

    public void keyPressed(int keyCode) {}

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isHidden()
    {
        return !this.hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getName()
    {
        return name;
    }
}
