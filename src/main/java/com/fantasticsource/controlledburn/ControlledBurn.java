package com.fantasticsource.controlledburn;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static com.fantasticsource.controlledburn.FireConfig.specialToggles;

@Mod(modid = ControlledBurn.MODID, name = ControlledBurn.NAME, version = ControlledBurn.VERSION, acceptableRemoteVersions = "*")
public class ControlledBurn
{
    public static final String MODID = "controlledburn";
    public static final String NAME = "Controlled Burn";
    public static final String VERSION = "1.12.2.019";
    public static int replaceBlockWithFireChanceRange;
    public static BlockFire oldFire;


    public ControlledBurn()
    {
        MinecraftForge.EVENT_BUS.register(ControlledBurn.class);
        oldFire = Blocks.FIRE;
    }

    public static int minFireAge()
    {
        Object[] ageArray = BlockFireEdit.AGE.getAllowedValues().toArray();
        return (int) ageArray[0];
    }

    public static int maxFireAge()
    {
        Object[] ageArray = BlockFireEdit.AGE.getAllowedValues().toArray();
        return (int) ageArray[ageArray.length - 1];
    }

    public static int fireAgeRange()
    {
        return maxFireAge() - minFireAge();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void calcConfigs(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) FireData.update();
    }

    @SubscribeEvent
    public static void fluidPlacingBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        if (specialToggles.noLavaFire && event.getNewState().getBlock().getClass() == BlockFireEdit.class)
        {
            event.setNewState(event.getOriginalState());
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BlockFireEdit newFire = (BlockFireEdit) (new BlockFireEdit()).setHardness(0.0F).setLightLevel(1.0F).setUnlocalizedName("fire").setRegistryName(Objects.requireNonNull(Blocks.FIRE.getRegistryName()));

        event.getRegistry().register(newFire);

        Field f;
        try
        {
            f = ReflectionHelper.findField(Blocks.class, "field_150480_ab");
        }
        catch (ReflectionHelper.UnableToFindFieldException e)
        {
            f = ReflectionHelper.findField(Blocks.class, "FIRE");
        }

        try
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

            f.set(null, newFire);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Copy fire-related stats for vanilla blocks
        for (Block b : ForgeRegistries.BLOCKS.getValues())
        {
            if (oldFire.getEncouragement(b) != 0 && b != Blocks.AIR)
            {
                Blocks.FIRE.setFireInfo(b, oldFire.getEncouragement(b), oldFire.getFlammability(b));
            }
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        FireData.update();
    }
}
