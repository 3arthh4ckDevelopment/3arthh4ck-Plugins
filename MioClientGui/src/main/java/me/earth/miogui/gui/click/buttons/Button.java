package me.earth.miogui.gui.click.buttons;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.gui.click.Component;
import me.earth.miogui.gui.click.Item;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item implements Globals {

    private boolean state;

    public Button(String name) {
        super(name);
        height = MioClickGui.CLICKGUI.get().getButtonHeight();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;
        boolean dotgod = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        if (newStyle) {
            Render2DUtil.drawRect(x, y, x + (float) width, y + (float) height - 0.5f, (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));

            Managers.TEXT.drawStringWithShadow(getName(),
                    x + 2.3f, y - 2.0f - (float) MioClickGui.INSTANCE.getTextOffset(),
                    getState() ? ColorUtil.getCurrentGui(240) : -1);

        } else if (dotgod) {
            Render2DUtil.drawRect(x, y, x + (float) width, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(65) : ColorUtil.getCurrentWithAlpha(90)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(35)));

            Managers.TEXT.drawStringWithShadow(getName(),
                    x + 2.3f, y - 2.0f - (float) MioClickGui.INSTANCE.getTextOffset(),
                    getState() ? ColorUtil.getCurrentGui(240) : 0xB0B0B0);

        } else if (future) {
            Render2DUtil.drawRect(x, y, x + (float) width, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(99) : ColorUtil.getCurrentWithAlpha(120)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(55)));

            Managers.TEXT.drawStringWithShadow(getName(),
                    x + 2.3f,
                    y - 2.0f - (float) MioClickGui.INSTANCE.getTextOffset(),
                    getState() ? -1 : -5592406);

        } else {
            Render2DUtil.drawRect(x, y, x + (float) width, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200)) : (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));

            Managers.TEXT.drawStringWithShadow(getName(),
                    x + 2.3f,
                    y - 2.0f - (float) MioClickGui.INSTANCE.getTextOffset(),
                    getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return state;
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : MioClickGui.INSTANCE.getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= getX() && (float) mouseX <= getX() + (float) getWidth() && (float) mouseY >= getY() && (float) mouseY <= getY() + (float) height;
    }
}

