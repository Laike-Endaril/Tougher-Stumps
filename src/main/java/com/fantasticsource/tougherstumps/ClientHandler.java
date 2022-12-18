package com.fantasticsource.tougherstumps;

import com.fantasticsource.mctools.ModelReplacer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class ClientHandler
{
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        for (BlockStump stump : BlocksAndItems.stumpBlocks.values()) ModelReplacer.replaceModel(stump, MODID + ":stump");
        for (BlockRoots roots : BlocksAndItems.rootBlocks.values()) ModelReplacer.replaceModel(roots, MODID + ":roots");
    }
}
