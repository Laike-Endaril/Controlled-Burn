package com.fantasticsource.controlledburn;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

import java.util.LinkedHashMap;
import java.util.Map;

@Config(modid = ControlledBurn.MODID)
public class FireConfig
{
    @Comment({
            "Multipliers for how fast fire naturally spreads and how fast fire burns blocks",
            "",
            "Also includes the setting for how often fire updates (which makes everything fire-related faster or slower)"
    })
    public static GlobalMultipliers global_multipliers = new GlobalMultipliers();
    public static class GlobalMultipliers
    {
        @Comment({
                "",
                "",
                "How fast blocks are destroyed (burnt) when 'on fire', as a multiplier (2 means twice as fast, 0.5 means half as fast)",
                "",
                "AKA flammability multiplier",
                "",
                "Set this to 0 to make it so fire doesn't break (burn) blocks at all (but can still be lit)",
                "",
                "Default/vanilla is 1",
                ""
        })
        @Config.RangeDouble(min = 0)
        public double burn_speed_multiplier = 1;

        @Comment({
                "",
                "",
                "How fast fire spreads, as a multiplier (2 means twice as fast, 0.5 means half as fast)",
                "",
                "AKA encouragement multiplier",
                "",
                "Does nothing if dontDestroyBlocks is set to true",
                "",
                "Default/vanilla is 1",
                ""
        })
        @Config.RangeDouble(min = 0)
        public double spread_speed_multiplier = 1;

        @Comment({
                "",
                "",
                "The number of game ticks before fire blocks update themselves",
                "",
                "The lower you set this, the faster fire works (in every way)",
                "",
                "Default/vanilla is 30",
                ""
        })
        @Config.RangeInt(min = 1)
        public int tick_delay = 30;
    }



    @Comment({
            "Includes whether certain sources can start fires, whether fire ignores rain, and other oddities"
    })
    public static SpecialToggles special_toggles = new SpecialToggles();
    public static class SpecialToggles
    {
        @Comment({
                "",
                "",
                "If set to true, fire ignores the fire resistance of humid biomes",
                "",
                "This affects block destruction (burn) chance and natural fire spread chance in humid biomes",
                "",
                "Default/vanilla is false",
                ""
        })
        public boolean ignore_humid_biomes = false;

        @Comment({
                "",
                "",
                "If set to true, fire ignores rain",
                "",
                "WHEN IT'S RAINING this affects chance of fire extinguishing from rain, chance of natural fire spread, and chance of fire spread when fire destroys (burns) a block",
                "",
                "Default/vanilla is false",
                ""
        })
        public boolean ignore_rain = false;

        @Comment({
                "",
                "",
                "Whether fire spreading from lava is disabled",
                "",
                "Default/vanilla is false",
                ""
        })
        public boolean no_lava_fire = false;

        @Comment({
                "",
                "",
                "Whether fire spreading from lightning is disabled",
                "",
                "Default/vanilla is false",
                ""
        })
        public boolean no_lightning_fire = false;
    }



    @Comment({
            "These determine how often fire spreads to adjacent blocks after burning a block"
    })
    public static BurnSpreadChances burn_spread_chances = new BurnSpreadChances();
    public static class BurnSpreadChances
    {
        @Comment({
                "",
                "",
                "When a fire at the MAXIMUM age (15 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
                "",
                "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
                "",
                "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)",
                "",
                "Default/vanilla is 80",
                ""
        })
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        public int max_burn_spread_chance = 80;

        @Comment({
                "",
                "",
                "When a fire at the MINIMUM age (0 in vanilla) destroys (burns through) a block, the chance that the broken (burnt) block is replaced with fire",
                "",
                "Does nothing if fire never destroys blocks, eg. if dontDestroyBlocks is set to true",
                "",
                "Uses a full-number percentage (25 means 25% chance, 75 means 75% chance)",
                "",
                "Default/vanilla is 50",
                ""
        })
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        public int min_burn_spread_chance = 50;
    }



    @Comment({
            "How far fire can naturally spread from one block to another in each direction"
    })
    public static FireSpreadRanges fire_spread_reach = new FireSpreadRanges();
    public static class FireSpreadRanges
    {
        @Comment({
                "",
                "",
                "The maximum upward distance fire can naturally spread to when updating (eg. between two trees)",
                "",
                "Default/vanilla is 4",
                ""
        })
        @Config.RangeInt(min = 0)
        public int reach_above = 4;

        @Comment({
                "",
                "",
                "The maximum downward distance fire can naturally spread to when updating (eg. between two trees)",
                "",
                "Default/vanilla is 1",
                ""
        })
        @Config.RangeInt(min = 0)
        public int reach_below = 1;

        @Comment({
                "",
                "",
                "The maximum horizontal distance fire can naturally spread to when updating (eg. between two trees)",
                "",
                "Default/vanilla is 1",
                ""
        })
        @Config.RangeInt(min = 0)
        public int reach_horizontal = 1;
    }



    @Comment({
            "How much \"life\" new fires have when spreading"
    })
    public static SpreadStrengths spread_strengths = new SpreadStrengths();
    public static class SpreadStrengths
    {
        @Comment({
                "",
                "",
                "When fire spreads by destroying a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
                "",
                "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
                "",
                "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
                "",
                "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1 if you want vanilla mechanics in this regard",
                "",
                "Default/vanilla is -1",
                ""
        })
        @Config.RangeInt(min = -1, max = 100)
        public int burn_spread_strength = -1;

        @Comment({
                "",
                "",
                "When fire spreads naturally, ie. not when it's spreading by destroying (burning) a block, the new fire's duration is set to the duration of the fire it came from, multiplied by this",
                "",
                "Fire's lifetime is only measured in integers from 0 to 15, so if you set the percentage to anything lower than 7 it will be the same as if you set it to 0",
                "",
                "Uses a full-number percentage (25 means 25% strength, 75 means 75% strength)",
                "",
                "The normal vanilla fire mechanics don't use a percentage reduction like this, so set this to -1 if you want vanilla mechanics in this regard",
                "",
                "Default/vanilla is -1",
                ""
        })
        @Config.RangeInt(min = -1, max = 100)
        public int natural_spread_strength = -1;
    }

    @Comment({
            "This allows you to set a custom BASE flammability for any given block",
            "",
            "The EFFECTIVE flammability of the block will be this value times burn_speed_multiplier",
            "",
            "This is how fast/easily the block is destroyed by fire",
            ""
    })
    public static Map<String, Integer> block_flammabilities = new LinkedHashMap<>();

    @Comment({
            "This allows you to set a custom BASE encouragement for any given block",
            "",
            "The EFFECTIVE encouragement of the block will be this value times spread_speed_multiplier",
            "",
            "This is how fast/easily fire spreads to the block",
            ""
    })
    public static Map<String, Integer> block_encouragements = new LinkedHashMap<>();
}
