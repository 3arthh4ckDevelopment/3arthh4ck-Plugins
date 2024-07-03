package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.CheckUtil;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Block32k extends Module {

    private static final Block32k INSTANCE = new Block32k();
    public List<EntityPlayer> badguys;
    
    public Block32k() {
        super("32kBlock", Category.Combat);
        badguys = new ArrayList<>();

        this.setData(new SimpleData(this,  "Blocks dispensers if another player is enabling auto32k"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            findEnemies();
            if (badguys.size() == 0) {
                return;
            }
            if (CheckUtil.holding32k(mc.player)) {
                return;
            }
            final int currentSlot = mc.player.inventory.currentItem;
            final int obs = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            if (obs != -1) {
                mc.player.inventory.currentItem = obs;
                badguys.forEach(this::block32k);
                mc.player.inventory.currentItem = currentSlot;
            }
        }));
    }

    public static Block32k getInstance() {
        return INSTANCE;
    }
    
    public void findEnemies() {
        badguys = mc.world.playerEntities.stream()
                .filter(Block32k::selfCheck)
                .filter(Block32k::checkFriend)
                .filter(Block32k::checkDistance)
                .filter(Block32k::checkItem)
                .filter(Block32k::checkDispenserItem)
                .collect(Collectors.toList());
    }
    
    public void block32k(final EntityPlayer entityPlayer) {
        mc.world.loadedTileEntityList.stream()
                .filter(Block32k::checkDispenserEntity)
                .filter(Block32k::checkMaxDistance)
                .forEach((e) -> {
                    if (mc.world.getBlockState(e.getPos().offset(EnumFacing.getDirectionFromEntityLiving(e.getPos(), entityPlayer))).getMaterial().isReplaceable()) {
                        CheckUtil.placeBlock(e.getPos().offset(EnumFacing.getDirectionFromEntityLiving(e.getPos(), entityPlayer)), mc.player.inventory.currentItem);
                    }
                });
    }
    
    public static boolean checkMaxDistance(final TileEntity tileEntity) {
        return tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) <= 64.0;
    }
    
    public static boolean checkDispenserEntity(final TileEntity tileEntity) {
        return tileEntity instanceof TileEntityDispenser;
    }
    
    public static boolean checkDispenserItem(final EntityPlayer entityPlayer) {
        return ((ItemBlock)entityPlayer.getHeldItemMainhand().getItem()).getBlock() == Blocks.DISPENSER;
    }
    
    public static boolean checkItem(final EntityPlayer entityPlayer) {
        return entityPlayer.getHeldItemMainhand().getItem() instanceof ItemBlock;
    }
    
    public static boolean checkDistance(final EntityPlayer entityPlayer) {
        return mc.player.getDistance(entityPlayer) <= 8.0;
    }
    
    public static boolean checkFriend(final EntityPlayer entityPlayer) {
        return !Managers.FRIENDS.contains(entityPlayer.getName());
    }
    
    public static boolean selfCheck(final EntityPlayer entityPlayer) {
        return entityPlayer != mc.player;
    }

}
