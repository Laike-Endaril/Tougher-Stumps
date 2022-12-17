package com.wynprice.secretroomsmod;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SecretItems
{
    public static final Item SECRET_WOODEN_DOOR = new BaseItemDoor(SecretBlocks.SECRET_WOODEN_DOOR, "secret_wooden_door");


    public static void preInit()
    {
        ForgeRegistries.ITEMS.register(SECRET_WOODEN_DOOR);
    }


    public static void regRenders()
    {
        regRender();
    }

    public static void regRender()
    {
        SECRET_WOODEN_DOOR.setCreativeTab(SecretRooms5.TAB);
        ModelLoader.setCustomModelResourceLocation(SECRET_WOODEN_DOOR, 0, new ModelResourceLocation(SECRET_WOODEN_DOOR.getRegistryName(), "inventory"));
    }
}
