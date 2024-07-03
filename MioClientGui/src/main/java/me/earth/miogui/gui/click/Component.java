package me.earth.miogui.gui.click;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.click.buttons.Button;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glColor4f;

public class Component extends Module {

    public static int[] counter1 = new int[]{1};

    private final ArrayList<Item> items = new ArrayList<>();

    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden;
    private int angle;

    public Map<Integer, Integer> colorMap = new HashMap<>();

    public Component(String name, int x, int y, boolean open) {
        super(name, Category.Client);
        angle = 180;
        this.x = x;
        this.y = y;
        width = 88;
        height = MioClickGui.CLICKGUI.get().getButtonHeight() + 3;
        this.open = open;
        setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    private void drawOutline(float thickness, int color) {
        float totalItemHeight = 0.0f;

        if (open) {
            totalItemHeight = getTotalItemHeight() - 2.0f;
        }

        Render2DUtil.drawLine(x, (float) y - 1.5f, x, (float) (y + height) + totalItemHeight, thickness, MioClickGui.CLICKGUI.get().rainbow.getValue() ? ColorUtil.getRainbow().getRGB() : color);
        Render2DUtil.drawLine(x + width, (float) y - 1.5f, x + width, (float) (y + height) + totalItemHeight, thickness, MioClickGui.CLICKGUI.get().rainbow.getValue() ? (ColorUtil.getRainbow().getRGB()) : color);
        Render2DUtil.drawLine(x, (float) y - 1.5f, x + width, (float) y - 1.5f, thickness, MioClickGui.CLICKGUI.get().rainbow.getValue() ? ColorUtil.getRainbow().getRGB() : color);
        Render2DUtil.drawLine(x, (float) (y + height) + totalItemHeight, x + width, (float) (y + height) + totalItemHeight, thickness, MioClickGui.CLICKGUI.get().rainbow.getValue() ? ColorUtil.rainbow(500).getRGB() : color);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drag(mouseX, mouseY);
        counter1 = new int[]{1};
        float totalItemHeight = open ? getTotalItemHeight() - 2.0f : 0.0f;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;

        int color = ColorUtil.toARGB(MioClickGui.CLICKGUI.get().color.getValue().getRed(), MioClickGui.CLICKGUI.get().color.getValue().getGreen(), MioClickGui.CLICKGUI.get().color.getValue().getBlue(), future ? 99 : 120);

        Gui.drawRect(x, y - 1, x + width, y + height - 6, MioClickGui.CLICKGUI.get().rainbow.getValue() ? ColorUtil.getCurrentWithAlpha(future ? 99 : 150) : color);

        if (future) {
            drawArrow();
        }

        if (open) {

            if (MioClickGui.CLICKGUI.get().line.getValue()) {
                if (MioClickGui.CLICKGUI.get().rainbow.getValue() && MioClickGui.CLICKGUI.get().rollingLine.getValue()) {

                    float hue = MioClickGui.CLICKGUI.get().rainbowDelay.getValue();
                    int height = mc.displayHeight;
                    float tempHue = hue;

                    for (int i2 = 0; i2 <= height; ++i2) {
                        colorMap.put(i2, Color.HSBtoRGB(tempHue, (float) MioClickGui.CLICKGUI.get().rainbowSaturation.getValue().intValue() / 255.0f, (float) MioClickGui.CLICKGUI.get().rainbowBrightness.getValue().intValue() / 255.0f));

                        tempHue += 1.0f / (float) height * (float) 5;
                    }

                    GL11.glLineWidth(1.0f);
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                    GL11.glBegin(1);

                    Color currentColor = new Color(ColorUtil.getCurrentWithAlpha(150));

                    GL11.glColor4f((currentColor.getRed() / 255.0f), (currentColor.getGreen() / 255.0f), (currentColor.getBlue() / 255.0f), (currentColor.getAlpha() / 255.0f));

                    GL11.glVertex3f((float)(x + width), ((float) y - 1.5f), 0.0f);
                    GL11.glVertex3f((float) x, ((float) y - 1.5f), 0.0f);
                    GL11.glVertex3f((float) x, ((float) y - 1.5f), 0.0f);

                    float currentHeight = (float) getHeight() - 1.5f;

                    for (Item item : getItems()) {
                        currentColor = MioClickGui.CLICKGUI.get().rainbowMode.getValue() != ClickGuiModule.Rainbow.NORMAL ?
                                ColorUtil.rainbow(MathUtil.clamp((int)((float) y + (currentHeight += (float)item.getHeight() + 1.5f)), 0, mc.displayHeight)) :
                                new Color(colorMap.get(MathUtil.clamp((int)((float) y + (currentHeight += (float)item.getHeight() + 1.5f)), 0, mc.displayHeight)));

                        GL11.glColor4f((currentColor.getRed() / 255.0f), (currentColor.getGreen() / 255.0f), (currentColor.getBlue() / 255.0f), (currentColor.getAlpha() / 255.0f));

                        GL11.glVertex3f((float) x, ((float) y + currentHeight), 0.0f);
                        GL11.glVertex3f((float) x, ((float) y + currentHeight), 0.0f);
                    }

                    currentColor = MioClickGui.CLICKGUI.get().rainbowMode.getValue() != ClickGuiModule.Rainbow.NORMAL ?
                            ColorUtil.rainbow(MathUtil.clamp((int)((float)(y + this.height) + totalItemHeight), 0, mc.displayHeight)) :
                            new Color(colorMap.get(MathUtil.clamp((int)((float)(y + this.height) + totalItemHeight), 0, mc.displayHeight)));

                    GL11.glColor4f((currentColor.getRed() / 255.0f), (currentColor.getGreen() / 255.0f), (currentColor.getBlue() / 255.0f), (currentColor.getAlpha() / 255.0f));

                    GL11.glVertex3f((float)(x + width), ((float)(y + this.height) + totalItemHeight), 0.0f);
                    GL11.glVertex3f((float)(x + width), ((float)(y + this.height) + totalItemHeight), 0.0f);

                    for (Item item : getItems()) {
                        currentColor = MioClickGui.CLICKGUI.get().rainbowMode.getValue() != ClickGuiModule.Rainbow.NORMAL ?
                                ColorUtil.rainbow(MathUtil.clamp((int)((float) y + (currentHeight -= (float)item.getHeight() + 1.5f)), 0, mc.displayHeight)) :
                                new Color(colorMap.get(MathUtil.clamp((int)((float) y + (currentHeight -= (float)item.getHeight() + 1.5f)), 0, mc.displayHeight)));

                        GL11.glColor4f((currentColor.getRed() / 255.0f), (currentColor.getGreen() / 255.0f), (currentColor.getBlue() / 255.0f), (currentColor.getAlpha() / 255.0f));

                        GL11.glVertex3f((float)(x + width), ((float) y + currentHeight), 0.0f);
                        GL11.glVertex3f((float)(x + width), ((float) y + currentHeight), 0.0f);
                    }

                    currentColor = new Color(ColorUtil.getCurrentWithAlpha(150));

                    GL11.glColor4f((currentColor.getRed() / 255.0f), (currentColor.getGreen() / 255.0f), (currentColor.getBlue() / 255.0f), (currentColor.getAlpha() / 255.0f));

                    GL11.glVertex3f((float)(x + width), (float) y - 1.5f, 0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel(GL11.GL_FLAT);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();

                } else {
                    drawOutline(1.0f, color);
                }
            }
            if (MioClickGui.CLICKGUI.get().rect.getValue()) {
                int rectColor = MioClickGui.CLICKGUI.get().colorRect.getValue() ? ColorUtil.getCurrentWithAlpha(30) : ColorUtil.toARGB(10, 10, 10, 30);

                Render2DUtil.drawRect(x, (float) y + 12.5f, x + width, (float) (y + height) + totalItemHeight, rectColor);
            }
        }

        Managers.TEXT.drawStringWithShadow(getName(), (float) x + 3.0f, (float) y - 4.6f - (float) MioClickGui.INSTANCE.getTextOffset(), -1);

        if (open) {
            float y = (float) (getY() + getHeight()) - 3.0f;

            for (Item item : getItems()) {
                counter1[0] = counter1[0] + 1;
                if (item.isHidden()) continue;
                item.setLocation((float) x + 2.0f, y);
                item.setHeight(MioClickGui.CLICKGUI.get().getButtonHeight());
                item.setWidth(getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += (float) item.getHeight() + 1.5f;
            }
        }
    }

    public void drawArrow() {
        if (!open) {
            if (angle > 0) {
                angle -= 6;
            }
        } else if (angle < 180) {
            angle += 6;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        glColor(new Color(255, 255, 255, 255));
        mc.getTextureManager().bindTexture(new ResourceLocation("arrow.png"));
        GlStateManager.translate(getX() + getWidth() - 7, (getY() + 6) - 0.3F, 0.0F);
        GlStateManager.rotate(calculateRotation(angle), 0.0F, 0.0F, 0.0F);
        ColorUtil.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            x2 = x - mouseX;
            y2 = y - mouseY;
            MioClickGui.INSTANCE.getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return;
        }
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            drag = false;
        }
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        items.add(button);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHiddenMio() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final ArrayList<Item> getItems() {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : getItems()) {
            height += (float) item.getHeight() + 1.5f;
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

    public static void glColor(Color color) {
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

}

