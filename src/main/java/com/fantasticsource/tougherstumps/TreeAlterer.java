package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class TreeAlterer implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        Chunk[] chunks = new Chunk[]{chunkProvider.provideChunk(chunkX, chunkZ), chunkProvider.provideChunk(chunkX + 1, chunkZ), chunkProvider.provideChunk(chunkX, chunkZ + 1), chunkProvider.provideChunk(chunkX + 1, chunkZ + 1)};
        for (Chunk chunk : chunks)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int y = 1; y < 256; y++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        IBlockState blockState = chunk.getBlockState(x, y, z);
                        Block log = blockState.getBlock(), stump = BlocksAndItems.stumpBlocks.get(log);
                        if (stump != null)
                        {
                            BlockPos belowPos = new BlockPos(x, y - 1, z);
                            IBlockState below = chunk.getBlockState(belowPos);
                            //TODO replace this...remember that some trees grow upside-down, possibly even sideways
                            if (below.getBlock().canSustainPlant(below, world, belowPos, EnumFacing.UP, (IPlantable) Blocks.SAPLING))
                            {
                                //Using chunk.setBlockState() prevents block updates into adjacent chunks
                                chunk.setBlockState(new BlockPos(x, y, z), stump.getDefaultState());
                                chunk.setBlockState(belowPos, BlocksAndItems.rootBlocks.get(log).getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}
