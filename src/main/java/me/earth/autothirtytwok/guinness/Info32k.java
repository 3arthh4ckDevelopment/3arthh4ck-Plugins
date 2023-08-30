package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.gui.ScaledResolution;

public class Info32k extends Module {

    private static final Info32k INSTANCE = new Info32k();

    public Info32k() {
        super("32kInfo", Category.Combat);
        this.setData(new SimpleData(this,  "Tells you if you have a 32k in your hotbar"));

        this.listeners.add(new LambdaListener<>(Render2DEvent.class, e -> {
            final ScaledResolution sr = new ScaledResolution(mc);
            if (this.has32k()) {
                String text = "32k in hotbar!";
                Managers.TEXT.drawStringWithShadow(text, sr.getScaledWidth() / 2 - Managers.TEXT.getStringWidth(text), 3, -65536);
            }
            else {
                String text = "No 32k in hotbar!";
                Managers.TEXT.drawStringWithShadow(text, sr.getScaledWidth() / 2 - Managers.TEXT.getStringWidth(text), 3, -65536);
            }
        }));
    }

    public static Info32k getInstance() {
        return INSTANCE;
    }
    
    public boolean has32k() {
        for (int i = 0; i < 9; ++i) {
            if (CheckUtil.is32k(mc.player.inventory.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }
}
