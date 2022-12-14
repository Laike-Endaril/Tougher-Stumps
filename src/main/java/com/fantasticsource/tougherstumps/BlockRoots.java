package com.fantasticsource.tougherstumps;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class BlockRoots extends Block
{
    public BlockRoots()
    {
        super(Material.WOOD);
        setBlockUnbreakable();
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(MODID + ":roots");
        setRegistryName("roots");
        Blocks.FIRE.setFireInfo(this, 1, 1);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        pos = pos.up();
        if (worldIn.getBlockState(pos).getBlock() == BlocksAndItems.blockStump) worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
