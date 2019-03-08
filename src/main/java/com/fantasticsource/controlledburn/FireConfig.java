package com.fantasticsource.controlledburn;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = ControlledBurn.MODID)
public class FireConfig
{
    @Comment({
            "",
            "",
            "How fast blocks are destroyed (burnt) when 'on fire', as a multiplier (2 means twice as fast, 0.5 means half as fast)",
            "AKA flammability multiplier",
            "Set this to 0 to make it so fire doesn't break (burn) blocks at all (but can still be lit)",
            "The default/vanilla value in MC version 1.12.2 is 1.0",
            ""
    })
    public static float fireBurnSpeedMultiplier = 1;

    @Comment({
            "",
            "",
            "How fast fire spreads, as a multiplier (2 means twice as fast, 0.5 means half as fast)",
            "AKA encouragement multiplier",
            "Does nothing if dontDestroyBlocks is set to true",
            "The default/vanilla value in MC version 1.12.2 is 1.0",
            ""
    })
    public static float fireSpreadSpeedMultiplier = 1;

    @Comment({
            "",
            "",
            "If set to true, fire ignores the fire resistance of humid biomes. Is it fire on ice, or ice on fire? (Disclaimer: Does not make ice flammable)",
            "This affects block destruction (burn) chance and natural fire spread chance in humid biomes",
            "The default/vanilla value in MC version 1.12.2 is false",
            ""
    })
    public static boolean ignoreHumidBiomes = false;

    @Comment({
            "",
            "",
            "If set to true, fire ignores rain. Not even mother nature can save you now!",
            "WHEN IT'S RAINING this affects chance of fire extinguishing from rain, chance of natural fire spread, and chance of fire spread when fire destroys (burns) a block",
            "The default/vanilla value in MC version 1.12.2 is false",
            ""
    })
    public static boolean ignoreRain = false;

    @Comment({
            "",
            "",
            "When a fire at the MAXIMUM age (15 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
            "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
            "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)",
            "The default/vanilla value in MC version 1.12.2 is 80",
            ""
    })
    public static int maxReplaceBlockWithFireChance = 80;

    @Comment({
            "",
            "",
            "When a fire at the MINIMUM age (0 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
            "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
            "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)",
            "The default/vanilla value in MC version 1.12.2 is 50",
            ""
    })
    public static int minReplaceBlockWithFireChance = 50;

    @Comment({
            "",
            "",
            "Whether fire spreading from lava is disabled",
            "The default/vanilla value in MC version 1.12.2 is false",
            ""
    })
    public static boolean noLavaFire = false;

    @Comment({
            "",
            "",
            "Whether fire spreading from lightning is disabled",
            "The default/vanilla value in MC version 1.12.2 is false",
            ""
    })
    public static boolean noLightningFire = false;

    @Comment({
            "",
            "",
            "The maximum upward distance fire can spread to instantaneously",
            "Increasing this too much can have a negative impact on server performance",
            "The default/vanilla value in MC version 1.12.2 is 4",
            ""
    })
    @Config.RangeInt(min = 0)
    public static int reachAbove = 4;

    @Comment({
            "",
            "",
            "The maximum downward distance fire can spread to instantaneously",
            "Increasing this too much can have a negative impact on server performance",
            "The default/vanilla value in MC version 1.12.2 is 1",
            ""
    })
    @Config.RangeInt(min = 0)
    public static int reachBelow = 1;

    @Comment({
            "",
            "",
            "The maximum horizontal distance fire can spread to instantaneously (eg. between two trees)",
            "Increasing this too much can have a negative impact on server performance",
            "The default/vanilla value in MC version 1.12.2 is 1",
            ""
    })
    @Config.RangeInt(min = 0)
    public static int reachHorizontal = 1;

    @Comment({
            "",
            "",
            "When fire spreads naturally, ie. not when it's spreading by destroying (burning) a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
            "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
            "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
            "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1.0 if you want vanilla mechanics in this regard",
            "The default/vanilla value in MC version 1.12.2 is -1",
            ""
    })
    @Config.RangeInt(min = -1, max = 100)
    public static int spreadStrengthNatural = -1;

    @Comment({
            "",
            "",
            "When fire spreads by destroying a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
            "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
            "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
            "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1.0 if you want vanilla mechanics in this regard",
            "The default/vanilla value in MC version 1.12.2 is -1",
            ""
    })
    @Config.RangeInt(min = -1, max = 100)
    public static int spreadStrengthWhenDestroying = -1;

    @Comment({
            "",
            "",
            "",
            "Here are some example setups:",
            "",
            "",
            "",
            "VANILLA",
            "This emulates normal vanilla fire behavior, and SHOULD BE the default settings when this mod is first installed",
            "",
            "fireBurnSpeedMultiplier=1.0",
            "fireSpreadSpeedMultiplier=1.0",
            "ignoreHumidBiomes=false",
            "ignoreRain=false",
            "maxReplaceBlockWithFireChance=80",
            "minReplaceBlockWithFireChance=50",
            "reachAbove=4",
            "reachBelow=1",
            "reachHorizontal=1",
            "spreadStrengthNatural=-1",
            "spreadStrengthWhenDestroying=-1",
            "",
            "",
            "",
            "ETERNAL FLAME",
            "This emulates vanilla behavior if you were to run '/gamerule doFireTick false' on a server",
            "Keep in mind that this does NOT ACTUALLY SET THE GAMERULE FLAG; the gamerule flag is completely separate from this mod, and still does its normal thing",
            "Also note that ONLY FIRE ON TOP OF FLAMMABLE BLOCKS WILL BE ETERNAL; other fire will still go out",
            "",
            "fireBurnSpeedMultiplier=0",
            "fireSpreadSpeedMultiplier=0",
            "ignoreHumidBiomes=false",
            "ignoreRain=false",
            "maxReplaceBlockWithFireChance=80",
            "minReplaceBlockWithFireChance=50",
            "reachAbove=4",
            "reachBelow=1",
            "reachHorizontal=1",
            "spreadStrengthNatural=-1",
            "spreadStrengthWhenDestroying=-1",
            "",
            "",
            "",
            "NO FIRE SPREAD",
            "This emulates the behavior of my 'No Fire Spread' mod, the precursor to this mod",
            "It prevents fire from spreading, but fire can still destroy (burn) blocks and be extinguished naturally",
            "",
            "fireBurnSpeedMultiplier=1.0",
            "fireSpreadSpeedMultiplier=0",
            "ignoreHumidBiomes=false",
            "ignoreRain=false",
            "maxReplaceBlockWithFireChance=80",
            "minReplaceBlockWithFireChance=50",
            "reachAbove=4",
            "reachBelow=1",
            "reachHorizontal=1",
            "spreadStrengthNatural=-1",
            "spreadStrengthWhenDestroying=-1",
            "",
            "",
            "",
            "ACIDIC FIRE",
            "A cool type of alternate fire behavior that I accidentally made while testing this mod",
            "Makes fire only spread when it destroys the block it is currently burning; similar to acid eroding something",
            "",
            "fireBurnSpeedMultiplier=1.0",
            "fireSpreadSpeedMultiplier=0",
            "ignoreHumidBiomes=false",
            "ignoreRain=false",
            "maxReplaceBlockWithFireChance=100",
            "minReplaceBlockWithFireChance=100",
            "reachAbove=4",
            "reachBelow=1",
            "reachHorizontal=1",
            "spreadStrengthNatural=-1",
            "spreadStrengthWhenDestroying=-1",
            "",
            "",
            "",
            "Oh and if you haven't already figured out, the variable below this comment does nothing (except allow me to put comments here)"
    })
    public static boolean zADummyVarForThisFooterCommentBecauseIDontKnowAnyOtherWayToPutOneHere = false;
}
