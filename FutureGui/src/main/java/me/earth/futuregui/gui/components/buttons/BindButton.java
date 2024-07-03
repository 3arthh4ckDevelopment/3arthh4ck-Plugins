package me.earth.futuregui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.futuregui.gui.FutureGuiScreen;
import me.earth.futuregui.gui.components.Button;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.LocalRandom;

public class BindButton extends Button {
    private final BindSetting setting;
    public boolean isListening;

    public BindButton(BindSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + width + 7.4f, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(230) : FutureColorUtil.getClientColorCustomAlpha(170)) : !isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130));
        if (isListening) {
            Managers.TEXT.drawStringWithShadow(context, "Listening...", x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        else {
            Managers.TEXT.drawStringWithShadow(context, setting.getName() + " " + TextColor.GRAY + setting.getValue(), x + 2.3F, y - 1.7F - FutureGuiScreen.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
    }

    @Override
    public void update() {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        if (isListening) {
            Bind bind = Bind.fromKey(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            else if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = Bind.none();
            }

            setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public void toggle()
    {
        isListening = !isListening;
    }

    @Override
    public boolean getState()
    {
        return !isListening;
    }

}
