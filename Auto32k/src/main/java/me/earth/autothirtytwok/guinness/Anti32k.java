package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.init.Items;

public class Anti32k extends Module {

    private static final Anti32k INSTANCE = new Anti32k();
    public Anti32k() {
        super("Anti32k", Category.Combat);
        this.setData(new SimpleData(this,  "Swaps a totem to your mainhand when an enemy with a 32k is in range"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            swapTotem();
            findEnemies();
        }));
    }

    public static Anti32k getInstance() {
        return INSTANCE;
    }
    
    public void swapTotem() {
        if (mc.player.inventory.getStackInSlot(0).getItem().equals(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        final int totem = InventoryUtil.findInHotbar(s -> s.getItem() == Items.TOTEM_OF_UNDYING);
        if (totem != -1 && totem != 0 && totem != -2) {
            if (mc.currentScreen instanceof GuiHopper) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, totem - 4, 0, ClickType.SWAP, mc.player);
                mc.playerController.windowClick(mc.player.openContainer.windowId, totem - 4, 0, ClickType.SWAP, mc.player);
            }
            else if (mc.currentScreen instanceof GuiDispenser) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, totem, 0, ClickType.SWAP, mc.player);
            }
            else if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory)) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, totem, 0, ClickType.SWAP, mc.player);
            }
            else {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, totem, 0, ClickType.SWAP, mc.player);
            }
        }
    }
    
    public void findEnemies() {
        final int badGuys = (int) mc.world.playerEntities.stream()
                        .filter(Anti32k::isFriend)
                        .filter(Anti32k::isInRange)
                        .filter(Anti32k::has32k)
                        .count();

        if (badGuys == 0) {
            return;
        }
        if (CheckUtil.holding32k(mc.player) || this.isEnabled()) {
            return;
        }
        mc.player.inventory.currentItem = 0;
    }

    
    public static boolean has32k(final EntityPlayer entityPlayer) {
        return CheckUtil.holding32k(entityPlayer);
    }
    
    public static boolean isInRange(final EntityPlayer entityPlayer) {
        return mc.player.getDistance(entityPlayer) <= 8.0;
    }
    
    public static boolean isFriend(final EntityPlayer entityPlayer) {
        return !Managers.FRIENDS.contains(entityPlayer.getName());
    }
}
