package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.futuregui.gui.Component;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.gui.components.Item;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.LocalRandom;

import java.util.ArrayList;
import java.util.List;

import static me.earth.futuregui.gui.Component.calculateRotation;

public class ModuleButton extends Button {
    private final Module module;
    private final List<Item> settings = new ArrayList<>();
    private boolean subOpen;
    private int progress;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        initSettings();
        this.progress = 0;
    }

    public void initSettings() {
        List<Item> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting<?> setting : this.module.getSettings()) {
                if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled")) {
                    newItems.add(new BooleanButton((BooleanSetting) setting));
                }
                else if (setting instanceof BindSetting) {
                    newItems.add(new BindButton((BindSetting) setting));
                }
                else if (setting instanceof EnumSetting) {
                    newItems.add(new EnumButton<>((EnumSetting<?>) setting));
                }
                else if (setting instanceof NumberSetting) {
                    if (((NumberSetting<?>) setting).hasRestriction()) {
                        newItems.add(new Slider<>((NumberSetting<?>) setting));
                    }
                    else {
                        newItems.add(new UnlimitedSlider<>((NumberSetting<?>) setting));
                    }
                }
                else if (setting instanceof StringSetting) {
                    newItems.add(new StringButton((StringSetting) setting));
                }
            }
        }

        this.settings.clear();
        this.settings.addAll(newItems);
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        if (!this.settings.isEmpty()) {
            context.getMatrices().push();
            context.drawTexture(Component.GEAR, (int) (getX() + getWidth() - 6.7F), (int) (getY() + 7.7F - 0.3F), 0, 0, 10, 10, 10, 10, 10, 10);
            context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(calculateRotation((float) progress)));
            context.getMatrices().pop();
//            RenderSystem.enableBlend();
//            MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/future/gear.png"));
//            context.getMatrices().translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
//            context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(calculateRotation((float) progress)));
//            FutureGui.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
//            RenderSystem.disableBlend();

            if (subOpen) {
                float height = 1;
                ++progress;
                for (Item item : this.settings) {
                    if(item.isHidden()) {
                        height += 15F;
                        item.setLocation(x + 1, y + height);
                        item.setHeight(15);
                        item.setWidth(width - 9);
                        item.drawScreen(context, mouseX, mouseY, partialTicks);
                    }

                    item.update();
                }
            }
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.settings.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
                mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            }

            if (subOpen) {
                for (Item item : this.settings) {
                    if (item.isHidden()) {
                        item.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (!this.settings.isEmpty() && subOpen) {
            for (Item item : this.settings) {
                if (item.isHidden()) {
                    item.charTyped(chr, modifiers);
                }
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (!this.settings.isEmpty() && subOpen) {
            for (Item item : this.settings) {
                if (item.isHidden()) {
                    item.keyPressed(keyCode);
                }
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = 14;
            for (Item item : this.settings) {
                if (item.isHidden()) {
                    height += item.getHeight() + 1;
                }
            }

            return height + 2;
        }
        else {
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