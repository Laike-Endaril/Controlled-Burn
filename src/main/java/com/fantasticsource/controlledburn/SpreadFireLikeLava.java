package com.fantasticsource.controlledburn;

import com.fantasticsource.mctools.event.BlockTick;
import com.fantasticsource.tools.Tools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SpreadFireLikeLava
{
    public static final BlockPos.MutableBlockPos MUT_POS = new BlockPos.MutableBlockPos();
    public static final Predicate<BlockTick.BlockTickData> ACTION = event ->
    {
        World world = event.world;
        if (!world.getGameRules().getBoolean("doFireTick")) return false;

        int x = event.x, y = event.y, z = event.z;
        MUT_POS.setPos(x, y, z);
        IBlockState state = world.getBlockState(MUT_POS);
        if (!FireData.blockSpreadsFire.contains(state)) return false;


        int flareHeight = Tools.random(3);
        if (flareHeight > 0)
        {
            for (int i = 0; i < flareHeight; ++i)
            {
                MUT_POS.setPos(MUT_POS.getX() + Tools.random(3) - 1, MUT_POS.getY() + 1, MUT_POS.getZ() + Tools.random(3) - 1);
                if (MUT_POS.getY() >= world.getHeight() || !world.isBlockLoaded(MUT_POS)) return false;


                state = world.getBlockState(MUT_POS);

                if (state.getBlock().isAir(state, world, MUT_POS))
                {
                    if (isSurroundingBlockFlammable(world, MUT_POS))
                    {
                        world.setBlockState(MUT_POS, Blocks.FIRE.getDefaultState());
                        return true;
                    }
                }
                else if (state.getMaterial().blocksMovement()) return false;
            }
            return false;
        }
        else
        {
            boolean result = false;
            for (int i = 0; i < 3; ++i)
            {
                MUT_POS.setPos(x + Tools.random(3) - 1, y, z + Tools.random(3) - 1);
                if (MUT_POS.getY() > world.getHeight() || !world.isBlockLoaded(MUT_POS)) return false;


                BlockPos pos2 = MUT_POS.up();
                if (world.isAirBlock(pos2) && isSurroundingBlockFlammable(world, MUT_POS))
                {
                    result = true;
                    world.setBlockState(pos2, Blocks.FIRE.getDefaultState());
                }
            }
            return result;
        }
    };


    protected static boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos)
    {
        BlockPos pos2;
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            pos2 = pos.offset(enumfacing);
            if (worldIn.isBlockLoaded(pos2) && Blocks.FIRE.getEncouragement(worldIn.getBlockState(pos2).getBlock()) > 0) return true;
        }
        return false;
    }
}
