package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.Objects;

public class EnumButton<E extends Enum<E>> extends Button {
    public EnumSetting<E> setting;
    private boolean open;

    public EnumButton(EnumSetting<E> setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW || MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;
        boolean future = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.FUTURE;
        boolean dotgod = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        if (future) {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(99) : ColorUtil.getCurrentWithAlpha(120)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(55)));

        } else if (dotgod) {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(65) : ColorUtil.getCurrentWithAlpha(90)) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(26) : ColorUtil.getCurrentWithAlpha(35)));

        } else {
            Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentWithAlpha(120) : ColorUtil.getCurrentWithAlpha(200)) : (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        }
        Managers.TEXT.drawStringWithShadow((newStyle ? setting.getName().toLowerCase() + ":" : setting.getName()) + " " + ChatFormatting.GRAY + (setting.getValue().name().equalsIgnoreCase("ABC") ? "ABC" : setting.getValue().name()), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);

        int y = (int) this.y;

        if (isOpen()) {
            for (Object o : setting.getValue().getClass().getEnumConstants()) {

                y += 12;
                String s = !Objects.equals(o.toString(), "ABC") ? Character.toUpperCase(o.toString().charAt(0)) + o.toString().toLowerCase().substring(1) : o.toString();

                Managers.TEXT.drawStringWithShadow((setting.getValue().name().equals(s) ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s, width / 2.0f - Managers.TEXT.getStringWidth(s) / 2.0f + 2.0f + x, y + (12 / 2f) - (mc.fontRenderer.FONT_HEIGHT / 2f) + 3.5f, -1);
            }
        }
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

        if (isOpen()) {
            for (Object o : setting.getValue().getDeclaringClass().getEnumConstants()) {
                y += 12;
                if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 12 + 3.5f && mouseButton == 0) {
                    setting.fromString(String.valueOf(o));
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
            }
        }
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toggle() {
        setting.setValue((E) EnumHelper.next(setting.getValue()));;
    }

    @Override
    public boolean getState() {
        return true;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}

