package me.earth.miogui.gui.click.buttons;

import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BooleanButton extends Button {

    private final BooleanSetting setting;
    private int progress;
    private boolean open;

    public BooleanButton(BooleanSetting setting) {
        super(setting.getName());
        progress = 0;
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;
        boolean dotgod = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        if (future) {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(99) : ColorUtil.getCurrentWithAlpha(120)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(55)));
            Managers.TEXT.drawStringWithShadow((newStyle ? getName().toLowerCase() : getName()), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);

        } else if (dotgod) {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(65) : ColorUtil.getCurrentWithAlpha(90)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(55)));
            Managers.TEXT.drawStringWithShadow((getName().toLowerCase()), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? ColorUtil.getCurrentGui(240) : 0xB0B0B0);

        } else {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200)) : (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            Managers.TEXT.drawStringWithShadow((newStyle ? getName().toLowerCase() : getName()), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);
        }

        /*
        if (setting.parent) {

            if (isOpen()) {
                ++progress;
            }

            if (future) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(new ResourceLocation("gear.png"));
                GlStateManager.translate(getX() + getWidth() - 6.7F + 8.0f, getY() + 7.7F - 0.3F, 0.0F);
                GlStateManager.rotate(calculateRotation((float) progress), 0.0F, 0.0F, 1.0F);
                RenderUtil.drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            } else {
                String color = (getState() || newStyle) ? "" : "" + ChatFormatting.GRAY;
                String gear = isOpen() ? "-" : "+";

                Managers.TEXT.drawStringWithShadow(color + gear,
                        x - 1.5f + (float) width - 7.4f + 8.0f,
                        y - 2.2f - (float) MioClickGui.INSTANCE.getTextOffset(), -1);
            }
        }

         */
    }

    @Override
    public void update() {
        setHidden(!setting.getVisibility());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            setOpen(!isOpen());
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    @Override
    public void toggle() {
        setting.setValue(!(setting.getValue()));
    }

    @Override
    public boolean getState() {
        return setting.getValue();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}

