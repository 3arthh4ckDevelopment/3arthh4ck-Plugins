package me.earth.futuregui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.MathHelper;

public class FutureTextManager extends SubscriberImpl implements Globals
{
    private static final FutureTextManager INSTANCE = new FutureTextManager();

    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;

    private final StopWatch idleTimer = new StopWatch();
    private boolean idling;

    private FutureTextManager()
    {
        this.updateResolution();
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent tickEvent)
            {
                updateResolution();
            }
        });
    }

    public static FutureTextManager getInstance()
    {
        return INSTANCE;
    }

    public void drawStringWithShadow(String text, float x, float y, int color)
    {
        this.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color, boolean shadow)
    {
        return mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int getStringWidth(String text)
    {
        return mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight()
    {
        return mc.fontRenderer.FONT_HEIGHT;
    }

    public void updateResolution()
    {
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;

        if (i == 0) {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        double scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        double scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign()
    {
        if(idleTimer.passed(500))
        {
            idling = !idling;
            idleTimer.reset();
        }

        return idling ? "_" : "";
    }

}
