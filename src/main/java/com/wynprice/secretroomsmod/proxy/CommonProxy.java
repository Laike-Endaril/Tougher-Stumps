package com.wynprice.secretroomsmod.proxy;

import com.wynprice.secretroomsmod.SecretBlocks;
import com.wynprice.secretroomsmod.SecretItems;
import com.wynprice.secretroomsmod.SecretRooms5;
import com.wynprice.secretroomsmod.handler.ParticleHandler;
import com.wynprice.secretroomsmod.handler.ServerRecievePacketHandler;
import com.wynprice.secretroomsmod.tileentity.TileEntityInfomationHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        SecretItems.preInit();

        SecretBlocks.preInit();
    }

    public void init(FMLInitializationEvent event)
    {
        Class[] tileEntityClasses = {
                TileEntityInfomationHolder.class,
        };
        for (Class clas : tileEntityClasses)
            GameRegistry.registerTileEntity(clas, SecretRooms5.MODID + clas.getSimpleName());

        Object[] handlers = {
                new ParticleHandler(),
                new ServerRecievePacketHandler()
        };
        for (Object o : handlers)
            MinecraftForge.EVENT_BUS.register(o);
    }

    //used for clients to get an instance of the player that wont crash the game
    public EntityPlayer getPlayer()
    {
        return null;
    }
}
