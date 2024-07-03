package me.earth.miogui.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.click.Component;
import me.earth.miogui.gui.click.Item;
import me.earth.miogui.gui.click.buttons.ModuleButton;
import me.earth.miogui.util.Particle;
import me.earth.miogui.util.Snow;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Random;

public class MioClickGui extends GuiScreen {

    public static MioClickGui INSTANCE;
    Minecraft mc = Minecraft.getMinecraft();

    public static final ModuleCache<ClickGuiModule> CLICKGUI = Caches.getModule(ClickGuiModule.class);

    private final ArrayList<Snow> snow = new ArrayList<>();
    private final Particle.Util particles = new Particle.Util(300);
    private final ArrayList<Component> components = new ArrayList<>();
    public final GuiScreen screen;

    public MioClickGui(GuiScreen screen) {
        this.screen = screen;
        onLoad();
    }

    private void onLoad() {
        for (Module m : Managers.MODULES.getRegistered())
            updateModule(m);

        INSTANCE = this;

        int x = -84;

        for (Category category : Category.values()) {
            components.add(new Component(category.getName(), x += 90, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    Managers.MODULES.getModulesFromCategory(category).forEach(module -> addButton(new ModuleButton(module)));
                }
            });
        }

        components.forEach(components -> components.getItems().sort(Comparator.comparing(Item::getName)));

        Random random = new Random();

        for (int i = 0; i < 100; ++i) {

            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                this.snow.add(snow);
            }
        }
    }

    public void updateModule(Module module) {
        for (Component component : components) {

            for (Item item : component.getItems()) {

                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        checkMouseWheel();

        if (mc.world != null) {
            drawDefaultBackground();
        } else {
            Gui.drawRect(0, 0, 1920, 1080, ColorUtil.injectAlpha(new Color(-1072689136), 150).getRGB());
        }

        if (CLICKGUI.get().background.getValue() && mc.currentScreen instanceof MioClickGui && mc.world != null) {
            drawVGradientRect(0, 0, (float) mc.displayWidth, (float) mc.displayHeight, new Color(0, 0, 0, 0).getRGB(), ColorUtil.getCurrentWithAlpha(60));
        }

        if (CLICKGUI.get().particles.getValue()) {
            particles.drawParticles();
        }

        components.forEach(components ->  components.drawScreen(mouseX, mouseY, partialTicks));

        ScaledResolution res = new ScaledResolution(mc);

        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        if (!snow.isEmpty() && (month == 12 || month == 1 || month == 2)) {
            snow.forEach(snow -> snow.drawSnow(res));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();

        if (dWheel < 0) {
            components.forEach(component -> component.setY(component.getY() - 10));

        } else if (dWheel > 0) {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (mc.entityRenderer.isShaderActive()) {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    public static void drawVGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
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
        bufferbuilder.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

}

