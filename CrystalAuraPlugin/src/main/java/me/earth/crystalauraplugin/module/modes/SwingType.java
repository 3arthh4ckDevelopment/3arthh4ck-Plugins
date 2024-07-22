package me.earth.crystalauraplugin.module.modes;

import net.minecraft.util.Hand;

public enum SwingType {
    None {
        @Override
        public Hand getHand() {
            return null;
        }
    },
    MainHand {
        @Override
        public Hand getHand() {
            return Hand.MAIN_HAND;
        }
    },
    OffHand {
        @Override
        public Hand getHand() {
            return Hand.OFF_HAND;
        }
    };


    public abstract Hand getHand();
}

