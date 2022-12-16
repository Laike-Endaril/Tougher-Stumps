package com.wynprice.secretroomsmod.proxy;

import com.wynprice.secretroomsmod.SecretBlocks;
import com.wynprice.secretroomsmod.SecretItems;
import com.wynprice.secretroomsmod.base.BaseTERender;
import com.wynprice.secretroomsmod.handler.HandlerUpdateChecker;
import com.wynprice.secretroomsmod.handler.SecretKeyBindings;
import com.wynprice.secretroomsmod.tileentity.TileEntityInfomationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        SecretItems.regRenders();

        SecretBlocks.regRenders();

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfomationHolder.class, new BaseTERender<>());

        Object[] handlers = {
                new SecretKeyBindings(),
                new HandlerUpdateChecker()
        };
        for (Object o : handlers) MinecraftForge.EVENT_BUS.register(o);
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return Minecraft.getMinecraft().player;
    }
}
