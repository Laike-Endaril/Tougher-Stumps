package com.wynprice.secretroomsmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.wynprice.secretroomsmod.SecretItems.SECRET_WOODEN_DOOR;

public class ClientProxy
{
    @SideOnly(Side.CLIENT)
    public static EntityPlayer getPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    public static void init(FMLPreInitializationEvent event)
    {
        SECRET_WOODEN_DOOR.setCreativeTab(SecretRooms5.TAB);
        ModelLoader.setCustomModelResourceLocation(SECRET_WOODEN_DOOR, 0, new ModelResourceLocation(SECRET_WOODEN_DOOR.getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFakeDoor.class, new BaseTERender<>());
    }
}
