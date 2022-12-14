package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class BlockStump extends Block
{
    public BlockStump()
    {
        super(Material.WOOD);
        setBlockUnbreakable();
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(MODID + ":stump");
        setRegistryName("stump");
        Blocks.FIRE.setFireInfo(this, 3, 2);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        pos = pos.down();
        if (worldIn.getBlockState(pos).getBlock() == BlocksAndItems.blockRoots) worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
