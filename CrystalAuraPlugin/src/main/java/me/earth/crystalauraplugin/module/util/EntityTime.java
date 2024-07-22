package me.earth.crystalauraplugin.module.util;

import net.minecraft.entity.Entity;

public class EntityTime {
    private final long time;
    private final Entity entity;

    public EntityTime(Entity entity, long time) {
        this.entity = entity;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

