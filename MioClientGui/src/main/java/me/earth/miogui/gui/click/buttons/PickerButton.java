package me.earth.miogui.gui.click.buttons;

import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

public class PickerButton extends Button {
    
    ColorSetting setting;
    private Color finalColor;
    private boolean open;
    boolean pickingColor;
    boolean pickingHue;
    boolean pickingAlpha;

    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder builder = tessellator.getBuffer();

    public PickerButton(ColorSetting setting) {
        super(setting.getName());
        this.setting = setting;
        finalColor = setting.getValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;
        boolean dotgod = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f,
                (future || dotgod) ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(55)) : !isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);

        try {
            Render2DUtil.drawRect(x - 1.5f + (float) width + 0.6f - 0.5f, y + 5.0f, x + (float) width + 7.0f - 2.5f, y + (float) height - 4.0f, finalColor.getRGB());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Managers.TEXT.drawStringWithShadow(((newStyle || dotgod) ? getName().toLowerCase() : getName()), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), dotgod ? 0xB0B0B0 : -1);

        if (isOpen()) {
            drawPicker(setting, (int) x, (int) y + 15, (int) x, (int) y + 103, (int) x, (int) y + 95, mouseX, mouseY);
            Managers.TEXT.drawStringWithShadow("copy", x + 2.3f, y + 113.0f, isInsideCopy(mouseX, mouseY) ? -1 : -5592406);
            Managers.TEXT.drawStringWithShadow("paste", x + (float) width - 2.3f - Managers.TEXT.getStringWidth("paste") + 11.7f - 4.6f, y + 113.0f, isInsidePaste(mouseX, mouseY) ? -1 : -5592406);
            setting.setValue(finalColor);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if ((mouseButton == 1 || mouseButton == 0) && isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            setOpen(!isOpen());
        }
        if (mouseButton == 0 && isInsideCopy(mouseX, mouseY) && isOpen()) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            String hex = String.format("#%02x%02x%02x%02x", finalColor.getAlpha(), finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue());
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            ChatUtil.sendMessage("Copied the color to your clipboard.");
        }
        if (mouseButton == 0 && isInsidePaste(mouseX, mouseY) && isOpen()) {
            try {
                if (readClipboard() != null) {
                    if (Objects.requireNonNull(readClipboard()).startsWith("#")) {
                        String hex = Objects.requireNonNull(readClipboard());

                        int a = Integer.valueOf(hex.substring(1, 3), 16);
                        int r = Integer.valueOf(hex.substring(3, 5), 16);
                        int g = Integer.valueOf(hex.substring(5, 7), 16);
                        int b = Integer.valueOf(hex.substring(7, 9), 16);

                        /*
                        if (setting.hideAlpha) {
                            setting.setValue(new Color(r, g, b));
                        } else {

                         */
                        setting.setValue(new Color(r, g, b, a));
                        ChatUtil.sendMessage("Pasted the color to the client.");
                       // }
                    } else {
                        String[] color = readClipboard().split(",");
                        setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                    }
                }
            }
            catch (NumberFormatException e) {
                ChatUtil.sendMessage("Bad color format! Use Hex (#FFFFFFFF)");
            }
        }
    }

    @Override
    public void update() {
        setHidden(!setting.getVisibility());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        pickingAlpha = false;
        pickingHue = false;
        pickingColor = false;
    }

    public boolean isInsideCopy(int mouseX, int mouseY) {
        return mouseOver((int) ((int) x + 2.3f), (int) y + 113, (int) ((int) x + 2.3f) + Managers.TEXT.getStringWidth("copy"), (int)(y + 112.0f) + Managers.TEXT.getStringHeightI(), mouseX, mouseY);
    }

    public boolean isInsidePaste(int mouseX, int mouseY) {
        return mouseOver((int) (x + (float) width - 2.3f - Managers.TEXT.getStringWidth("paste") + 11.7f - 4.6f), (int) y + 113, (int) (x + (float) width - 2.3f - Managers.TEXT.getStringWidth("paste") + 11.7f - 4.6f) + Managers.TEXT.getStringWidth("paste"), (int)(y + 112.0f) + Managers.TEXT.getStringHeightI(), mouseX, mouseY);
    }

    public void drawPicker(ColorSetting setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float restrictedX;
        float[] color = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        try {
            color = new float[]{Color.RGBtoHSB((setting.getValue()).getRed(), (setting.getValue()).getGreen(), (setting.getValue()).getBlue(), null)[0], Color.RGBtoHSB((setting.getValue()).getRed(), (setting.getValue()).getGreen(), (setting.getValue()).getBlue(), null)[1], Color.RGBtoHSB((setting.getValue()).getRed(), (setting.getValue()).getGreen(), (setting.getValue()).getBlue(), null)[2], (float)(setting.getValue()).getAlpha() / 255.0f};
        }
        catch (Exception exception) {
            Earthhack.getLogger().info("mio color picker says it's a bad color!");
        }
        int pickerWidth = (int) (width + 7.4f);
        int pickerHeight = 78;
        int hueSliderWidth = pickerWidth + 3;
        int hueSliderHeight = 7;
        int alphaSliderHeight = 7;
        if (!(!pickingColor || Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))) {
            pickingColor = false;
        }
        if (!(!pickingHue || Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))) {
            pickingHue = false;
        }
        if (!(!pickingAlpha || Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))) {
            pickingAlpha = false;
        }
        if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) {
            pickingColor = true;
        }
        if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY)) {
            pickingHue = true;
        }
        if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY)) {
            pickingAlpha = true;
        }
        if (pickingHue) {
            restrictedX = Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
            color[0] = (restrictedX - (float)hueSliderX) / (float)hueSliderWidth;
        }
        if (pickingAlpha) {
            restrictedX = Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1.0f - (restrictedX - (float)alphaSliderX) / (float)pickerWidth;
        }
        if (pickingColor) {
            restrictedX = Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float)pickerX) / (float)pickerWidth;
            color[2] = 1.0f - (restrictedY - (float)pickerY) / (float)pickerHeight;
        }
        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);
        float selectedRed = (float)(selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (float)(selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (float)(selectedColor & 0xFF) / 255.0f;

        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        drawHueSlider(hueSliderX, hueSliderY, pickerWidth + 1, hueSliderHeight, color[0]);

        int cursorX = (int)((float)pickerX + color[1] * (float)pickerWidth);
        int cursorY = (int)((float)(pickerY + pickerHeight) - color[2] * (float)pickerHeight);

        //drawOutlineRect(cursorX - 2.2, cursorY - 2.2, cursorX + 2.2, cursorY + 2.2, Color.black, 0.1f);
        //Gui.drawRect((int) (cursorX - 2), (int) (cursorY - 2), (int) (cursorX + 2), (int) (cursorY + 2), (int) -1);
        if (pickingColor) {
            Render2DUtil.drawCircle((cursorX), (cursorY), 6.4f, Color.BLACK.getRGB());
            Render2DUtil.drawCircle((cursorX), (cursorY), 6, ColorUtil.toARGB(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), 255));
        } else {
            Render2DUtil.drawCircle((cursorX), (cursorY), 3.4f, Color.BLACK.getRGB());
            Render2DUtil.drawCircle((cursorX), (cursorY), 3, -1);
        }

        // if (!setting.hideAlpha) {
            drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth - 1, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        // }
        finalColor = getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    public static Color getColor(Color color, float alpha) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        return new Color(red, green, blue, alpha);
    }

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(9);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glDisable(3008);
        GL11.glBegin(9);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glEnable(3008);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    private float hueX;
    private float prevHueX;

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            Render2DUtil.drawRect(x, y, x + width, y + 4, -65536);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB((float)step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float)(step + 1) / 6.0f, 1.0f, 1.0f);
                drawGradientRect(x, (float)y + (float)step * ((float)height / 6.0f), x + width, (float)y + (float)(step + 1) * ((float)height / 6.0f), previousStep, nextStep, false);
                ++step;
            }
            int sliderMinY = (int)((float)y + (float)height * hue) - 4;
            Render2DUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
            drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, Color.BLACK, 1.0f);
        } else {
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB((float)step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float)(step + 1) / 6.0f, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6) + 3, y + height, previousStep, nextStep, true);
                ++step;
            }

            /*int sliderMinX = (int) ((float)width * hue);

            setHueX(sliderMinX);

            int sliderPosX = (int) getHueX() + x;

            RenderUtil.drawRect(sliderPosX - 1, y - 1.2f, sliderPosX + 1, y + height + 1.2f, -1);
            drawOutlineRect(sliderPosX - 1.2, y - 1.2, sliderPosX + 1.2, y + height + 1.2, Color.BLACK, 0.1f);
            */
            int sliderMinX = (int)((float)x + (float)width * hue);

            Render2DUtil.drawRect(sliderMinX - 1, y - 1.2f, sliderMinX + 1, y + height + 1.2f, -1);
            drawOutlineRect(sliderMinX - 1.2, y - 1.2, sliderMinX + 1.2, y + height + 1.2, Color.BLACK, 0.1f);

        }
    }

    public void setHueX(float x) {
        if (hueX == x) return;
        prevHueX = hueX;
        hueX = x;
    }

    public float getHueX() {
        if (ColorUtil.fps < 20) {
            return hueX;
        }
        hueX = prevHueX + (hueX - prevHueX) * mc.getRenderPartialTicks() / (8 * (Math.min(240, ColorUtil.fps) / 240f));
        return hueX;
    }

    private float alphaX;
    private float prevAlphaX;

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = height / 2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Render2DUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, -1);
                Render2DUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, -7303024);
                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    Render2DUtil.drawRect(minX, y, maxX, y + height, -7303024);
                    Render2DUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, -1);
                }
            }
            left = !left;
        }
        drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1.0f).getRGB(), 0);
        /*int sliderMinX = (int) ((float)width * alpha);
        //RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        //drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, Color.BLACK, 1.0f);

        setAlphaX(sliderMinX);

        int sliderPosX = (int) getAlphaX() + x;

        RenderUtil.drawRect(sliderPosX - 1, y - 1.2f, sliderPosX + 1, y + height + 1.2f, -1);
        drawOutlineRect(sliderPosX - 1.2, y - 1.2, sliderPosX + 1.2, y + height + 1.2, Color.BLACK, 0.1f);
         */
        int sliderMinX = (int)((float)(x + width) - (float)width * alpha);
        Render2DUtil.drawRect(sliderMinX - 1, y - 1.2f, sliderMinX + 1, y + height + 1.2f, -1);
        drawOutlineRect(sliderMinX - 1.2, y - 1.2, sliderMinX + 1.2, y + height + 1.2, Color.BLACK, 0.1f);

    }

    public void setAlphaX(float x) {
        if (alphaX == x) return;
        prevAlphaX = alphaX;
        alphaX = x;
    }

    public float getAlphaX() {
        if (ColorUtil.fps < 20) {
            return alphaX;
        }
        alphaX = prevAlphaX + (alphaX - prevAlphaX) * mc.getRenderPartialTicks() / (8 * (Math.min(240, ColorUtil.fps) / 240f));
        return alphaX;
    }

    public static void drawGradientRect(double leftpos, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(col1 & 0xFF) / 255.0f;
        float f5 = (float)(col2 >> 24 & 0xFF) / 255.0f;
        float f6 = (float)(col2 >> 16 & 0xFF) / 255.0f;
        float f7 = (float)(col2 >> 8 & 0xFF) / 255.0f;
        float f8 = (float)(col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(right, top, 0.0).color((float)(endColor >> 24 & 0xFF) / 255.0f, (float)(endColor >> 16 & 0xFF) / 255.0f, (float)(endColor >> 8 & 0xFF) / 255.0f, (float)(endColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos(left, top, 0.0).color((float)(startColor >> 16 & 0xFF) / 255.0f, (float)(startColor >> 8 & 0xFF) / 255.0f, (float)(startColor & 0xFF) / 255.0f, (float)(startColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos(left, bottom, 0.0).color((float)(startColor >> 16 & 0xFF) / 255.0f, (float)(startColor >> 8 & 0xFF) / 255.0f, (float)(startColor & 0xFF) / 255.0f, (float)(startColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos(right, bottom, 0.0).color((float)(endColor >> 24 & 0xFF) / 255.0f, (float)(endColor >> 16 & 0xFF) / 255.0f, (float)(endColor >> 8 & 0xFF) / 255.0f, (float)(endColor >> 24 & 0xFF) / 255.0f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            float startA = (float)(startColor >> 24 & 0xFF) / 255.0f;
            float startR = (float)(startColor >> 16 & 0xFF) / 255.0f;
            float startG = (float)(startColor >> 8 & 0xFF) / 255.0f;
            float startB = (float)(startColor & 0xFF) / 255.0f;
            float endA = (float)(endColor >> 24 & 0xFF) / 255.0f;
            float endR = (float)(endColor >> 16 & 0xFF) / 255.0f;
            float endG = (float)(endColor >> 8 & 0xFF) / 255.0f;
            float endB = (float)(endColor & 0xFF) / 255.0f;
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glShadeModel(7425);
            GL11.glBegin(9);
            GL11.glColor4f(startR, startG, startB, startA);
            GL11.glVertex2f((float)minX, (float)minY);
            GL11.glVertex2f((float)minX, (float)maxY);
            GL11.glColor4f(endR, endG, endB, endA);
            GL11.glVertex2f((float)maxX, (float)maxY);
            GL11.glVertex2f((float)maxX, (float)minY);
            GL11.glEnd();
            GL11.glShadeModel(7424);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
        } else {
            drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
        }
    }

    public static int gradientColor(int color, int percentage) {
        int r = ((color & 0xFF0000) >> 16) * (100 + percentage) / 100;
        int g = ((color & 0xFF00) >> 8) * (100 + percentage) / 100;
        int b = (color & 0xFF) * (100 + percentage) / 100;
        return new Color(r, g, b).hashCode();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor, boolean hovered) {
        if (hovered) {
            startColor = gradientColor(startColor, -20);
            endColor = gradientColor(endColor, -20);
        }
        float c = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float c1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float c2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float c3 = (float)(startColor & 0xFF) / 255.0f;
        float c4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float c5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float c6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float c7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, bottom, 0.0).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(c5, c6, c7, c4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static String readClipboard() {
        try {
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException | IOException exception) {
            return null;
        }
    }

    public static void drawOutlineRect(double left, double top, double right, double bottom, Color color, float lineWidth) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color.getRGB() >> 24 & 0xFF) / 255.0f;
        float f = (float)(color.getRGB() >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color.getRGB() >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color.getRGB() & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GL11.glPolygonMode(1032, 6913);
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPolygonMode(1032, 6914);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}