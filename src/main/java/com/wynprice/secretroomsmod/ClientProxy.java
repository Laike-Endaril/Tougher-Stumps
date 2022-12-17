package com.wynprice.secretroomsmod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy
{
    @SideOnly(Side.CLIENT)
    public static EntityPlayer getPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfomationHolder.class, new BaseTERender<>());
    }
}
