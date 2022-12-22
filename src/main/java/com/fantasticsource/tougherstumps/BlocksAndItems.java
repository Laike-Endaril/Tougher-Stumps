package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;

public class BlocksAndItems
{
    public static HashMap<Block, BlockStump> stumpBlocks = new HashMap<>();
    public static HashMap<Block, BlockRoots> rootBlocks = new HashMap<>();


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockRegistry(RegistryEvent.Register<Block> event)
    {
        for (Block block : ForgeRegistries.BLOCKS.getValues())
        {
            if (block.isWood(null, null)) addStumpAndRoots(block);
        }
    }

    public static void addStumpAndRoots(Block log)
    {
        System.out.println(TextFormatting.YELLOW + log.getLocalizedName());
        BlockStump stump = new BlockStump(log);
        BlockRoots roots = new BlockRoots(log);
        stumpBlocks.put(log, stump);
        rootBlocks.put(log, roots);
        ForgeRegistries.BLOCKS.register(stump);
        ForgeRegistries.BLOCKS.register(roots);
    }
}
