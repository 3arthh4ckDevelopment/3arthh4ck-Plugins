package me.earth.autothirtytwok.guinness;

import me.earth.autothirtytwok.util.GuinnessRenderUtil;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class HopperRadius extends Module {

    private static final HopperRadius INSTANCE = new HopperRadius();
    private static final ModuleCache<SecretClose> SECRET_CLOSE =
            Caches.getModule(SecretClose.class);
    private static final ModuleCache<XCarry> XCARRY =
            Caches.getModule(XCarry.class);

    private final Setting<Double> height =
            register(new NumberSetting<>("Height", 1.0, 6.0, 9.0));
    private final Setting<Color> color1 =
            register(new ColorSetting("Color1", new Color(255, 0, 0, 0)));
    private final Setting<Color> color2 =
            register(new ColorSetting("Color2", new Color(0, 255, 0, 0)));
    public BlockPos oldHopperPos;
    public double radius;
    public BlockPos hopperPos;
    public double wallHeight;
    
    public HopperRadius() {
        super("HopperRadius", Category.Combat);

        this.setData(new SimpleData(this,  "Shows the radius around the hopper you are currently in"));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null)
                return;

            if (hopperPos == null) {
                return;
            }
            if (!mc.world.getBlockState(hopperPos).getBlock().getLocalizedName().equalsIgnoreCase("Hopper") || mc.player.getDistanceSqToCenter(this.hopperPos) > 65.0) {
                hopperPos = null;
            }
        }));

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, e -> {
            if (wallHeight < height.getValue()) {
                wallHeight += Double.longBitsToDouble(Double.doubleToLongBits(3751.2945874163925) ^ 0x7FD9347793877A0BL) * height.getValue();
            }
            else if (wallHeight > height.getValue()) {
                wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(114.68668265750983) ^ 0x7FD8D113DC7F3B77L);
            }
            if (hopperPos != null) {
                GuinnessRenderUtil.drawCircle(hopperPos, Double.longBitsToDouble(Double.doubleToLongBits(0.14553988619673233) ^ 0x7FE2A10D0DBD4061L), this.wallHeight, color1.getValue(), color2.getValue());
                oldHopperPos = this.hopperPos;
                radius = Double.longBitsToDouble(Double.doubleToLongBits(0.14070361133713452) ^ 0x7FE20293708FA091L);
                return;
            }
            if (hopperPos == null && oldHopperPos != null) {
                GuinnessRenderUtil.drawCircle(oldHopperPos, radius, wallHeight, color1.getValue(), color2.getValue());
                if (wallHeight > Double.longBitsToDouble(Double.doubleToLongBits(1.1989844897406259E308) ^ 0x7FE557B6C1188A7BL)) {
                    wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(219.7551656050837) ^ 0x7FD2E1B3C896855BL);
                    return;
                }
                if (this.radius > Double.longBitsToDouble(Double.doubleToLongBits(6.522680943073321E306) ^ 0x7FA293C429F2655FL)) {
                    this.radius -= Double.longBitsToDouble(Double.doubleToLongBits(90.1592080629349) ^ 0x7FEF13A9EE9A7DBDL);
                }
                else {
                    this.radius = Double.longBitsToDouble(Double.doubleToLongBits(7.96568863695466E307) ^ 0x7FDC5BD9D9AD2AC5L);
                }
            }
        }));

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, e -> {
            if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)e.getPacket();
                final BlockPos packetPos = packet.getPos();
                if (mc.world.getBlockState(packetPos).getBlock().getLocalizedName().equalsIgnoreCase("Hopper")) {
                    wallHeight = Double.longBitsToDouble(Double.doubleToLongBits(2.226615095116189E307) ^ 0x7FBFB542DC55A837L);
                    hopperPos = packetPos;
                }
            }
            if (e.getPacket() instanceof CPacketCloseWindow) {
                if (SECRET_CLOSE.isEnabled() && (SecretClose.lastGui instanceof GuiHopper || mc.currentScreen instanceof GuiHopper)) {
                    return;
                }
                Label_0497: {
                    if (XCARRY.isEnabled()) {
                        if (!(SecretClose.lastGui instanceof GuiInventory)) {
                            if (!(mc.currentScreen instanceof GuiInventory)) {
                                break Label_0497;
                            }
                        }
                        return;
                    }
                }
                this.hopperPos = null;
            }
        }));

        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, e -> {
            if (!(e.getPacket() instanceof SPacketCloseWindow)) {
                if (e.getPacket() instanceof SPacketOpenWindow) {
                    if (((SPacketOpenWindow)e.getPacket()).getWindowTitle().getUnformattedText().equalsIgnoreCase("Item Hopper")) {
                        return;
                    }
                    this.hopperPos = null;
                }
            }
        }));
    }

    public static HopperRadius getInstance() {
        return INSTANCE;
    }

}
