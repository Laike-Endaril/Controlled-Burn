package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.fantasticsource.controlledburn.FireConfig.*;

public class BlockFireEdit extends BlockFire
{
    private static boolean tryBurnBlockSpecial(World world, BlockPos pos)
    {
        IBlockState blockTo = FireData.blockTransformationMap.get(world.getBlockState(pos));
        if (blockTo != null)
        {
            world.setBlockState(pos, blockTo);
            return true;
        }
        else return false;
    }

    public int tickRate(World worldIn)
    {
        return globalMultipliers.tickDelay;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        //Hard stops/fire disablers
        if (!worldIn.getGameRules().getBoolean("doFireTick")) return;
        if (!worldIn.isAreaLoaded(pos, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire

        //Check for illegal placement of fire block
        if (!canPlaceBlockAt(worldIn, pos))
        {
            worldIn.setBlockToAir(pos);
            return;
        }

        //Check for rain extinguishing
        boolean fireSourceBelow = worldIn.getBlockState(pos.down()).getBlock().isFireSource(worldIn, pos.down(), EnumFacing.UP);
        int age = state.getValue(AGE);
        if (!fireSourceBelow && !specialToggles.ignoreRain && worldIn.isRaining() && canDie(worldIn, pos) && rand.nextFloat() < 0.2F + (float) age * 0.03F)
        {
            worldIn.setBlockToAir(pos); //Extinguished by rain
            return;
        }

        //33% chance to add 1 to fire age
        if (age < ControlledBurn.maxFireAge() && rand.nextInt(3) == 2)
        {
            state = state.withProperty(AGE, (++age));
            worldIn.setBlockState(pos, state, 4); //Flag 4 prevents block from re-rendering if client side
        }

        //Natural extinguishing over time
        if (!fireSourceBelow)
        {
            if (!canNeighborCatchFire(worldIn, pos))
            {
                if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) || age > 3)
                {
                    worldIn.setBlockToAir(pos); //Extinguish if no fire source below, nothing adjacent can catch fire, and (there is no solid block below OR age > 3)
                }
                else
                {
                    worldIn.scheduleUpdate(pos, this, tickRate(worldIn) + rand.nextInt(FireConfig.globalMultipliers.tickDelayRandomization + 1));
                }

                return;
            }
            //No fire source below, but a neighbor can catch fire

            if (!canCatchFire(worldIn, pos.down()) && age >= ControlledBurn.maxFireAge() && rand.nextInt(4) == 0)
            {
                worldIn.setBlockToAir(pos); //25% chance to extinguish if age is maxed and block below isn't flammable
                return;
            }
        }

        boolean feelsHumid = worldIn.isBlockinHighHumidity(pos) && !specialToggles.ignoreHumidBiomes;
        int humidModifier = (feelsHumid ? -50 : 0);

        //Try to destroy (burn) adjacent blocks, possibly replacing them with more fire
        if (globalMultipliers.burnSpeedMultiplier != 0)
        {
            tryBurnAdjacent(worldIn, pos.down(), 250 + humidModifier, rand, age, EnumFacing.UP);
            tryBurnAdjacent(worldIn, pos.up(), 250 + humidModifier, rand, age, EnumFacing.DOWN);
            tryBurnAdjacent(worldIn, pos.south(), 300 + humidModifier, rand, age, EnumFacing.NORTH);
            tryBurnAdjacent(worldIn, pos.north(), 300 + humidModifier, rand, age, EnumFacing.SOUTH);
            tryBurnAdjacent(worldIn, pos.west(), 300 + humidModifier, rand, age, EnumFacing.EAST);
            tryBurnAdjacent(worldIn, pos.east(), 300 + humidModifier, rand, age, EnumFacing.WEST);
        }

        //Try to spread naturally
        if (globalMultipliers.spreadSpeedMultiplier > 0)
        {
            for (int trySpreadX = -spreadRanges.reachHorizontal; trySpreadX <= spreadRanges.reachHorizontal; ++trySpreadX)
            {
                for (int trySpreadZ = -spreadRanges.reachHorizontal; trySpreadZ <= spreadRanges.reachHorizontal; ++trySpreadZ)
                {
                    for (int trySpreadY = -spreadRanges.reachBelow; trySpreadY <= spreadRanges.reachAbove; ++trySpreadY)
                    {
                        if (trySpreadX != 0 || trySpreadY != 0 || trySpreadZ != 0)
                        {
                            BlockPos spreadPos = pos.add(trySpreadX, trySpreadY, trySpreadZ);
                            int adjacentEncouragement = getNeighborEncouragement(worldIn, spreadPos);

                            if (adjacentEncouragement > 0)
                            {
                                int spreadStrength = (adjacentEncouragement + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (age + 30);

                                if (feelsHumid)
                                {
                                    spreadStrength /= 2;
                                }

                                int spreadDifficulty = (trySpreadY > 1 ? 100 + (trySpreadY - 1) * 100 : 100);
                                if (spreadStrength > 0 && rand.nextInt(spreadDifficulty) <= spreadStrength && (specialToggles.ignoreRain || !worldIn.isRaining() || !canDie(worldIn, spreadPos)))
                                {
                                    if (spreadStrengths.naturalSpreadStrength == -1)
                                    {
                                        int childAge = age + rand.nextInt(5) / 4;

                                        if (childAge > ControlledBurn.maxFireAge())
                                        {
                                            childAge = ControlledBurn.maxFireAge();
                                        }

                                        worldIn.setBlockState(spreadPos, state.withProperty(AGE, childAge), 3);
                                    }
                                    else if (spreadStrengths.naturalSpreadStrength != 0)
                                    {
                                        int childAge = age < 0 ? 0 : age > ControlledBurn.maxFireAge() ? ControlledBurn.maxFireAge() : age;
                                        childAge = ControlledBurn.maxFireAge() - (ControlledBurn.maxFireAge() - childAge) * spreadStrengths.naturalSpreadStrength / 100;

                                        if (childAge < ControlledBurn.maxFireAge()) worldIn.setBlockState(spreadPos, state.withProperty(AGE, childAge), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        worldIn.scheduleUpdate(pos, this, tickRate(worldIn) + rand.nextInt(FireConfig.globalMultipliers.tickDelayRandomization + 1));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return (worldIn.getBlockState(pos.down()).isTopSolid() || this.canNeighborCatchFire(worldIn, pos))
                && (!specialToggles.noLightningFire || !callerNameContains("Lightning"));
    }

    public boolean callerNameContains(String subString)
    {
        subString = subString.toLowerCase();

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack)
        {
            if (element.getClassName().toLowerCase().contains(subString)) return true;
        }
        return false;
    }

    @Override
    public int getFlammability(Block blockIn)
    {
        int baseFlammability = super.getFlammability(blockIn);

        if (globalMultipliers.burnSpeedMultiplier == 0)
        {
            if (baseFlammability > 0) return 1;
            if (baseFlammability < 0) return -1; //Not sure why this would be a thing, but here's your compatibility
            return 0;
        }

        return (int) (baseFlammability * globalMultipliers.burnSpeedMultiplier);
    }

    @Override
    public int getEncouragement(Block blockIn)
    {
        return (int) (super.getEncouragement(blockIn) * globalMultipliers.spreadSpeedMultiplier);
    }

    private void tryBurnAdjacent(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (random.nextInt(chance) < iblockstate.getBlock().getFlammability(worldIn, pos, face))
        {
            //Destroy (burn) this adjacent block
            int replaceBlockWithFireChance;
            if (ControlledBurn.fireAgeRange() == 0) replaceBlockWithFireChance = burnSpreadChances.minBurnSpreadChance + ControlledBurn.replaceBlockWithFireChanceRange / 2;
            else replaceBlockWithFireChance = (int) (burnSpreadChances.minBurnSpreadChance + ControlledBurn.replaceBlockWithFireChanceRange * (float) age / ControlledBurn.fireAgeRange());

            if (!tryBurnBlockSpecial(worldIn, pos))
            {
                if (random.nextInt(100) < replaceBlockWithFireChance && (!worldIn.isRainingAt(pos) || specialToggles.ignoreRain))
                {
                    //Replace destroyed (burnt) block with new fire block

                    if (spreadStrengths.burnSpreadStrength == -1)
                    {
                        if (age < ControlledBurn.maxFireAge()) //If source fire block's age is less than max...
                        {
                            //...75% chance to set child's age to source's age, and 25% to set it 1 higher
                            worldIn.setBlockState(pos, getDefaultState().withProperty(AGE, (random.nextInt(5) == 4 ? age + 1 : age)), 3);
                        }
                        else
                        {
                            //Otherwise, set child's age to source's age (which is also max age in this case)
                            worldIn.setBlockState(pos, getDefaultState().withProperty(AGE, age), 3);
                        }
                    }
                    else if (spreadStrengths.burnSpreadStrength != 0)
                    {
                        int childAge = ControlledBurn.maxFireAge() - (ControlledBurn.maxFireAge() - age) * spreadStrengths.burnSpreadStrength / 100;

                        if (childAge < ControlledBurn.maxFireAge()) worldIn.setBlockState(pos, getDefaultState().withProperty(AGE, childAge), 3);
                        else
                        {
                            if (spreadStrengths.burnSpreadStrength != 100) worldIn.setBlockToAir(pos);
                            else worldIn.setBlockState(pos, getDefaultState().withProperty(AGE, age), 3);
                        }
                    }
                    else worldIn.setBlockToAir(pos);
                }
                else
                {
                    //Replace destroyed (burnt) block with air block
                    worldIn.setBlockToAir(pos);
                }
            }

            if (iblockstate.getBlock() == Blocks.TNT)
            {
                Blocks.TNT.onBlockDestroyedByPlayer(worldIn, pos, iblockstate.withProperty(BlockTNT.EXPLODE, true));
            }
        }
    }

    private boolean canNeighborCatchFire(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (canCatchFire(worldIn, pos.offset(enumfacing), enumfacing.getOpposite())) return true;
        }
        return false;
    }

    private int getNeighborEncouragement(World worldIn, BlockPos pos)
    {
        if (worldIn.isAirBlock(pos))
        {
            int i = 0;
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
            }
            return i;
        }
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        soundAndParticles(worldIn, pos, rand);
    }

    @SideOnly(Side.CLIENT)
    public void soundAndParticles(World worldIn, BlockPos pos, Random rand)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        //1 in 24 chance to play fire sound
        if (rand.nextInt(24) == 0)
        {
            worldIn.playSound(x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1 + rand.nextFloat(), (float) (0.3 + 0.7 * rand.nextFloat()), false);
        }


        //Smoke particle spawning stuff below this line


        //If block below is solid OR block below is flammable, spawn 3 smoke particles randomly within top half of fire block
        //This means that the "normal" fire graphic is shown (the one that you see if you light the top of netherrack on fire)
        if (worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) || canCatchFire(worldIn, pos.down()))
        {
            for (int i = 0; i < 3; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + rand.nextDouble(), y + 0.5 + 0.5 * rand.nextDouble(), z + rand.nextDouble());
            }
            return;
        }


        //Block below is not solid AND block below is not flammable; check other sides for flammable blocks

        if (canCatchFire(worldIn, pos.west()))
        {
            for (int i = 0; i < 2; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + 0.1 * rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble());
            }
        }

        if (canCatchFire(worldIn, pos.east()))
        {
            for (int i = 0; i < 2; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + 1 - 0.1 * rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble());
            }
        }

        if (canCatchFire(worldIn, pos.north()))
        {
            for (int i = 0; i < 2; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + rand.nextDouble(), y + rand.nextDouble(), z + 0.1 * rand.nextDouble());
            }
        }

        if (canCatchFire(worldIn, pos.south()))
        {
            for (int i = 0; i < 2; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + rand.nextDouble(), y + rand.nextDouble(), z + 1 - 0.1 * rand.nextDouble());
            }
        }

        //This actually means the BOTTOM of the block above the fire block is on fire (make sense?)
        if (canCatchFire(worldIn, pos.up()))
        {
            for (int i = 0; i < 2; ++i)
            {
                spawnSmokeParticle(worldIn, rand, x + rand.nextDouble(), y + 1 - 0.1 * rand.nextDouble(), z + rand.nextDouble());
            }
        }
    }

    public void spawnSmokeParticle(World world, Random rand, double x, double y, double z)
    {
        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
    }
}
