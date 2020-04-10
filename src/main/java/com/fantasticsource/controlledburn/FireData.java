package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
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
                    System.err.println("Block not found: " + token);
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
        for (String s : blockTransformations)
        {
            String[] tokens = s.split(",");
            if (tokens.length != 2)
            {
                System.err.println("Invalid block transformation entry: " + s);
                continue;
            }

            ArrayList<IBlockState> fromStates = blockstatesMatching(tokens[0]);
            ArrayList<IBlockState> toStates = blockstatesMatching(tokens[1]);
            if (fromStates == null || toStates == null || fromStates.size() == 0 || toStates.size() == 0)
            {
                System.err.println("Invalid block transformation entry: " + s);
                continue;
            }

            if (toStates.size() == 1)
            {
                for (IBlockState state : fromStates)
                {
                    blockTransformationMap.put(state, toStates.get(0));
                }
            }
            else if (fromStates.size() == toStates.size())
            {
                for (int i = 0; i < fromStates.size(); i++)
                {
                    blockTransformationMap.put(fromStates.get(i), toStates.get(i));
                }
            }
            else
            {
                for (IBlockState state : fromStates)
                {
                    blockTransformationMap.put(state, toStates.get(0));
                }
            }
        }
    }


    protected static ArrayList<IBlockState> blockstatesMatching(String blockID)
    {
        ArrayList<IBlockState> result = new ArrayList<>();

        String[] tokens = blockID.split(":");
        String domain = "minecraft", name, meta = "*";
        switch (tokens.length)
        {
            case 1:
                name = tokens[0].trim();
                break;


            case 2:
                if (tokens[1].trim().equals("*"))
                {
                    name = tokens[0].trim();
                    break;
                }

                try
                {
                    meta = "" + Integer.parseInt(tokens[1].trim());
                    name = tokens[0].trim();
                }
                catch (NumberFormatException e)
                {
                    domain = tokens[0].trim();
                    name = tokens[1].trim();
                }
                break;


            case 3:
                domain = tokens[0].trim();
                name = tokens[1].trim();
                meta = tokens[2].trim();
                break;


            default:
                System.err.println("Invalid blockstate entry: " + tokens[1]);
                return null;
        }


        ResourceLocation rl = new ResourceLocation(domain, name);
        if (!ForgeRegistries.BLOCKS.containsKey(rl))
        {
            System.err.println("Block not found: " + rl);
            return null;
        }

        Block block = ForgeRegistries.BLOCKS.getValue(rl);
        if (block == null)
        {
            System.err.println("Block was null: " + rl);
            return null;
        }


        int i;
        try
        {
            i = Integer.parseInt(meta);
            IBlockState state;
            try
            {
                state = block.getStateFromMeta(i);
                result.add(state);
            }
            catch (Exception e2)
            {
            }
        }
        catch (NumberFormatException e)
        {
            for (i = 0; i < 16; i++)
            {
                IBlockState state;
                try
                {
                    state = block.getStateFromMeta(i);
                }
                catch (Exception e2)
                {
                    continue;
                }
                if (!result.contains(state)) result.add(state);
            }
        }

        return result;
    }
}
