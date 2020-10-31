package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.controlledburn.FireConfig.*;

public class FireData
{
    public static int replaceBlockWithFireChanceRange;
    public static LinkedHashMap<IBlockState, IBlockState> blockTransformationMap = new LinkedHashMap<>();

    public static void update()
    {
        replaceBlockWithFireChanceRange = burnSpreadChances.maxBurnSpreadChance - burnSpreadChances.minBurnSpreadChance;


        HashSet<Block> blocks;
        String token;
        int flammability = 0, encouragement = 0;
        boolean sameFlammability = false, sameEncouragement = false;

        for (String string : blockSettings)
        {
            String[] tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for block-specific setting; please check example in tooltip");
            else
            {
                blocks = blocksMatching(tokens[0].trim());

                if (blocks.size() == 0)
                {
                    System.err.println("Block(s) not found: " + tokens[0].trim());
                }
                else
                {
                    token = tokens[1].trim();
                    if (token.equals("=")) sameFlammability = true;
                    else flammability = Integer.parseInt(token);

                    token = tokens[2].trim();
                    if (token.equals("=")) sameEncouragement = true;
                    else encouragement = Integer.parseInt(token);

                    for (Block b : blocks)
                    {
                        Blocks.FIRE.setFireInfo(b, sameEncouragement ? ControlledBurn.OLD_FIRE.getEncouragement(b) : encouragement, sameFlammability ? ControlledBurn.OLD_FIRE.getFlammability(b) : flammability);
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

            if (toStates.size() > 1 && fromStates.size() == toStates.size())
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


    protected static HashSet<Block> blocksMatching(String blockID)
    {
        HashSet<Block> blocks = new HashSet<>();

        ResourceLocation resourceLocation = new ResourceLocation(blockID);
        Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
        if (block != null && block != Blocks.AIR) blocks.add(block);
        else if (blockID.contains("oredict:"))
        {
            for (ItemStack stack : OreDictionary.getOres(blockID.replace("oredict:", "")))
            {
                block = Block.getBlockFromItem(stack.getItem());
                if (block != null && block != Blocks.AIR) blocks.add(block); //block CAN be null here
            }
        }

        return blocks;
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


        HashSet<Block> blocks;
        if (domain.equals("oredict")) blocks = blocksMatching(domain + ":" + name);
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(domain, name));
            if (block == null || block == Blocks.AIR) return result;

            blocks = new HashSet<>();
            blocks.add(block);
        }


        for (Block block : blocks)
        {
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
        }

        return result;
    }
}
