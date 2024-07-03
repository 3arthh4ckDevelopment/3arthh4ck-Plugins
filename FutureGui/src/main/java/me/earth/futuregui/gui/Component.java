package me.earth.futuregui.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.gui.components.Item;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Component implements Globals
{
    private Minecraft minecraft = Minecraft.getMinecraft();
    private final String name;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private int angle;
    private boolean open;
    public boolean drag;
    private final List<Item> items = new ArrayList<>();
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.angle = 180;
        this.width = 88;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    public void setupItems()
    {
        //For the child class
    }

    private void drag(int mouseX, int mouseY)
    {
        if (!drag)
        {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
        Render2DUtil.drawRect(x,  y - 1.5f, x + width,  y + height - 6, FutureColorUtil.getClientColorCustomAlpha(220));
        if (open) {
            Render2DUtil.drawRect(x, y + 12.5f, x + width, (y + height) + totalItemHeight, 0x77000000);
        }
        Managers.TEXT.drawStringWithShadow(name, x + 3.0f, y + 1.5f/* - 4.0f*/, -1); //15592941

        if (!open) {
            if (this.angle > 0) {
                this.angle -= 6;
            }
        } else if (this.angle < 180) {
            this.angle += 6;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        FutureColorUtil.glColor(new Color(255, 255, 255, 255));
        minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/future/arrow.png"));
        GlStateManager.translate(getX() + getWidth() - 7.0f, (getY() + 6) - 0.3F, 0.0F);
        GlStateManager.rotate(calculateRotation(angle), 0.0F, 0.0F, 1.0F);
        FutureGui.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if (this.open) {
            float y = (this.getY() + this.getHeight()) - 3.0f;
            for (Item item : getItems()) {
                item.setLocation(x + 2.0f, y);
                item.setWidth(getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5f;
            }
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            x2 = x - mouseX;
            y2 = y - mouseY;
            FutureGui.getInstance().getComponents().forEach(component ->
            {
                if (component.drag)
                {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY))
        {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton)
    {
        if (releaseButton == 0)
        {
            drag = false;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode)
    {
        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button)
    {
        items.add(button);
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
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

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isHidden()
    {
        return this.hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final List<Item> getItems()
    {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY)
    {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight()
    {
        float height = 0;
        for (Item item : getItems())
        {
            height += item.getHeight() + 1.5F;
        }
        return height;
    }

    public static float calculateRotation(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    public String getName()
    {
        return name;
    }

}
