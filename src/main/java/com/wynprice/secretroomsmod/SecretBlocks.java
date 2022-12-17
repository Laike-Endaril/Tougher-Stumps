package com.wynprice.secretroomsmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class SecretBlocks
{

    public static final BlockFakeDoor SECRET_WOODEN_DOOR = new BlockFakeDoor("secret_wooden_door", Material.WOOD);


    public static void preInit()
    {
        regSingleBlockIgnoreAll(SECRET_WOODEN_DOOR);
    }

    private final static ArrayList<Block> BLOCKS_WITH_CUSTOM_STATE_MAP = new ArrayList<>();
    private final static ArrayList<IProperty<?>[]> PROPERTIES_TO_IGNORE_CUSTOM_STATE_MAP = new ArrayList<>();

    public static void regRenders()
    {
        for (int i = 0; i < BLOCKS_WITH_CUSTOM_STATE_MAP.size(); i++) createStateMappers(BLOCKS_WITH_CUSTOM_STATE_MAP.get(i), PROPERTIES_TO_IGNORE_CUSTOM_STATE_MAP.get(i));
    }

    private static void regSingleBlockIgnoreAll(Block block)
    {
        regSingleBlock(block, block.getDefaultState().getProperties().asMultimap().asMap().keySet().toArray(new IProperty[0]));
    }

    @SideOnly(Side.CLIENT)
    private static void createStateMappers(Block block, IProperty<?>[] toIgnore)
    {
        ModelLoader.setCustomStateMapper(block, (new StateMap.Builder().ignore(toIgnore)).build());
    }

    private static void regSingleBlock(Block block, IProperty<?>... toIgnore)
    {
        BLOCKS_WITH_CUSTOM_STATE_MAP.add(block);
        PROPERTIES_TO_IGNORE_CUSTOM_STATE_MAP.add(toIgnore);
        register(block);
    }

    private static void register(Block block)
    {
        ForgeRegistries.BLOCKS.register(block);
    }
}
