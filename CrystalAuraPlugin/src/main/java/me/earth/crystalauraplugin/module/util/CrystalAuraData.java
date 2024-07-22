package me.earth.crystalauraplugin.module.util;

import me.earth.crystalauraplugin.module.CrystalAura;
import me.earth.earthhack.api.module.data.DefaultData;

public class CrystalAuraData
        extends DefaultData<CrystalAura> {
    public CrystalAuraData(CrystalAura autoCrystal) {
        super(autoCrystal);
        this.descriptions.put(this.module.getSetting("Place"), "Decides if we place crystals or not.");
        this.descriptions.put(this.module.getSetting("Target"), "Which Players to target. -Closest targets the closest player -Damage targets the player we can damage most(CPU intensive)");
        this.descriptions.put(this.module.getSetting("PlaceRange"), "Max distance from you to the position to place on.");
        this.descriptions.put(this.module.getSetting("PlaceTrace"), "Max distance through walls, to the position to place on. (Most severs allow the same wall- and normal range for placing crystals)");
        this.descriptions.put(this.module.getSetting("MinDamage"), "Minimum damage a crystal we place has to deal to the enemy.");
        this.descriptions.put(this.module.getSetting("PlaceDelay"), "Delay (in ms) between each time we place a crystal.");
        this.descriptions.put(this.module.getSetting("MaxSelfPlace"), "Maximum damage a crystal we place can deal to us.");
        this.descriptions.put(this.module.getSetting("FacePlace"), "If the targets health is below this value we ignore MinDamage and faceplace him.");
        this.descriptions.put(this.module.getSetting("MultiPlace"), "Maximum amount of crystals dealing damage that can exist at the same time.");
        this.descriptions.put(this.module.getSetting("CountMin"), "If off : only count crystals that deal more than minDamage towards the MultiPlace value. Recommended On if you have higher Ping.");
        this.descriptions.put(this.module.getSetting("AntiSurround"), "Places on positions that already have crystals on them to speed up the CrystalAura. (Has similar effects to SetDead)");
        this.descriptions.put(this.module.getSetting("1.13+"), "After version 1.13 it's possible to place crystals in 1 block high spaces. Use for ViaVersion servers.");
        this.descriptions.put(this.module.getSetting("Attack"), "When to attack: -Always well, attacks always -BreakSlot Only attack if we are holding a crystal, this is very recommended, since you can leave the CrystalAura on at all times -Calc Same as breakslot but will show an ESP.");
        this.descriptions.put(this.module.getSetting("Break"), "Decides if CrystalAura attacks crystals or not.");
        this.descriptions.put(this.module.getSetting("BreakRange"), "Max distance between you and the crystal you want to break.");
        this.descriptions.put(this.module.getSetting("BreakTrace"), "Max distance through walls between you and the crystal you want to break. Most servers are a lot more strict with this than with place wall range. For Orientation: Vanilla servers have a wall range of 3 blocks.");
        this.descriptions.put(this.module.getSetting("MinBreakDmg"), "Minimum damage that a crystal has to deal to the enemy to be attacked by us.");
        this.descriptions.put(this.module.getSetting("SlowBreak"), "Crystals dealing damage that lies between MinBreakDmg and this value will be broken slowly with the given SlowDelay.");
        this.descriptions.put(this.module.getSetting("SlowDelay"), "Delay that crystals that deal less Damage than SlowBreak get blown up with.");
        this.descriptions.put(this.module.getSetting("BreakDelay"), "Delay between each time we attack a crystal.");
        this.descriptions.put(this.module.getSetting("MaxSelfBreak"), "Maximum damage a crystal we break can deal to us.");
        this.descriptions.put(this.module.getSetting("Instant"), "Attacks crystals immediately when they spawn. You can't rotate using this. Can speed up CrystalAura by up to 100%. (Previously known as Predict)");
        this.descriptions.put(this.module.getSetting("Rotate"), "Some AntiCheats require you to look at the positions you place/break on : -None don't rotate -Break only rotate for breaking crystals -Place only rotate for placing crystals -All rotate for both placing/breaking");
        this.descriptions.put(this.module.getSetting("Stay"), "Keeps the rotations to a position from placement til attacking.");
        this.descriptions.put(this.module.getSetting("MultiThread"), "Especially Target - Damage can be heavy for the CPU, this will transfer the calculations to another Thread which will make the CrystalAura eat up less FPS. It's possible to rotate using this, but not recommended.");
        this.descriptions.put(this.module.getSetting("Suicide"), "Only recommended if you run around with 20 Totem kits. Goes all out and will ignore damage dealt to you.");
        this.descriptions.put(this.module.getSetting("Range"), "Distance from crystal to target.");
        this.descriptions.put(this.module.getSetting("Override"), "Ignore MinBreakDmg and MinPlaceDmg if we can deal lethal damage to the target.");
        this.descriptions.put(this.module.getSetting("MinFace"), "MinDamage for Faceplacing.");
        this.descriptions.put(this.module.getSetting("AntiFriendPop"), "Calculates damage dealt to friends.");
        this.descriptions.put(this.module.getSetting("AutoSwitch"), "Automatically switches to crystals: -None never switch -Bind use a bind to toggle switching on and off -Always always switch.");
        this.descriptions.put(this.module.getSetting("MainHand"), "If On, AutoSwitch will switch to the main hand, if Off, to the off hand if the Offhand module is enabled.");
        this.descriptions.put(this.module.getSetting("SwitchBind"), "The bind for AutoSwitch Mode Bind.");
        this.descriptions.put(this.module.getSetting("SwitchBack"), "If the SwitchBind is pressed again, while we are holding crystals in Offhand -> switch to Totems in Offhand.");
        this.descriptions.put(this.module.getSetting("Swing"), "Determines whether and with which Arm to swing with.");
        this.descriptions.put(this.module.getSetting("CombinedTrace"), "Prevents placing Crystals that don't lie within the break wall range.");
        this.descriptions.put(this.module.getSetting("SetDead"), "Removes crystals after we attacked them (Client sided), which can speed up CrystalAura. Similar effects to AntiSurround, but will send less attack packets.");
        this.descriptions.put(this.module.getSetting("Cooldown"), "Most servers have a cooldown within which you can't attack entities after you switched your mainhand slot. Attacking crystals during this time can cause the AntiCheat to flag you. This setting prevents that by waiting for the given delay (ms).");
        this.descriptions.put(this.module.getSetting("PartialTicks"), "Only touch if you read the code. Required when you want to use Multithreading and Rotate.");
        this.descriptions.put(this.module.getSetting("FallBack"), "Due to the Damage calculation crystals that deal no damage to you and the enemy can block high damaging positions. This setting will cause the CrystalAura to break such Crystals.");
        this.descriptions.put(this.module.getSetting("FB-Dmg"), "Max Damage a FallBack crystal can deal to you.");
        this.descriptions.put(this.module.getSetting("SoundRemove"), "Explosion sounds arrive before the actual Explosion at our client. It can be used to improve CrystalAura speeds.");
        this.descriptions.put(this.module.getSetting("HoldFP"), "Faceplaces while you hold the left Mouse button.");
        this.descriptions.put(this.module.getSetting("MultiTask"), "Recommended On. If Off: won't place in ticks that we attacked a crystal in.");
        this.descriptions.put(this.module.getSetting("ThreadMode"), "The entry point for starting a Thread when MultiThreading. Recommended ones would be Pre and Delay.");
        this.descriptions.put(this.module.getSetting("ThreadDelay"), "Delay between each Thread when ThreadMode is Delay. Low thread delays can be intensive.");
        this.descriptions.put(this.module.getSetting("ID-Predict"), "Purely a fun setting. Drastically increases your chances of getting kicked, but can reach the theoretical speed limit of up to 20 crystals/second.");
        this.descriptions.put(this.module.getSetting("NoOffhandParticles"), "Blocks the particles that appear when attacking crystals with a weapon in your mainhand.");
        this.descriptions.put(this.module.getSetting("PingBypass"), "Pure Convenience so you don't need to toggle this module when you use PingBypass. Stops this module from doing anything while PingBypass is enabled, so that the Server-CrystalAura won't be disturbed. Also makes it so that instead of toggling the Switch, the CrystalAura SwitchBind just functions as the Offhand CrystalBind.");
        this.descriptions.put(this.module.getSetting("Interpolate"), "Currently in BETA! Predicts where players go.");
        this.descriptions.put(this.module.getSetting("LegSwitch"), "Takes the LegSwitch module into account.");
    }

    @Override
    public int getColor() {
        return -65536;
    }

    @Override
    public String getDescription() {
        return "Breaks and places Crystals.";
    }
}

