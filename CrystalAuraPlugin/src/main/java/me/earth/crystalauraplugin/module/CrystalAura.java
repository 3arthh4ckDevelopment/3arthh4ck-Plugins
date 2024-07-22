package me.earth.crystalauraplugin.module;

import io.netty.util.internal.ConcurrentSet;
import me.earth.crystalauraplugin.module.modes.*;
import me.earth.crystalauraplugin.module.util.CrystalAuraData;
import me.earth.crystalauraplugin.module.util.EntityTime;
import me.earth.crystalauraplugin.module.util.TaskThread;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.Swing;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrystalAura
        extends Module {
    private static final ModuleCache<PingBypassModule> PINGBYPASS = Caches.getModule(PingBypassModule.class);
    final Setting<ACPage> pages = this.register(new EnumSetting<ACPage>("Pages", ACPage.Place));
    final Setting<Boolean> place = this.register(new BooleanSetting("Place", true));
    final Setting<Target> target = this.register(new EnumSetting<Target>("Target", Target.Closest));
    final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Float> placeTrace = this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Float> minDamage = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    final Setting<Integer> placeDelay = this.register(new NumberSetting<Integer>("PlaceDelay", 0, 0, 500));
    final Setting<Float> maxSelfP = this.register(new NumberSetting<Float>("MaxSelfPlace", Float.valueOf(9.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    final Setting<Float> facePlace = this.register(new NumberSetting<Float>("FacePlace", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    final Setting<Integer> multiPlace = this.register(new NumberSetting<Integer>("MultiPlace", 1, 1, 5));
    final Setting<Boolean> countMin = this.register(new BooleanSetting("CountMin", false));
    final Setting<Boolean> antiSurr = this.register(new BooleanSetting("AntiSurround", true));
    final Setting<Boolean> newerVer = this.register(new BooleanSetting("1.13+", false));
    final Setting<Attack> attack = this.register(new EnumSetting<Attack>("Attack", Attack.BreakSlot));
    final Setting<Boolean> explode = this.register(new BooleanSetting("Break", true));
    final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Float> breakTrace = this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Float> breakMinDmg = this.register(new NumberSetting<Float>("MinBreakDmg", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    final Setting<Float> slowBreak = this.register(new NumberSetting<Float>("SlowBreak", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    final Setting<Integer> slowDelay = this.register(new NumberSetting<Integer>("SlowDelay", 500, 0, 500));
    final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 0, 0, 500));
    final Setting<Float> maxSelfB = this.register(new NumberSetting<Float>("MaxSelfBreak", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", false));
    final Setting<Rotate> rotate = this.register(new EnumSetting<Rotate>("Rotate", Rotate.None));
    final Setting<Boolean> stay = this.register(new BooleanSetting("Stay", false));
    final Setting<Boolean> multiThread = this.register(new BooleanSetting("MultiThread", false));
    final Setting<Boolean> suicide = this.register(new BooleanSetting("Suicide", false));
    final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(12.0f), Float.valueOf(6.0f), Float.valueOf(12.0f)));
    final Setting<Boolean> override = this.register(new BooleanSetting("Override", false));
    final Setting<Float> minFP = this.register(new NumberSetting<Float>("MinFace", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(4.0f)));
    final Setting<Boolean> noFriendP = this.register(new BooleanSetting("AntiFriendPop", true));
    final Setting<AutoSwitch> autoSwitch = this.register(new EnumSetting<AutoSwitch>("AutoSwitch", AutoSwitch.Bind));
    final Setting<Boolean> mainHand = this.register(new BooleanSetting("MainHand", false));
    final Setting<Bind> switchBind = this.register(new BindSetting("SwitchBind", Bind.none()));
    final Setting<Boolean> switchBack = this.register(new BooleanSetting("SwitchBack", true));
    final Setting<SwingType> swing = this.register(new EnumSetting<SwingType>("Swing", SwingType.MainHand));
    final Setting<Float> pbTrace = this.register(new NumberSetting<Float>("CombinedTrace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Boolean> setDead = this.register(new BooleanSetting("SetDead", false));
    final Setting<Boolean> dangerous = this.register(new BooleanSetting("Dangerous", false));
    final Setting<Float> targetRange = this.register(new NumberSetting<Float>("T-Range", Float.valueOf(20.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    final Setting<Boolean> useYawLimit = this.register(new BooleanSetting("UseYawLimit", false));
    final Setting<Boolean> useForPlace = this.register(new BooleanSetting("PlaceYawLimit", false));
    final Setting<Integer> limit = this.register(new NumberSetting<Integer>("YawLimit", 20, 1, 180));
    final Setting<Integer> jitter = this.register(new NumberSetting<Integer>("RandomYaw", 20, 0, 40));
    final Setting<Boolean> antiFeetPlace = this.register(new BooleanSetting("AntiFeetPlace", true));
    final Setting<Integer> footDelay = this.register(new NumberSetting<Integer>("FootDelay", 12, 0, 100));
    final Setting<Boolean> flooder = this.register(new BooleanSetting("Flooder", false));
    final Setting<Integer> floodDelay = this.register(new NumberSetting<Integer>("FloodDelay", 5, 0, 20));
    final Setting<Integer> floodDelayNs = this.register(new NumberSetting<Integer>("FloodDelayNs", 500000, 100000, 1000000));
    final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("Cooldown", 250, 0, 500));
    final Setting<Float> partialT = this.register(new NumberSetting<Float>("PartialTicks", Float.valueOf(0.8f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    final Setting<Boolean> fallBack = this.register(new BooleanSetting("FallBack", true));
    final Setting<Float> fallbackDmg = this.register(new NumberSetting<Float>("FB-Dmg", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    final Setting<Boolean> soundR = this.register(new BooleanSetting("SoundRemove", false));
    final Setting<Boolean> holdFP = this.register(new BooleanSetting("HoldFP", true));
    final Setting<Boolean> multiTask = this.register(new BooleanSetting("MultiTask", true));
    final Setting<ThreadMode> threadMode = this.register(new EnumSetting<ThreadMode>("ThreadMode", ThreadMode.Pre));
    final Setting<Integer> threadDelay = this.register(new NumberSetting<Integer>("ThreadDelay", 50, 5, 500));
    final Setting<Boolean> predict = this.register(new BooleanSetting("ID-Predict", false));
    final Setting<Boolean> noParticles = this.register(new BooleanSetting("NoOffhandParticles", false));
    final Setting<Boolean> pingBypass = this.register(new BooleanSetting("PingBypass", true));
    final Setting<Integer> interpolate = this.register(new NumberSetting<Integer>("Interpolate", 0, 0, 20));
    final Setting<Boolean> slowLegBreak = this.register(new BooleanSetting("SlowLegBreak", false));
    final Setting<Integer> legDelay = this.register(new NumberSetting<Integer>("LegDelay", 100, 1, 500));
    final Setting<Integer> tickThreshold = this.register(new NumberSetting<Integer>("TickThreshold", 40, 1, 50));
    final Setting<Integer> maxTick = this.register(new NumberSetting<Integer>("MaxTickTime", 45, 1, 50));
    final Setting<Integer> serverDelay = this.register(new NumberSetting<Integer>("ServerDelay", 3, 0, 20));
    final Setting<Boolean> antiWeakness = this.register(new BooleanSetting("AntiWeakness", false));
    final Setting<Boolean> noFaceSpam = this.register(new BooleanSetting("No-Face-Spam", true));
    final Setting<Boolean> breakSwitch = this.register(new BooleanSetting("BreakSwitch", false));
    final Setting<Boolean> fade = this.register(new BooleanSetting("Fade", false));
    final Setting<Boolean> legSwitch = this.register(new BooleanSetting("LegSwitch", true));
    final Setting<Integer> animation = this.register(new NumberSetting<Integer>("AnimationTime", 250, 0, 500));
    final ColorSetting fillColor = this.register(new ColorSetting("Fill", new Color(255, 255, 255, 128)));
    final ColorSetting outlineColor = this.register(new ColorSetting("Outline", new Color(255, 255, 255, 255)));
    final Map<Integer, EntityTime> killed = new ConcurrentHashMap<Integer, EntityTime>();
    final Set<Integer> attacked = new ConcurrentSet();
    final Set<BlockPos> slow = new ConcurrentSet();
    final StopWatch renderTimer = new StopWatch();
    final StopWatch animationTimer = new StopWatch();
    final StopWatch threadTimer = new StopWatch();
    final HelperBreak breakHelper;
    final HelperPlace placeHelper;
    final StopWatch targetTimer = new StopWatch();
    final StopWatch serverTimer = new StopWatch();
    private final DiscreteTimer placeTimer = new GuardTimer(1000L, 5L).reset(this.placeDelay.getValue().intValue());
    private final DiscreteTimer breakTimer = new GuardTimer(1000L, 5L).reset(this.breakDelay.getValue().intValue());
    private final Set<BlockPos> positions = Collections.newSetFromMap(new ConcurrentHashMap());
    private final TaskThread thread = new TaskThread("3arthh4ck-CrystalAuraThread");
    private final AtomicBoolean started = new AtomicBoolean();
    protected boolean shouldBreak;
    protected boolean hasBroken = false;
    protected boolean shouldPlace = true;
    float[] rotations;
    float[] targetRotations;
    BlockPos renderPos;
    BlockPos lastRenderPos;
    TimeAnimation alphaAnimation = null;
    Runnable postRunnable;
    boolean confirmed;
    boolean setSafe;
    ExecutorService floodService;
    FloodThread currentRunnable;
    boolean shouldStop;
    private Calculation currentCalc;
    private PlayerEntity currentTarget;
    private Entity currentCrystal;
    private boolean tick;
    private boolean switching;

    public CrystalAura() {
        super("CrystalAura", Category.Combat);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerPostKeys(this));
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerKeys(this));
        this.listeners.add(new ListenerPlace(this));
        this.listeners.add(new ListenerAttack(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerAnimation(this));
        this.breakHelper = new HelperBreak(this);
        this.placeHelper = new HelperPlace(this);
        this.setData(new CrystalAuraData(this));
        this.floodService = null;
        this.currentRunnable = null;
        this.shouldPlace = true;
        new PageBuilder<>(this, this.pages).addPage(v -> v == ACPage.Place, this.place, this.newerVer).addPage(v -> v == ACPage.Break, this.attack, this.instant).addPage(v -> v == ACPage.Misc, this.rotate, this.jitter).addPage(v -> v == ACPage.Dev, this.antiFeetPlace, this.legDelay).addPage(v -> v == ACPage.Render, this.fade, this.outlineColor).register(Visibilities.VISIBILITY_MANAGER);
    }

    @Override
    protected void onLoad() {
        if (!this.started.getAndSet(true)) {
            this.thread.start();
        }
    }

    @Override
    protected void onEnable() {
        this.shouldPlace = true;
        this.positions.clear();
        this.renderPos = null;
        this.alphaAnimation = null;
        this.animationTimer.reset();
        this.floodService = null;
        this.currentRunnable = null;
    }

    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "\u00a7aSwitching";
        }
        return this.currentTarget == null ? null : this.currentTarget.getName().getString();
    }

    public DiscreteTimer getBreakTimer() {
        return this.breakTimer;
    }

    public DiscreteTimer getPlaceTimer() {
        return this.placeTimer;
    }

    public PlayerEntity getTarget() {
        return this.currentTarget;
    }

    protected void setTarget(PlayerEntity target) {
        this.currentTarget = target;
        this.targetTimer.reset();
    }

    public Set<BlockPos> getPositions() {
        return this.positions;
    }

    protected void setTick(boolean tick) {
        this.tick = tick;
    }

    protected boolean canTick() {
        return !this.tick;
    }

    protected void runNonRotateThread(ThreadMode mode) {
        if (this.threadMode.getValue() == mode && this.multiThread.getValue().booleanValue() && this.rotate.getValue() == Rotate.None) {
            this.runThread();
        }
    }

    protected void runThread() {
        if (CrystalAura.mc.world != null && CrystalAura.mc.player != null && !this.isPingBypass()) {
            ArrayList<PlayerEntity> players = new ArrayList<PlayerEntity>(Managers.ENTITIES.getPlayers());
            ArrayList<Entity> crystals = new ArrayList<Entity>(Managers.ENTITIES.getEntities());
            Calculation calc = new Calculation(this, players, crystals);
            this.setCurrentCalc(calc);
            this.thread.submit(calc);
        }
    }

    protected Calculation getCurrentCalc() {
        return this.currentCalc;
    }

    protected void setCurrentCalc(Calculation calc) {
        this.currentCalc = calc;
    }

    protected void swing() {
        if (this.swing.getValue() != SwingType.None) {
            Swing.Client.swing(this.swing.getValue().getHand());
        }
    }

    public boolean isSwitching() {
        return this.switching;
    }

    public void setSwitching(boolean switching) {
        this.switching = switching;
    }

    protected void setUnsafe() {
        if (!this.setSafe) {
            Managers.SAFETY.setSafe(false);
            this.setSafe = true;
        }
    }

    public void setRenderPos(BlockPos pos) {
        if (pos != null || this.renderTimer.passed(250L)) {
            this.lastRenderPos = this.renderPos;
            this.renderPos = pos;
            if (this.alphaAnimation == null) {
                this.alphaAnimation = new TimeAnimation(this.animation.getValue().intValue(), 0.0, this.fillColor.getAlpha(), false, true, AnimationMode.LINEAR);
            } else if (this.animationTimer.passed(250L)) {
                this.alphaAnimation.play();
                this.alphaAnimation.setCurrent(0.0);
                this.animationTimer.reset();
            }
            this.renderTimer.reset();
        }
    }

    public Entity getCurrentCrystal() {
        return this.currentCrystal;
    }

    protected void setCurrentCrystal(Entity currentCrystal) {
        this.currentCrystal = currentCrystal;
    }

    protected boolean shouldFacePlace() {
        return this.holdFP.getValue() && mc.mouse.wasLeftButtonClicked() && !CrystalAura.mc.playerController.getIsHittingBlock() && !(CrystalAura.mc.player.getMainHandStack().getItem() instanceof PickaxeItem) && !(CrystalAura.mc.currentScreen instanceof GenericContainerScreen);
    }

    protected void checkKilled() {
        if (CrystalAura.mc.world != null && !this.isPingBypass()) {
            for (Map.Entry<Integer, EntityTime> entry : this.killed.entrySet()) {
                if (entry.getValue() == null) {
                    this.killed.remove(entry.getKey());
                    continue;
                }
                if (System.nanoTime() - entry.getValue().getTime() <= 500000000L) continue;
                Entity entity = entry.getValue().getEntity();
                entity.isDead = false;
                if (Managers.ENTITIES.getEntities().contains(entity)) continue;
                CrystalAura.mc.world.addEntity(entity);
                entity.isDead = false;
                this.killed.remove(entry.getKey());
            }
        }
    }

    protected void reset() {
        this.killed.clear();
        this.attacked.clear();
        this.positions.clear();
        this.slow.clear();
    }

    public boolean isPingBypass() {
        return this.pingBypass.getValue() && PINGBYPASS.isEnabled();
    }
}

