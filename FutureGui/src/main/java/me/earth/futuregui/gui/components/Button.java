package me.earth.futuregui.gui.components;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.futuregui.gui.Component;
import me.earth.futuregui.gui.FutureGuiScreen;
import me.earth.futuregui.util.FutureColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.LocalRandom;

public class Button extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(DrawContext context, float mouseX, float mouseY, float partialTicks) {
        Render2DUtil.drawRect(context.getMatrices(), x, y, x + width, y + height, getState() ? (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(245) : FutureColorUtil.getClientColorCustomAlpha(215)) : (!isHovering(mouseX, mouseY) ? FutureColorUtil.getClientColorCustomAlpha(60) : FutureColorUtil.getClientColorCustomAlpha(130)));
        Managers.TEXT.drawStringWithShadow(context, getName(), x + 2.0f, y + 4.0f, getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f, new LocalRandom(0), mc.player.getX(), mc.player.getY(), mc.player.getZ()));
    }

    public void toggle() {}

    public boolean getState()
    {
        return state;
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    public boolean isHovering(float mouseX, float mouseY) {
        for (Component component : FutureGuiScreen.getInstance().getComponents()) {
            if (component.drag) {
                return false;
            }
        }

        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
