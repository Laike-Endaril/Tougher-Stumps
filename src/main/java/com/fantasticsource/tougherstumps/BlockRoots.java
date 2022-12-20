package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class BlockRoots extends BlockStump
{
    public BlockRoots(Block log)
    {
        super(log);

        setUnlocalizedName(MODID + ":roots_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());
        setRegistryName("roots_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());

        blockResistance *= 2;
        Blocks.FIRE.setFireInfo(this, (int) (Blocks.FIRE.getEncouragement(log) * 0.25), (int) (Blocks.FIRE.getFlammability(log) * 0.25));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        pos = pos.up();
        if (worldIn.getBlockState(pos).getBlock() == BlocksAndItems.stumpBlocks.get(log)) worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
