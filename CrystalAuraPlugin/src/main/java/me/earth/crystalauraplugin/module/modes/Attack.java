package me.earth.crystalauraplugin.module.modes;

import me.earth.earthhack.impl.util.minecraft.InventoryUtil;

public enum Attack {
    Always {
        @Override
        public boolean shouldCalc() {
            return true;
        }

        @Override
        public boolean shouldAttack() {
            return true;
        }
    },
    BreakSlot {
        @Override
        public boolean shouldCalc() {
            return InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL);
        }

        @Override
        public boolean shouldAttack() {
            return InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL);
        }
    },
    Calc {
        @Override
        public boolean shouldCalc() {
            return true;
        }

        @Override
        public boolean shouldAttack() {
            return InventoryUtil.isHolding(net.minecraft.item.Items.END_CRYSTAL);
        }
    };


    public abstract boolean shouldCalc();

    public abstract boolean shouldAttack();
}

