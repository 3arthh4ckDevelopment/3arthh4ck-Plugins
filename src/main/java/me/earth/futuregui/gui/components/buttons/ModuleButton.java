package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.futuregui.FutureGuiModule;
import me.earth.futuregui.gui.FutureGui;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.gui.components.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static me.earth.futuregui.gui.Component.calculateRotation;

public class ModuleButton extends Button
{
    private final Module module;
    private final List<Item> settings = new ArrayList<>();
    private boolean subOpen;
    private int progress;

    public ModuleButton(Module module)
    {
        super(module.getName());
        this.module = module;
        initSettings();
        this.progress = 0;
    }

    public void initSettings()
    {
        List<Item> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty())
        {
            for (Setting<?> setting : this.module.getSettings())
            {
                if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled"))
                {
                    newItems.add(new BooleanButton((BooleanSetting) setting));
                }
                else if (setting instanceof BindSetting)
                {
                    newItems.add(new BindButton((BindSetting) setting));
                }
                else if (setting instanceof EnumSetting)
                {
                    newItems.add(new EnumButton<>((EnumSetting<?>) setting));
                }
                else if (setting instanceof NumberSetting)
                {
                    if (((NumberSetting<?>) setting).hasRestriction())
                    {
                        newItems.add(new Slider<>((NumberSetting<?>) setting));
                    }
                    else
                    {
                        newItems.add(new UnlimitedSlider<>((NumberSetting<?>) setting));
                    }
                }
                else if (setting instanceof StringSetting)
                {
                    newItems.add(new StringButton((StringSetting) setting));
                }
            }
        }

        this.settings.clear();
        this.settings.addAll(newItems);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.settings.isEmpty())
        {
            FutureGuiModule gui = FutureGuiModule.getInstance();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/future/gear.png"));
            GlStateManager.translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
            GlStateManager.rotate(calculateRotation((float) progress), 0.0F, 0.0F, 1.0F);
            FutureGui.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();            if (subOpen)
            {
                float height = 1;
                ++progress;
                for (Item item : this.settings)
                {
                    if(item.isHidden())
                    {
                        height += 15F;
                        item.setLocation(x + 1, y + height);
                        item.setHeight(15);
                        item.setWidth(width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }

                    item.update();
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.settings.isEmpty())
        {
            if (mouseButton == 1 && isHovering(mouseX, mouseY))
            {
                subOpen = !subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            if (subOpen)
            {
                for (Item item : this.settings)
                {
                    if (item.isHidden())
                    {
                        item.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.settings.isEmpty() && subOpen)
        {
            for (Item item : this.settings)
            {
                if(item.isHidden())
                {
                    item.onKeyTyped(typedChar, keyCode);
                }
            }
        }
    }

    @Override
    public int getHeight()
    {
        if (subOpen)
        {
            int height = 14;
            for (Item item : this.settings)
            {
                if (item.isHidden())
                {
                    height += item.getHeight() + 1;
                }
            }

            return height + 2;
        }
        else
        {
            return 14;
        }
    }

    public Module getModule()
    {
        return this.module;
    }

    @Override
    public void toggle()
    {
        module.toggle();
    }

    @Override
    public boolean getState()
    {
        return module.isEnabled();
    }

}