package com.fantasticsource.controlledburn;

import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.fantasticsource.controlledburn.FireConfig.*;

@Mod(modid = ControlledBurn.MODID, name = ControlledBurn.NAME, version = ControlledBurn.VERSION, acceptableRemoteVersions = "*")
public class ControlledBurn {
    public static final String MODID = "controlledburn";
    public static final String NAME = "Controlled Burn";
    public static final String VERSION = "1.12.2.007";

    private static Logger logger;

    public static int replaceBlockWithFireChanceRange = maxReplaceBlockWithFireChance - minReplaceBlockWithFireChance;
    public static BlockFire oldFire;


    public ControlledBurn() {
        MinecraftForge.EVENT_BUS.register(ControlledBurn.class);
        oldFire = Blocks.FIRE;
    }

    public static int minFireAge() {
        Object[] ageArray = BlockFireEdit.AGE.getAllowedValues().toArray();
        return (int) ageArray[0];
    }

    public static int maxFireAge() {
        Object[] ageArray = BlockFireEdit.AGE.getAllowedValues().toArray();
        return (int) ageArray[ageArray.length - 1];
    }

    public static int fireAgeRange()
    {
        return maxFireAge() - minFireAge();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Map<String, Pair<Integer, Integer>> data = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : blockFlammabilities.entrySet())
        {
            data.put(entry.getKey(), new Pair<>(entry.getValue(), null));
        }

        for (Map.Entry<String, Integer> entry : blockEncouragements.entrySet())
        {
            if (!data.containsKey(entry.getKey())) data.put(entry.getKey(), new Pair<>(null, entry.getValue()));
            else data.put(entry.getKey(), new Pair<>(data.get(entry.getKey()).getKey(), entry.getValue()));
        }



        class FireData
        {
            Integer f, e;
        }

        ResourceLocation resourceLocation;
        Block b;
        FireData fireData;
        for (Map.Entry<String, Pair<Integer, Integer>> entry : data.entrySet())
        {
            resourceLocation = new ResourceLocation(entry.getKey());

            if (ForgeRegistries.BLOCKS.containsKey(resourceLocation))
            {
                b = ForgeRegistries.BLOCKS.getValue(resourceLocation);

                if (b != null)
                {
                    fireData = new FireData();

                    fireData.f = entry.getValue().getKey();
                    fireData.e = entry.getValue().getValue();

                    if (fireData.f == null) fireData.f = Blocks.FIRE.getFlammability(b);
                    if (fireData.e == null) fireData.e = Blocks.FIRE.getEncouragement(b);

                    Blocks.FIRE.setFireInfo(b, fireData.e, fireData.f);
                }
                else
                {
                    System.err.println("Block was found, but was null (this should never happen): " + entry.getKey());
                }
            }
            else
            {
                System.err.println("Could not find block: " + entry.getKey());
            }
        }

//        for (Map.Entry<ResourceLocation, Block> entry : ForgeRegistries.BLOCKS.getEntries())
//        {
//            System.out.println(entry.getKey().toString() + " === " + entry.getValue().getUnlocalizedName());
//        }
    }

    @SubscribeEvent
    public static void fluidPlacingBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        if (noLavaFire && event.getNewState().getBlock().getClass() == BlockFireEdit.class)
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
        try {
            f = ReflectionHelper.findField(Blocks.class, "field_150480_ab");
        }
        catch (ReflectionHelper.UnableToFindFieldException e)
        {
            f = ReflectionHelper.findField(Blocks.class, "FIRE");
        }

        try {
            f.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

            f.set(null, newFire);
        } catch (Exception e) {
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
}
