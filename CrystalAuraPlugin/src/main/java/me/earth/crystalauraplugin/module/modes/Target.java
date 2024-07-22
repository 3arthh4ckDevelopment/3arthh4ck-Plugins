package me.earth.crystalauraplugin.module.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public enum Target implements Globals {
    Closest {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players, double range) {
            return EntityUtil.getClosestEnemy(players);
        }
    },
    FOV {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players, double range) {
            PlayerEntity closest = null;
            double closestAngle = 360.0;
            for (PlayerEntity player : players) {
                double angle;
                if (!EntityUtil.isValid(player, range) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle) || !(angle < (double) (mc.options.getFov().getValue() / 2.0f)))
                    continue;
                closest = player;
                closestAngle = angle;
            }
            return closest;
        }
    },
    Angle {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players, double range) {
            PlayerEntity closest = null;
            double closestAngle = 360.0;
            for (PlayerEntity player : players) {
                double angle;
                if (!EntityUtil.isValid(player, range) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle))
                    continue;
                closest = player;
                closestAngle = angle;
            }
            return closest;
        }
    },
    Damage {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players, double range) {
            return null;
        }
    };


    public abstract PlayerEntity getTarget(List<PlayerEntity> var1, double var2);
}