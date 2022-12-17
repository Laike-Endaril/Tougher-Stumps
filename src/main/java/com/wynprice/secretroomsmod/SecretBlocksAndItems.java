package com.wynprice.secretroomsmod;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static com.wynprice.secretroomsmod.SecretRooms5.MODID;

public class SecretBlocksAndItems
{
    public static final BlockSecretDoor BLOCK_SECRET_DOOR = new BlockSecretDoor("secret_wooden_door", Material.WOOD);
    public static final Item ITEM_SECRET_DOOR = new BaseItemDoor(BLOCK_SECRET_DOOR, "secret_wooden_door");

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(SecretBlocksAndItems.ITEM_SECRET_DOOR);
        }
    };

    public static void preInit()
    {
        ForgeRegistries.BLOCKS.register(BLOCK_SECRET_DOOR);
        ForgeRegistries.ITEMS.register(ITEM_SECRET_DOOR);
    }

    public static void regRenders()
    {
        ModelLoader.setCustomStateMapper(BLOCK_SECRET_DOOR, (new StateMap.Builder().ignore(BLOCK_SECRET_DOOR.getDefaultState().getProperties().keySet().toArray(new IProperty[0]))).build());

        ITEM_SECRET_DOOR.setCreativeTab(CREATIVE_TAB);
        ModelLoader.setCustomModelResourceLocation(ITEM_SECRET_DOOR, 0, new ModelResourceLocation(ITEM_SECRET_DOOR.getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFakeDoor.class, new BaseTERender<>());
    }
}
