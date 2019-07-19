package com.fantasticsource.controlledburn;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = ControlledBurn.MODID)
public class FireConfig
{
    @Config.Name("Global Multipliers")
    @Config.LangKey(ControlledBurn.MODID + ".config.globalMultipliers")
    @Comment({
            "Multipliers for how fast fire naturally spreads and how fast fire burns blocks",
            "",
            "Also includes the setting for how often fire updates (which makes everything fire-related faster or slower)"
    })
    public static GlobalMultipliers globalMultipliers = new GlobalMultipliers();

    @Config.LangKey(ControlledBurn.MODID + ".config.specialToggles")
    @Comment({"Includes whether certain sources can start fires, whether fire ignores rain, and other oddities"})
    public static SpecialToggles specialToggles = new SpecialToggles();

    @Config.LangKey(ControlledBurn.MODID + ".config.burnSpreadChances")
    @Comment({"These determine how often fire spreads to adjacent blocks after burning a block"})
    public static BurnSpreadChances burnSpreadChances = new BurnSpreadChances();

    @Config.Name("Natural Fire Spread Ranges")
    @Config.LangKey(ControlledBurn.MODID + ".config.spreadRanges")
    @Comment({"How far fire can naturally spread from one block to another in each direction"})
    public static FireSpreadRanges spreadRanges = new FireSpreadRanges();

    @Config.Name("Spread Strengths")
    @Config.LangKey(ControlledBurn.MODID + ".config.spreadStrengths")
    @Comment({"How much 'life' new fires have when spreading"})
    public static SpreadStrengths spreadStrengths = new SpreadStrengths();

    @Config.Name("Block-Specific Settings")
    @Config.LangKey(ControlledBurn.MODID + ".config.blockSpecific")
    @Comment({
            "This allows you to set a custom BASE flammability and encouragement for any given block",
            "",
            "The EFFECTIVE stats of the block will be these values times their respective multipliers",
            "",
            "Flammability is how fast/easily the block is destroyed by fire",
            "",
            "Encouragement is how fast/easily fire spreads to the block",
            "",
            "The equals symbol can be used to leave a base stat as-is (eg, if you only want to change one stat)",
            "",
            "Syntax is [blockID, flammability, encouragement]. Examples below",
            "minecraft:grass, 5, 5",
            "minecraft:dirt, =, 5"
    })
    public static String[] blockSettings = {};

    @Config.Name("Block Transformations")
    @Config.LangKey(ControlledBurn.MODID + ".config.blockTransformations")
    @Comment({
            "This allows you to set what a block will become when it burns",
            "Eg. you can make grass blocks turn to dirt by using this setting:",
            "minecraft:grass, minecraft:dirt",
            "",
            "Keep in mind that this won't do anything unless you make sure the first block is actually flammable (and maybe give it encouragement too, depending; see the MC wiki for more info on those stats)"
    })
    public static String[] blockTransformations = {};

    public static class GlobalMultipliers
    {
        @Config.Name("Burn Speed Multiplier")
        @Config.LangKey(ControlledBurn.MODID + ".config.burnSpeedMultiplier")
        @Comment({
                "How fast blocks are destroyed (burnt) when 'on fire', as a multiplier (2 means twice as fast, 0.5 means half as fast)",
                "",
                "AKA flammability multiplier",
                "",
                "Set this to 0 to make it so fire doesn't break (burn) blocks at all (but can still be lit)"
        })
        @Config.RangeDouble(min = 0)
        public double burnSpeedMultiplier = 1;

        @Config.Name("Spread Speed Multiplier")
        @Config.LangKey(ControlledBurn.MODID + ".config.spreadSpeedMultiplier")
        @Comment({
                "How fast fire spreads, as a multiplier (2 means twice as fast, 0.5 means half as fast)",
                "",
                "AKA encouragement multiplier"
        })
        @Config.RangeDouble(min = 0)
        public double spreadSpeedMultiplier = 1;

        @Config.Name("Tick Delay")
        @Config.LangKey(ControlledBurn.MODID + ".config.tickDelay")
        @Comment({
                "The number of game ticks before fire blocks update themselves",
                "",
                "The lower you set this, the faster fire works (in every way)"
        })
        @Config.RangeInt(min = 1)
        public int tickDelay = 30;
    }

    public static class SpecialToggles
    {
        @Config.Name("Ignore Humid Biomes")
        @Config.LangKey(ControlledBurn.MODID + ".config.ignoreHumid")
        @Comment({
                "If set to true, fire ignores the fire resistance of humid biomes",
                "",
                "This affects block destruction (burn) chance and natural fire spread chance in humid biomes"
        })
        public boolean ignoreHumidBiomes = false;

        @Config.Name("Ignore Rain")
        @Config.LangKey(ControlledBurn.MODID + ".config.ignoreRain")
        @Comment({
                "If set to true, fire ignores rain",
                "",
                "WHEN IT'S RAINING this affects chance of fire extinguishing from rain, chance of natural fire spread, and chance of fire spread when fire destroys (burns) a block"
        })
        public boolean ignoreRain = false;

        @Config.Name("Disable Fires From Lava")
        @Config.LangKey(ControlledBurn.MODID + ".config.lavaFire")
        @Comment({"Whether fire spreading from lava is disabled"})
        public boolean noLavaFire = false;

        @Config.Name("Disable Fires From Lightning")
        @Config.LangKey(ControlledBurn.MODID + ".config.lightningFire")
        @Comment({"Whether fire spreading from lightning is disabled"})
        public boolean noLightningFire = false;
    }

    public static class BurnSpreadChances
    {
        @Config.Name("Max Burn Spread Chance")
        @Config.LangKey(ControlledBurn.MODID + ".config.maxBurnSpreadChance")
        @Comment({
                "When a fire at the MAXIMUM age (15 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
                "",
                "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
                "",
                "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)"
        })
        @Config.RangeInt(min = 0, max = 100)
        public int maxBurnSpreadChance = 80;

        @Config.Name("Min Burn Spread Chance")
        @Config.LangKey(ControlledBurn.MODID + ".config.minBurnSpreadChance")
        @Comment({
                "When a fire at the MINIMUM age (0 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
                "",
                "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
                "",
                "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)"
        })
        @Config.RangeInt(min = 0, max = 100)
        public int minBurnSpreadChance = 50;
    }

    public static class FireSpreadRanges
    {
        @Config.Name("Reach (Upwards)")
        @Config.LangKey(ControlledBurn.MODID + ".config.reachUp")
        @Comment({"The maximum upward distance fire can naturally spread to when updating (eg. between two trees)"})
        @Config.RangeInt(min = 0)
        public int reachAbove = 4;

        @Config.Name("Reach (Downwards)")
        @Config.LangKey(ControlledBurn.MODID + ".config.reachDown")
        @Comment({"The maximum downward distance fire can naturally spread to when updating (eg. between two trees)"})
        @Config.RangeInt(min = 0)
        public int reachBelow = 1;

        @Config.Name("Reach (Sideways)")
        @Config.LangKey(ControlledBurn.MODID + ".config.reachSide")
        @Comment({"The maximum horizontal distance fire can naturally spread to when updating (eg. between two trees)"})
        @Config.RangeInt(min = 0)
        public int reachHorizontal = 1;
    }

    public static class SpreadStrengths
    {
        @Config.Name("Spread Strength (Block Burning)")
        @Config.LangKey(ControlledBurn.MODID + ".config.burnSpreadStrength")
        @Comment({
                "When fire spreads by destroying a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
                "",
                "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
                "",
                "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
                "",
                "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1 if you want vanilla mechanics in this regard"
        })
        @Config.RangeInt(min = -1, max = 100)
        public int burnSpreadStrength = -1;

        @Config.Name("Spread Strength (Natural Spread)")
        @Config.LangKey(ControlledBurn.MODID + ".config.naturalSpreadStrength")
        @Comment({
                "When fire spreads naturally, ie. not when it's spreading by destroying (burning) a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
                "",
                "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
                "",
                "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
                "",
                "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1 if you want vanilla mechanics in this regard"
        })
        @Config.RangeInt(min = -1, max = 100)
        public int naturalSpreadStrength = -1;
    }
}
