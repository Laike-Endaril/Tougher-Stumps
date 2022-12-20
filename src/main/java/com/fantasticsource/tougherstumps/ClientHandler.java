package com.fantasticsource.tougherstumps;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class ClientHandler
{
    @SubscribeEvent
    public static void onModelsBaked(ModelBakeEvent event)
    {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        for (ModelResourceLocation mrl : registry.getKeys())
        {
            String name = mrl.getResourcePath(), variant = mrl.getVariant();
            if (name.contains("log") && variant.contains("axis=y"))
            {
                String[] variantProperties = Tools.fixedSplit(variant, ",");
                String newName = mrl.getResourceDomain() + "_" + name;
                for (String property : variantProperties)
                {
                    if (!property.equals("axis=y")) newName += "_" + property.replaceAll("=", "_");
                }


                //TODO create and register retextured models
                ModelResourceLocation newMRL = new ModelResourceLocation(MODID + ":" + newName);
                System.out.println(newMRL);
            }
        }
    }
}
