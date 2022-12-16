package com.fantasticsource.tougherstumps;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

@Mod(modid = TougherStumps.MODID, name = TougherStumps.NAME, version = TougherStumps.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.047c,)")
public class TougherStumps
{
    public static final String MODID = "tougherstumps";
    public static final String NAME = "Tougher Stumps";
    public static final String VERSION = "1.12.2.000b";

    protected static ArrayList<ChunkPos> done = new ArrayList<>();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TougherStumps.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        GameRegistry.registerWorldGenerator(new TreeAlterer(), Integer.MAX_VALUE);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
