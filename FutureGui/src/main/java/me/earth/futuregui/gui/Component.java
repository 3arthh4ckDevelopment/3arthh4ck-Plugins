package me.earth.futuregui.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.gui.components.Item;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.LocalRandom;

import java.util.ArrayList;
import java.util.List;

public class Component implements Globals {
    public static final Identifier ARROW = new Identifier("textures/future/arrow.png");
    public static final Identifier GEAR = new Identifier("textures/future/gear.png");

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

    public Component(String name, int x, int y, boolean open) {
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

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {

        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
        Render2DUtil.drawRect(context.getMatrices(), x,  y - 1.5f, x + width,  y + height - 6, FutureColorUtil.getClientColorCustomAlpha(220));
        if (open) {
            Render2DUtil.drawRect(context.getMatrices(), x, y + 12.5f, x + width, (y + height) + totalItemHeight, 0x77000000);
        }
        Managers.TEXT.drawStringWithShadow(context, name, x + 3.0f, y + 1.5f/* - 4.0f*/, -1); //15592941

        if (!open) {
            if (this.angle > 0) {
                this.angle -= 6;
            }
        } else if (this.angle < 180) {
            this.angle += 6;
        }

        context.getMatrices().push();
        context.drawTexture(ARROW, (int) (getX() + getWidth() - 7.0f), (int) ((getY() + 6) - 0.3), 0, 0, 10, 10, 10, 10, 10, 10);
        context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(calculateRotation(angle)));
        context.getMatrices().pop();

//        RenderSystem.enableBlend();
//        FutureColorUtil.glColor(context, new Color(255, 255, 255, 255));
//        minecraft.getTextureManager().bindTexture(new Identifier("textures/future/arrow.png"));
//        context.getMatrices().translate(getX() + getWidth() - 7.0f, (getY() + 6) - 0.3F, 0.0F);
//        context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(calculateRotation(angle)));
//        FutureGui.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
//        RenderSystem.disableBlend();

        if (this.open) {
            float y = (this.getY() + this.getHeight()) - 3.0f;
            for (Item item : getItems()) {
                item.setLocation(x + 2.0f, y);
                item.setWidth(getWidth() - 4);
                item.drawScreen(context, mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5f;
            }
        }

    }

    public void mouseClicked(float mouseX, float mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            x2 = x - (int) mouseX;
            y2 = y - (int) mouseY; //TODO: int --> float?
            FutureGuiScreen.getInstance().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
            mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            return;
        }

        if (!open) {
            return;
        }

        getItems().forEach(item -> {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    public void mouseReleased(float mouseX, float mouseY, int releaseButton) {
        if (releaseButton == 0) {
            drag = false;
        }

        if (!open) {
            return;
        }

        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void charTyped(char chr, int modifiers) {
        if (!open) {
            return;
        }

        getItems().forEach(item -> item.charTyped(chr, modifiers));
    }

    public void keyPressed(int keyCode) {
        if (!open) {
            return;
        }

        getItems().forEach(item -> item.keyPressed(keyCode));
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

    private boolean isHovering(float mouseX, float mouseY)
    {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0;
        for (Item item : getItems()) {
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
