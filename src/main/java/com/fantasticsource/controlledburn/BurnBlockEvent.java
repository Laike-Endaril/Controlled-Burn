package com.fantasticsource.controlledburn;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class BurnBlockEvent extends BlockEvent
{
    public BurnBlockEvent(World world, BlockPos pos, IBlockState state)
    {
        super(world, pos, state);
    }
}
