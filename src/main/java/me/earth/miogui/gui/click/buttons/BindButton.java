package me.earth.miogui.gui.click.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.miogui.ClickGuiModule;
import me.earth.miogui.gui.MioClickGui;
import me.earth.miogui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {
    private final BindSetting setting;
    public boolean isListening;

    public BindButton(BindSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean newStyle = MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.NEW || MioClickGui.CLICKGUI.get().style.getValue() == ClickGuiModule.Style.DOTGOD;

        int color = ColorUtil.toARGB(MioClickGui.CLICKGUI.get().color.getValue().getRed(), MioClickGui.CLICKGUI.get().color.getValue().getGreen(), MioClickGui.CLICKGUI.get().color.getValue().getBlue(), 255);
        Render2DUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!isHovering(mouseX, mouseY) ? ColorUtil.getCurrentGui(200) : ColorUtil.getCurrentGui(90)));
        if (isListening) {
            Managers.TEXT.drawStringWithShadow("Press a Key...", x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), -1);
        } else {
            Managers.TEXT.drawStringWithShadow((newStyle ? setting.getName().toLowerCase() : setting.getName()) + " " + ChatFormatting.GRAY + setting.getValue().toString().toUpperCase(), x + 2.3f, y - 1.7f - (float) MioClickGui.INSTANCE.getTextOffset(), getState() ? -1 : -5592406);
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
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (isListening) {
            Bind bind = Bind.fromKey(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = Bind.none();
            }
            setting.setValue(bind);
            onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return MioClickGui.CLICKGUI.get().getButtonHeight() - 1;
    }

    @Override
    public void toggle() {
        isListening = !isListening;
    }

    @Override
    public boolean getState() {
        return !isListening;
    }
}

