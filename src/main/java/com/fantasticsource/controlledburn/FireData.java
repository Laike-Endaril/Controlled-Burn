package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedHashMap;

import static com.fantasticsource.controlledburn.FireConfig.*;

public class FireData
{
    public static int replaceBlockWithFireChanceRange;
    public static LinkedHashMap<IBlockState, IBlockState> blockTransformationMap = new LinkedHashMap<>();

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


        blockTransformationMap.clear();
        IBlockState blockStateFrom, blockStateTo;
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        ResourceLocation rl;
        for (String s : blockTransformations)
        {
            String[] tokens = s.split(",");
            if (tokens.length != 2)
            {
                System.err.println("Invalid block transformation entry: " + s);
                continue;
            }

            String domain1 = "minecraft", name1;
            int meta1 = 0;
            String[] tokens1 = tokens[0].trim().split(":");
            switch (tokens1.length)
            {
                case 1:
                    name1 = tokens1[0].trim();
                    break;

                case 2:
                    try
                    {
                        meta1 = Integer.parseInt(tokens1[1].trim());
                        name1 = tokens1[0].trim();
                    }
                    catch (NumberFormatException e2)
                    {
                        domain1 = tokens1[0].trim();
                        name1 = tokens1[1].trim();
                    }
                    break;

                case 3:
                    domain1 = tokens1[0].trim();
                    name1 = tokens1[1].trim();
                    try
                    {
                        meta1 = Integer.parseInt(tokens1[2].trim());
                    }
                    catch (NumberFormatException e2)
                    {
                        System.err.println("Invalid blockstate entry: " + tokens[0]);
                        return;
                    }
                    break;

                default:
                    System.err.println("Invalid blockstate entry: " + tokens[0]);
                    return;
            }
            rl = new ResourceLocation(domain1, name1);
            if (!blocks.containsKey(rl))
            {
                System.err.println("Block not found: " + tokens[0].trim());
                continue;
            }
            try
            {
                blockStateFrom = blocks.getValue(rl).getStateFromMeta(meta1);
            }
            catch (Exception e3)
            {
                System.err.println("Bad meta for blockstate: " + tokens[0]);
                throw e3;
            }

            String domain2 = "minecraft", name2;
            int meta2 = 0;
            String[] tokens2 = tokens[1].trim().split(":");
            switch (tokens2.length)
            {
                case 1:
                    name2 = tokens2[0].trim();
                    break;

                case 2:
                    try
                    {
                        meta2 = Integer.parseInt(tokens2[1].trim());
                        name2 = tokens2[0].trim();
                    }
                    catch (NumberFormatException e2)
                    {
                        domain2 = tokens2[0].trim();
                        name2 = tokens2[1].trim();
                    }
                    break;

                case 3:
                    domain2 = tokens2[0].trim();
                    name2 = tokens2[1].trim();
                    try
                    {
                        meta2 = Integer.parseInt(tokens2[2].trim());
                    }
                    catch (NumberFormatException e2)
                    {
                        System.err.println("Invalid blockstate entry: " + tokens[1]);
                        return;
                    }
                    break;

                default:
                    System.err.println("Invalid blockstate entry: " + tokens[1]);
                    return;
            }
            rl = new ResourceLocation(domain2, name2);
            if (!blocks.containsKey(rl))
            {
                System.err.println("Block not found: " + tokens[1].trim());
                continue;
            }
            try
            {
                blockStateTo = blocks.getValue(rl).getStateFromMeta(meta2);
            }
            catch (Exception e3)
            {
                System.err.println("Bad meta for blockstate: " + tokens[1]);
                throw e3;
            }

            blockTransformationMap.put(blockStateFrom, blockStateTo);
        }
    }
}
