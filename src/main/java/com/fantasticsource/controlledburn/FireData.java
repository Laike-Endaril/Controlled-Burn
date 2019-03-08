package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static com.fantasticsource.controlledburn.FireConfig.blockSettings;
import static com.fantasticsource.controlledburn.FireConfig.burnSpreadChances;

public class FireData
{
    public static int replaceBlockWithFireChanceRange;

    public static void update()
    {
        replaceBlockWithFireChanceRange = burnSpreadChances.maxBurnSpreadChance - burnSpreadChances.minBurnSpreadChance;


        ResourceLocation resourceLocation;
        Block b;
        String token;
        int f, e;
        for (String string : blockSettings)
        {
            String[] tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for block-specific setting; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                resourceLocation = new ResourceLocation(token);

                if (!ForgeRegistries.BLOCKS.containsKey(resourceLocation))
                {
                    System.err.println("Could not find block: " + token);
                }
                else
                {
                    b = ForgeRegistries.BLOCKS.getValue(resourceLocation);

                    if (b == null)
                    {
                        System.err.println("Block was found, but was null (this should never happen): " + token);
                    }
                    else
                    {
                        token = tokens[1].trim();
                        if (token.equals("=")) f = Blocks.FIRE.getFlammability(b);
                        else f = Integer.parseInt(token);

                        token = tokens[2].trim();
                        if (token.equals("=")) e = Blocks.FIRE.getEncouragement(b);
                        else e = Integer.parseInt(token);

                        Blocks.FIRE.setFireInfo(b, e, f);
                    }
                }
            }
        }
    }
}
