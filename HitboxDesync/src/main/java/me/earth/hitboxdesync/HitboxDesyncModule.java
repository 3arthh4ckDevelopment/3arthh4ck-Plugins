package me.earth.hitboxdesync;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

    /**
    * https://github.com/mioclient/hitbox-desync
    */

public class HitboxDesyncModule extends Module {
    private static final HitboxDesyncModule INSTANCE = new HitboxDesyncModule();
    private static final double MAGIC_OFFSET = .200009968835369999878673424677777777777761;

    public HitboxDesyncModule()
    {
        super("HitboxDesync", Category.Movement);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent tickEvent) {
                if (mc.world == null)
                    return;

                EnumFacing f = mc.player.getHorizontalFacing();
                AxisAlignedBB bb = mc.player.getEntityBoundingBox();
                Vec3d center = bb.getCenter();
                Vec3d offset = new Vec3d(f.getDirectionVec());

                Vec3d fin = merge(new Vec3d(new BlockPos(center)).add(.5, 0, .5).add(offset.scale(MAGIC_OFFSET)), f);
                mc.player.setPositionAndUpdate(
                        fin.x == 0 ? mc.player.posX : fin.x,
                        mc.player.posY,
                        fin.z == 0 ? mc.player.posZ : fin.z);
                disable();
            }

        });

        this.setData(new SimpleData(this, "HitBox Desync exploit by mio client devs\nHow to use:\n1) enter a 2x1 hole\n2) look in +X or +Z direction\n3) Enable"));
    }

    private Vec3d merge(Vec3d a, EnumFacing facing) {
        return new Vec3d(a.x * Math.abs(facing.getDirectionVec().getX()), a.y * Math.abs(facing.getDirectionVec().getY()), a.z * Math.abs(facing.getDirectionVec().getZ()));
    }

    public static HitboxDesyncModule getInstance()
    {
        return INSTANCE;
    }

}
