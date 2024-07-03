package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.text.TextFormatting;

public class Throw32k extends Module {

    private static final Throw32k INSTANCE = new Throw32k();

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Single));
    
    public boolean ready2swap;
    public int sword;
    public int shulker;
    public int delay;
    
    public Throw32k() {
        super("32kThrow", Category.Combat);
        this.setData(new SimpleData(this, "Automatically throws out reverted swords in your hotbar and replaces them with shulkers"));
        this.ready2swap = false;

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            if (mode.getValue() == Mode.Single) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, shulker, this.sword, ClickType.SWAP, mc.player);
                mc.player.connection.sendPacket(new CPacketClickWindow(0, shulker, 0, ClickType.THROW, mc.player.inventory.getStackInSlot(shulker), mc.player.inventoryContainer.getNextTransactionID(mc.player.inventory)));
                disable();
            }
            if (ready2swap) {
                ++delay;
                if (delay >= 4) {
                    ready2swap = false;
                    delay = 0;
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, shulker, this.sword, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CPacketClickWindow(0, shulker, 0, ClickType.THROW, mc.player.inventory.getStackInSlot(shulker), mc.player.inventoryContainer.getNextTransactionID(mc.player.inventory)));
                }
            }
        }));

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, e -> {
            if (mode.getValue() == Mode.Persistant) {
                if (e.getPacket() instanceof CPacketCloseWindow) {
                    sword = -1;
                    sword = InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD);
                    shulker = -1;
                    for (int i = 9; i < 36; ++i) {
                        final ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemBlock) {
                                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                                if (CheckUtil.shulkers.contains(block)) {
                                    shulker = i;
                                }
                            }
                        }
                    }
                    if (sword == -1) {
                        return;
                    }
                    if (shulker == -1) {
                        sendClientMessage(TextFormatting.BLUE + "[32kThrow]" + TextFormatting.RESET + "No shulkers left!");
                        return;
                    }
                    if (CheckUtil.holding32k(mc.player)) {
                        return;
                    }
                    ready2swap = true;
                }
            }
        }));
    }

    public static Throw32k getInstance() {
        return INSTANCE;
    }
    
    @Override
    public void onEnable() {
        if (mode.getValue() == Mode.Persistant) {
            return;
        }
        sword = -1;
        sword = InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD);
        shulker = -1;
        for (int i = 9; i < 36; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (CheckUtil.shulkers.contains(block)) {
                        shulker = i;
                    }
                }
            }
        }
        if (sword == -1) {
            sendClientMessage("No sword to swap!");
            disable();
            return;
        }
        if (shulker == -1) {
            sendClientMessage("No shulkers left!");
            disable();
        }
    }

    private void sendClientMessage(String message) {
        Managers.CHAT.sendDeleteMessage(message, this.getName(), ChatIDs.MODULE);
    }
    
    @Override
    public void onDisable() {
        sword = -1;
        shulker = -1;
    }

    private enum Mode {
        Single,
        Persistant
    }

}
