package com.wynprice.secretroomsmod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SecretRooms5.MODID, name = SecretRooms5.MODNAME, version = SecretRooms5.VERSION, acceptedMinecraftVersions = "1.11.2")
public class SecretRooms5
{
    public static final String MODID = "secretroomsmod";
    public static final String MODNAME = "Secret Rooms 5";
    public static final String VERSION = "5.1.9";


    @Instance(MODID)
    public static SecretRooms5 instance;
    public static final CreativeTabs TAB = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(SecretItems.SECRET_WOODEN_DOOR);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SecretItems.preInit();
        SecretBlocks.preInit();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            SecretItems.regRenders();
            SecretBlocks.regRenders();
            ClientProxy.init(event);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerTileEntity(TileEntityFakeDoor.class, SecretRooms5.MODID + TileEntityFakeDoor.class.getSimpleName());
    }
}
