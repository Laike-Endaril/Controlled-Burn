package com.fantasticsource.controlledburn;

import com.fantasticsource.mctools.event.BlockTick;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.fantasticsource.controlledburn.FireConfig.*;

public class FireData
{
    public static int replaceBlockWithFireChanceRange;
    public static LinkedHashMap<FireDataFilter, IBlockState> blockTransformationMap = new LinkedHashMap<>();
    public static LinkedHashMap<FireDataFilter, Boolean> fireSourceBlocks = new LinkedHashMap<>();
    public static LinkedHashSet<FireDataFilter> blockSpreadsFire = new LinkedHashSet<>();

    public static void update()
    {
        replaceBlockWithFireChanceRange = burnSpreadChances.maxBurnSpreadChance - burnSpreadChances.minBurnSpreadChance;


        HashSet<Block> blocks;
        String token;
        int flammability = 0, encouragement = 0;
        boolean sameFlammability = false, sameEncouragement = false, good;
        Biome biome;
        FireDataFilter filter;


        for (String string : blockSettings)
        {
            String[] tokens = string.split(",");
            if (tokens.length != 3)
            {
                System.err.println("Wrong number of arguments for block-specific setting; please check example in tooltip");
                continue;
            }


            blocks = blocksMatching(tokens[0].trim());
            if (blocks.size() == 0)
            {
                System.err.println("Block(s) not found: " + tokens[0].trim());
                continue;
            }


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


        blockTransformationMap.clear();
        for (String s : blockTransformations)
        {
            String[] tokens = s.split(",");
            if (tokens.length < 2)
            {
                System.err.println("Not enough arguments for transformation entry: " + s);
                continue;
            }


            ArrayList<IBlockState> fromStates = blockstatesMatching(tokens[0].trim());
            ArrayList<IBlockState> toStates = blockstatesMatching(tokens[1].trim(), true);
            if (fromStates == null || toStates == null || fromStates.size() == 0 || toStates.size() == 0)
            {
                System.err.println("One or more blocks not found for transformation entry: " + s);
                continue;
            }


            filter = new FireDataFilter();
            good = true;
            for (int i = 2; i < tokens.length; i++)
            {
                token = tokens[i].trim();
                try
                {
                    filter.dimensions.add(Integer.parseInt(token));
                }
                catch (NumberFormatException e)
                {
                    biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(token));
                    if (biome != null) filter.biomes.add(biome);
                    else
                    {
                        System.err.println("Bad dimension number or biome name for transformation entry: " + token);
                        good = false;
                        break;
                    }
                }
            }
            if (!good) continue;


            filter.blockStates.addAll(fromStates);
            blockTransformationMap.put(filter, toStates.get(0));
        }


        fireSourceBlocks.clear();
        for (String s : FireConfig.fireSourceBlocks)
        {
            String[] tokens = s.split(",");
            if (tokens.length < 2)
            {
                System.err.println("Not enough arguments for fire source entry: " + s);
                continue;
            }

            ArrayList<IBlockState> fromStates = blockstatesMatching(tokens[0]);
            if (fromStates == null || fromStates.size() == 0)
            {
                System.err.println("Block(s) not found for fire source entry: " + s);
                continue;
            }


            filter = new FireDataFilter();
            good = true;
            for (int i = 2; i < tokens.length; i++)
            {
                token = tokens[i].trim();
                try
                {
                    filter.dimensions.add(Integer.parseInt(token));
                }
                catch (NumberFormatException e)
                {
                    biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(token));
                    if (biome != null) filter.biomes.add(biome);
                    else
                    {
                        System.err.println("Bad dimension number or biome name for fire source entry: " + token);
                        good = false;
                        break;
                    }
                }
            }
            if (!good) continue;


            filter.blockStates.addAll(fromStates);
            fireSourceBlocks.put(filter, Boolean.parseBoolean(tokens[1].trim()));
        }


        blockSpreadsFire.clear();
        for (String s : FireConfig.blockSpreadsFire)
        {
            String[] tokens = s.split(",");
            ArrayList<IBlockState> fromStates = blockstatesMatching(tokens[0].trim());
            if (fromStates == null || fromStates.size() == 0)
            {
                System.err.println("Invalid entry for spreading fire like lava: " + s);
                continue;
            }


            filter = new FireDataFilter();
            good = true;
            for (int i = 1; i < tokens.length; i++)
            {
                token = tokens[i].trim();
                try
                {
                    filter.dimensions.add(Integer.parseInt(token));
                }
                catch (NumberFormatException e)
                {
                    biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(token));
                    if (biome != null) filter.biomes.add(biome);
                    else
                    {
                        System.err.println("Bad dimension number or biome name for spreading fire like lava: " + token);
                        good = false;
                        break;
                    }
                }
            }
            if (!good) continue;


            filter.blockStates.addAll(fromStates);
            blockSpreadsFire.add(filter);
        }
        if (blockSpreadsFire.size() > 0) BlockTick.addAction(SpreadFireLikeLava.ACTION);
        else BlockTick.removeAction(SpreadFireLikeLava.ACTION);
    }


    protected static HashSet<Block> blocksMatching(String blockID)
    {
        return blocksMatching(blockID, false);
    }

    protected static HashSet<Block> blocksMatching(String blockID, boolean allowAir)
    {
        HashSet<Block> blocks = new HashSet<>();

        ResourceLocation resourceLocation = new ResourceLocation(blockID);
        Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
        if (block != null && (allowAir || block != Blocks.AIR)) blocks.add(block);
        else if (blockID.contains("oredict:") || blockID.contains("ore:"))
        {
            for (ItemStack stack : OreDictionary.getOres(blockID.replace("oredict:", "").replace("ore:", "")))
            {
                block = Block.getBlockFromItem(stack.getItem());
                if (block != null && (allowAir || block != Blocks.AIR)) blocks.add(block); //block CAN be null here
            }
        }

        return blocks;
    }


    protected static ArrayList<IBlockState> blockstatesMatching(String blockID)
    {
        return blockstatesMatching(blockID, false);
    }

    protected static ArrayList<IBlockState> blockstatesMatching(String blockID, boolean allowAir)
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
        if (domain.equals("oredict") || domain.equals("ore")) blocks = blocksMatching(domain + ":" + name, allowAir);
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(domain, name));
            if (block == null || (!allowAir && block == Blocks.AIR)) return result;

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


    public static class FireDataFilter
    {
        public ArrayList<Integer> dimensions = new ArrayList<>();
        public ArrayList<Biome> biomes = new ArrayList<>();
        public ArrayList<IBlockState> blockStates = new ArrayList<>();


        @Override
        protected FireDataFilter clone()
        {
            FireDataFilter other = new FireDataFilter();
            other.dimensions.addAll(dimensions);
            other.biomes.addAll(biomes);
            other.blockStates.addAll(blockStates);
            return other;
        }


        public boolean matches(World world, BlockPos pos, IBlockState state)
        {
            if (!blockStates.contains(state)) return false;
            if (dimensions.size() != 0 && !dimensions.contains(world.provider.getDimension())) return false;
            if (biomes.size() != 0 && !biomes.contains(world.getBiome(pos))) return false;
            return true;
        }


        @Override
        public int hashCode()
        {
            return (dimensions.hashCode() << biomes.hashCode()) ^ blockStates.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof FireDataFilter)) return false;

            FireDataFilter other = (FireDataFilter) obj;
            return (other.dimensions.equals(dimensions) && other.biomes.equals(biomes) && other.blockStates.equals(blockStates));
        }
    }
}
