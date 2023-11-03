package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.gui.click.Component;
import me.earth.miogui.gui.click.Item;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {

    private static final ModuleCache<FontMod> CUSTOM_FONT = Caches.getModule(FontMod.class);

    private final Module module;
    private List<Item> items = new ArrayList<>();
    private boolean subOpen;
    private int progress;

    public ModuleButton(Module module) {
        super(module.getName());
        progress = 0;
        this.module = module;
        initSettings();
    }

    @SuppressWarnings("unchecked")
    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<>();
        if (!module.getSettings().isEmpty()) {
            for (Setting<?> setting : module.getSettings()) {
                if (setting.getComplexity().shouldDisplay(setting) || setting.getVisibility()) {
                    if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled")) {
                        newItems.add(new BooleanButton((BooleanSetting) setting));
                    }
                    if (setting instanceof BindSetting && !setting.getName().equalsIgnoreCase("bind")) {
                        newItems.add(new BindButton((BindSetting) setting));
                    }
                    if ((setting instanceof StringSetting) && !setting.getName().equalsIgnoreCase("displayName")) {
                        newItems.add(new StringButton((StringSetting) setting));
                    }
                    if (setting instanceof ColorSetting) {
                        newItems.add(new PickerButton((ColorSetting) setting));
                    }
                    if (setting instanceof NumberSetting) {
                        newItems.add(new Slider((NumberSetting<Number>) setting));
                        continue;
                    }
                    if (!(setting instanceof EnumSetting)) continue;
                    newItems.add(new EnumButton<>((EnumSetting<?>) setting));
                }
            }
        }
        newItems.add(new BindButton((BindSetting) module.getSetting("Bind")));
        items = newItems;
    } //TODO: list and page settings settings!!

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!items.isEmpty()) {

            drawGear();

            if (subOpen) {

                ++progress;

                float height = 1.0f;
                for (Item item : items) {
                    me.earth.miogui.gui.click.Component.counter1[0] = Component.counter1[0] + 1;
                    if (!item.isHidden()) {
                        item.setLocation(x + 1.0f, y + (height += MioClickGui.CLICKGUI.get().getButtonHeight()));
                        item.setHeight(MioClickGui.CLICKGUI.get().getButtonHeight());
                        item.setWidth(width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);

                        if (item instanceof PickerButton && ((PickerButton)item).isOpen()) {
                            height += 110.0f;
                        }

                        if (item instanceof EnumButton && ((EnumButton<?>)item).isOpen()) {
                            height += ((EnumButton<?>) item).setting.getValue().getClass().getEnumConstants().length * 12;
                        }
                    }
                    item.update();
                }
            }
        }

        if (isHovering(mouseX, mouseY) && MioClickGui.CLICKGUI.get().isEnabled()) {

            String description = ChatFormatting.GRAY + module.getData().getDescription();

            Gui.drawRect(0, mc.currentScreen.height - 11, Managers.TEXT.getStringWidth(description) + 2, mc.currentScreen.height, ColorUtil.injectAlpha(new Color(-1072689136), 200).getRGB());

            assert mc.currentScreen != null;
            Managers.TEXT.drawStringWithShadow(description, 2, mc.currentScreen.height - 10, -1);
        }
    }

    public void drawGear() {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;

        if (MioClickGui.CLICKGUI.get().gear.getValue()) {

            if (future) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(new ResourceLocation("gear.png"));
                GlStateManager.translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
                GlStateManager.rotate(Component.calculateRotation((float) progress), 0.0F, 0.0F, 1.0F);
                ColorUtil.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            } else {
                String color = (module.isEnabled() || newStyle) ? "" : "" + ChatFormatting.GRAY;
                String gear = subOpen ? "-" : "+";
                float x = this.x - 1.5f + (float) width - 7.4f;

                Managers.TEXT.drawStringWithShadow(color + gear,
                        x + ((CUSTOM_FONT.get().isEnabled() && gear.equals("-")) ? 1.0f : 0.0f),
                        y - 2.2f - (float) MioClickGui.INSTANCE.getTextOffset()
                        , -1);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!items.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (subOpen) {
                for (Item item : items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!items.isEmpty() && subOpen) {
            for (Item item : items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
            for (Item item : items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;

                if (item instanceof PickerButton && ((PickerButton) item).isOpen()) {
                    height += 110;
                }
                if (item instanceof EnumButton && ((EnumButton) item).isOpen()) {
                    height += ((EnumButton) item).setting.getValue().getClass().getEnumConstants().length * 12;
                }
            }
            return height + 2;
        }
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void toggle() {
        module.toggle();
    }

    @Override
    public boolean getState() {
        return module.isEnabled();
    }

}

