package me.earth.crystalauraplugin.module.util;

import net.minecraft.entity.Entity;

public class BreakData {
    private float damage;
    private Entity crystal;
    private int count;
    private Entity fallBack;
    private float fallBackDamage = 1000.0f;
    private int minDmgCount;

    public int getCount() {
        return this.count;
    }

    public int getMinDmgCount() {
        return this.minDmgCount;
    }

    public void incrementMinDmgCount() {
        ++this.minDmgCount;
    }

    public void increment() {
        ++this.count;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Entity getCrystal() {
        return this.crystal;
    }

    public void setCrystal(Entity crystal) {
        this.crystal = crystal;
    }

    public Entity getFallBack() {
        return this.fallBack;
    }

    public void setFallBack(Entity fallBack, float damage) {
        if (this.fallBackDamage > damage) {
            this.fallBackDamage = damage;
            this.fallBack = fallBack;
        }
    }

    public float getFallBackDamage() {
        return this.fallBackDamage;
    }
}

