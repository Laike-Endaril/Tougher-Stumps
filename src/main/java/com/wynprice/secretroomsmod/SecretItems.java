package com.wynprice.secretroomsmod;

import com.wynprice.secretroomsmod.base.BaseItemDoor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

public class SecretItems
{
    public static final Item SECRET_WOODEN_DOOR = new BaseItemDoor(SecretBlocks.SECRET_WOODEN_DOOR, "secret_wooden_door");
    public static final Item SECRET_IRON_DOOR = new BaseItemDoor(SecretBlocks.SECRET_IRON_DOOR, "secret_iron_door");


    public static void preInit()
    {
        regItem(SECRET_WOODEN_DOOR);
        regItem(SECRET_IRON_DOOR);
    }


    public static void regRenders()
    {
        for (Item item : ALL_ITEMS) regRender(item);
    }

    public final static ArrayList<Item> ALL_ITEMS = new ArrayList<Item>();


    private static void regItem(Item item)
    {
        regItem(item, 64);
    }

    private static void regItem(Item item, int stackSize)
    {
        ALL_ITEMS.add(item);
        item.setMaxStackSize(stackSize);
        ForgeRegistries.ITEMS.register(item);
    }


    private static void regRender(Item item)
    {
        item.setCreativeTab(SecretRooms5.TAB);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
