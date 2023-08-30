package me.earth.autothirtytwok.phobos.phoboscriticals;

import me.earth.autothirtytwok.guinness.NewAuto32k;
import me.earth.autothirtytwok.phobos.earthautothirtytwok.EarthAuto32k;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

import java.util.Objects;

public class PhobosCriticals extends Module {

    private static final PhobosCriticals INSTANCE = new PhobosCriticals();

    private static final ModuleCache<NewAuto32k> NEW_AUTO_32K =
            Caches.getModule(NewAuto32k.class);
    private static final ModuleCache<EarthAuto32k> PHOBO_AUTO_32K =
            Caches.getModule(EarthAuto32k.class);

    public final Setting<Boolean> noDesync =
            register(new BooleanSetting("NoDesync", true));
    public final Setting<Boolean> cancelFirst =
            register(new BooleanSetting("CancelFirst32k", true));
    public final Setting<Integer> delay32k =
            register(new NumberSetting<>("32kDelay", 25, 0, 500));
    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.PACKET));
    private final Setting<Integer> packets =
            register(new NumberSetting<>("Packets", 2, 1, 5));
    private final Setting<Integer> desyncDelay =
            register(new NumberSetting<>("DesyncDelay", 10, 0, 500));
    private final StopWatch timer = new StopWatch();
    private final StopWatch timer32k = new StopWatch();
    private boolean firstCanceled = false;
    private boolean resetTimer = false;

    public PhobosCriticals() {
        super("PhobosCriticals", Category.Combat);
        this.setData(new SimpleData(this, "Scores criticals for you"));

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, event -> {
            CPacketUseEntity packet;
            if (PHOBO_AUTO_32K.isEnabled() && (PHOBO_AUTO_32K.get().switching || !PHOBO_AUTO_32K.get().autoSwitch.getValue() || PHOBO_AUTO_32K.get().mode.getValue() == EarthAuto32k.Mode.DISPENSER) && timer.passed(500L) && cancelFirst.getValue()) {
                firstCanceled = true;
            } else if (!PHOBO_AUTO_32K.isEnabled() || !Auto32k.getInstance().switching && PHOBO_AUTO_32K.get().autoSwitch.getValue() && PHOBO_AUTO_32K.get().mode.getValue() != EarthAuto32k.Mode.DISPENSER || !cancelFirst.getValue()) {
                firstCanceled = false;
            }
            if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
                if (firstCanceled) {
                    timer32k.reset();
                    resetTimer = true;
                    timer.setTime(desyncDelay.getValue() + 1);
                    firstCanceled = false;
                    return;
                }
                if (resetTimer && !timer32k.passed(delay32k.getValue())) {
                    return;
                }
                if (resetTimer && timer32k.passed(delay32k.getValue())) {
                    resetTimer = false;
                }
                if (!timer.passed(desyncDelay.getValue())) {
                    return;
                }
                if (!(!mc.player.onGround || mc.gameSettings.keyBindJump.isKeyDown() || !(packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) && noDesync.getValue() || mc.player.isInWater() || mc.player.isInLava())) {
                    if (mode.getValue() == Mode.PACKET) {
                        switch (packets.getValue()) {
                            case 1: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (double) 0.1f, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case 2: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case 3: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0125, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case 4: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.03, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case 5: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer());
                                mc.player.onCriticalHit(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)));
                                break;
                            }
                        }
                    } else {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                    }
                    timer.reset();
                }
            }
        }));
    }


    public static PhobosCriticals getInstance() {
        return INSTANCE;
    }

    public enum Mode {
        PACKET,
        BYPASS
    }
}

