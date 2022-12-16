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
    public Block log;

    public BlockRoots(Block log)
    {
        super(Material.WOOD);
        this.log = log;
        setBlockUnbreakable();
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(MODID + ":roots_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());
        setRegistryName("roots_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());
        Blocks.FIRE.setFireInfo(this, (int) (Blocks.FIRE.getEncouragement(log) * 0.2), (int) (Blocks.FIRE.getFlammability(log) * 0.2));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        pos = pos.up();
        if (worldIn.getBlockState(pos).getBlock() == BlocksAndItems.stumpBlocks.get(log)) worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
