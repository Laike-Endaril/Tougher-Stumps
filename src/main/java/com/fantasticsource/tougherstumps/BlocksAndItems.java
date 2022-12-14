package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class BlocksAndItems
{
    @GameRegistry.ObjectHolder("tougherstumps:stump")
    public static BlockStump blockStump;

    @GameRegistry.ObjectHolder("tougherstumps:roots")
    public static BlockRoots blockRoots;


    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockStump());
        registry.register(new BlockRoots());
    }
}
