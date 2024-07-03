package me.earth.futuregui.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Item;
import me.earth.futuregui.gui.components.buttons.ModuleButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FutureGuiScreen extends Screen {
    private static FutureGuiScreen instance;

    private final List<Component> components = new ArrayList<>();

    public FutureGuiScreen() {
        super(Text.of("FutureGui"));
        instance = this;
        load();
    }

    public static FutureGuiScreen getInstance() {
        if (instance == null) {
            instance = new FutureGuiScreen();
        }

        return instance;
    }

    public void load() {
        int x = -84;
        for(Category category : Category.values()) {
            components.add(new Component(category.getName(), x += 90, 4, true) {
                @Override
                public void setupItems() {
                    Managers.MODULES.getRegistered().forEach(module -> {
                        if (module.getCategory() == category) {
                            addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }

        components.forEach(components -> components.getItems().sort(Comparator.comparing(Item::getName)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Window window = MinecraftClient.getInstance().getWindow();
        RenderSystem.enableBlend();
        Render2DUtil.drawGradientRect(context.getMatrices(), 0.0F, 0.0F, window.getWidth(), window.getHeight(), true, 536870912, -1879048192);
        RenderSystem.disableBlend();

        components.forEach(components -> components.drawScreen(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            components.forEach(component -> component.setY(component.getY() - 10));
        }
        else if (verticalAmount > 0) {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        components.forEach(components -> components.mouseClicked((float) mouseX, (float) mouseY, mouseButton));
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        components.forEach(components -> components.mouseReleased((float) mouseX, (float) mouseY, mouseButton));
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        components.forEach(frame -> frame.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        components.forEach(frame -> frame.keyPressed(keyCode));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public List<Component> getComponents()
    {
        return components;
    }

    public int getTextOffset()
    {
        return -6;
    }

}
