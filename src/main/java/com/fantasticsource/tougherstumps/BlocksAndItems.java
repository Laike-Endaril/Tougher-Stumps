package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;

public class BlocksAndItems
{
    public static HashMap<Block, BlockStump> stumpBlocks = new HashMap<>();
    public static HashMap<Block, BlockRoots> rootBlocks = new HashMap<>();


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = ForgeRegistries.BLOCKS;
        for (Block block : registry.getValues())
        {
            //TODO Natura logs (not subclassed off of BlockLog or even BlockRotatedPillar)
            if (block instanceof BlockLog)
            {
                BlockStump stump = new BlockStump(block);
                BlockRoots roots = new BlockRoots(block);
                stumpBlocks.put(block, stump);
                rootBlocks.put(block, roots);
                registry.register(stump);
                registry.register(roots);
            }
        }
    }
}
