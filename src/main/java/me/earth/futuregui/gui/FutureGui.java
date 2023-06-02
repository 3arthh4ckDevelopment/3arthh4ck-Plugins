package me.earth.futuregui.gui;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.components.Item;
import me.earth.futuregui.gui.components.buttons.ModuleButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FutureGui extends GuiScreen
{
    private static FutureGui instance;

    private final List<Component> components = new ArrayList<>();

    public FutureGui()
    {
        instance = this;
        load();
    }

    public static FutureGui getInstance()
    {
        if (instance == null)
        {
            instance = new FutureGui();
        }

        return instance;
    }

    public void load()
    {
        int x = -84;
        for(Category category : Category.values())
        {
            components.add(new Component(category.name(), x += 90, 4, true)
            {
                @Override
                public void setupItems()
                {
                    Managers.MODULES.getRegistered().forEach(module ->
                    {
                        if (module.getCategory() == category)
                        {
                            addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }

        components.forEach(components -> components.getItems().sort(Comparator.comparing(Item::getName)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        checkMouseWheel();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Render2DUtil.drawGradientRect(0.0F, 0.0F, mc.displayWidth, mc.displayHeight, true,536870912, -1879048192);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton)
    {
        components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton)
    {
        components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    //TODO: implement mouse movement
    private void checkMouseWheel()
    {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0)
        {
            components.forEach(component -> component.setY(component.getY() - 10));
        }
        else if (dWheel > 0)
        {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
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
