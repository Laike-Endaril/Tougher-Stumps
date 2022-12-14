package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Mod(modid = TougherStumps.MODID, name = TougherStumps.NAME, version = TougherStumps.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.047c,)")
public class TougherStumps
{
    public static final String MODID = "tougherstumps";
    public static final String NAME = "Tougher Stumps";
    public static final String VERSION = "1.12.2.000b";

    protected static ArrayList<ChunkPos> done = new ArrayList<>();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TougherStumps.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void test(DecorateBiomeEvent.Post event)
    {
        ChunkPos pos = event.getChunkPos();
        Chunk chunk = event.getWorld().getChunkFromChunkCoords(pos.x, pos.z);
        for (int x = 0; x < 16; x++)
        {
            for (int y = 1; y < 256; y++)
            {
                for (int z = 0; z < 16; z++)
                {
                    IBlockState blockState = chunk.getBlockState(x, y, z);
                    Block block = blockState.getBlock();
                    if (block instanceof BlockLog)
                    {
                        IBlockState below = chunk.getBlockState(x, y - 1, z);
                        BlockPos belowPos = new BlockPos(x, y - 1, z);
                        if (below.getBlock().canSustainPlant(below, event.getWorld(), belowPos, EnumFacing.UP, (IPlantable) Blocks.SAPLING))
                        {
                            chunk.setBlockState(new BlockPos(x, y, z), BlocksAndItems.blockStump.getDefaultState());
                            chunk.setBlockState(belowPos, BlocksAndItems.blockRoots.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}
