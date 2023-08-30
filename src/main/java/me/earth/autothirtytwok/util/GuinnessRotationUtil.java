package me.earth.autothirtytwok.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class GuinnessRotationUtil implements Globals {

    public static float[] rotateClient(final double n, final double n2, final double n3) {
        final double diffX = n + Double.longBitsToDouble(Double.doubleToLongBits(2.4805877814838024) ^ 0x7FE3D83E6822A419L) - mc.player.posX;
        final double diffY = n2 + Double.longBitsToDouble(Double.doubleToLongBits(155.92994975338294) ^ 0x7F837DC225FC367FL) - (mc.player.posY + mc.player.getEyeHeight());
        final double diffZ = n3 + Double.longBitsToDouble(Double.doubleToLongBits(27.441460872776755) ^ 0x7FDB7103946B0A2FL) - mc.player.posZ;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - Float.intBitsToFloat(Float.floatToIntBits(1.7317656f) ^ 0x7D69AA7F);
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        final float[] array = new float[2];
        final int n4 = 0;
        final EntityPlayerSP player = mc.player;
        array[n4] = (player.rotationYaw += MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        final int n5 = 1;
        final EntityPlayerSP player2 = mc.player;
        array[n5] = (player2.rotationPitch += MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        return array;
    }

    public static double[] calculateLookAt(final double n, final double n2, final double n3, final EntityPlayer entityPlayer) {
        double dirx = entityPlayer.posX - n;
        double diry = entityPlayer.posY - n2;
        double dirz = entityPlayer.posZ - n3;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch *= Double.longBitsToDouble(Double.doubleToLongBits(0.05684625147455668) ^ 0x7FE1BF2FB7944027L);
        yaw *= Double.longBitsToDouble(Double.doubleToLongBits(0.05102168933521887) ^ 0x7FE6BA5FD44A73E5L);
        yaw += Double.longBitsToDouble(Double.doubleToLongBits(1.3477969896426776) ^ 0x7FA31093938299CFL);
        return new double[] { yaw, pitch };
    }

}
