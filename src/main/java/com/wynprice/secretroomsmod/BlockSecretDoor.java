package com.wynprice.secretroomsmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSecretDoor extends BlockDoor implements ITileEntityProvider
{
    public BlockSecretDoor(String name, Material materialIn)
    {
        super(materialIn);
        setRegistryName(name);
        setUnlocalizedName(name);
        setHardness(0.5f);
        translucent = true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityFakeDoor();
    }


    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type)
    {
        return false;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(SecretBlocksAndItems.ITEM_SECRET_DOOR);
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune)
    {
        return SecretBlocksAndItems.ITEM_SECRET_DOOR;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState state = world.getTileEntity(pos) instanceof TileEntityFakeDoor ? ((TileEntityFakeDoor) world.getTileEntity(pos)).getTextureState() : null;
        return state.getBlock().canBeConnectedTo(world, pos, facing);
    }

    public IBlockState overrideThisState(World world, BlockPos pos, IBlockState defaultState)
    {
        return defaultState.getValue(HALF) != EnumDoorHalf.UPPER || world.getBlockState(pos.down()).getBlock() != this ? defaultState : defaultState.withProperty(OPEN, world.getBlockState(pos.down()).getValue(OPEN)).withProperty(FACING, world.getBlockState(pos.down()).getValue(FACING)).withProperty(POWERED, world.getBlockState(pos.down()).getValue(POWERED));
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        return world.getTileEntity(pos) instanceof TileEntityFakeDoor && TileEntityFakeDoor.getTextureState(world, pos) != null ? TileEntityFakeDoor.getTextureState(world, pos).getBlock().getSoundType() : SoundType.STONE;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER)
        {
            BlockPos blockpos = pos.down();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
            else if (blockIn != this)
            {
                iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
            }
        }
        else
        {
            boolean flag1 = false;
            BlockPos blockpos1 = pos.up();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP))
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (iblockstate1.getBlock() == this)
                {
                    worldIn.setBlockToAir(blockpos1);
                }
            }

            if (flag1)
            {
                if (!worldIn.isRemote)
                {
                    dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else
            {
                boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos1);

                if (blockIn != this && (flag || blockIn.getDefaultState().canProvidePower()) && flag != iblockstate1.getValue(POWERED))
                {
                    worldIn.setBlockState(blockpos1, iblockstate1.withProperty(POWERED, flag), 2);

                    if (flag != state.getValue(OPEN))
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, flag), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager)
    {
        BlockPos pos = target.getBlockPos();
        IBlockState textureState = getTextureState(world, pos);
        AxisAlignedBB axisalignedbb = textureState.getBoundingBox(world, pos);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        double xx = x + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
        double yy = y + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
        double zz = z + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

        if (target.sideHit == EnumFacing.DOWN) yy = y + axisalignedbb.minY - 0.10000000149011612D;
        if (target.sideHit == EnumFacing.UP) yy = y + axisalignedbb.maxY + 0.10000000149011612D;
        if (target.sideHit == EnumFacing.NORTH) zz = z + axisalignedbb.minZ - 0.10000000149011612D;
        if (target.sideHit == EnumFacing.SOUTH) zz = z + axisalignedbb.maxZ + 0.10000000149011612D;
        if (target.sideHit == EnumFacing.WEST) xx = x + axisalignedbb.minX - 0.10000000149011612D;
        if (target.sideHit == EnumFacing.EAST) xx = x + axisalignedbb.maxX + 0.10000000149011612D;

        manager.addEffect(((net.minecraft.client.particle.ParticleDigging) new net.minecraft.client.particle.ParticleDigging.Factory().createParticle(0, world, xx, yy, zz, 0, 0, 0, Block.getStateId(textureState))).setBlockPos(pos).multiplyVelocity(0.2f).multipleParticleScaleBy(0.6f));

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        IBlockState state = getTextureState(world, pos);
        for (int x = 0; x < 4; ++x)
        {
            for (int y = 0; y < 4; ++y)
            {
                for (int z = 0; z < 4; ++z)
                {
                    double xx = (x + 0.5) / 4;
                    double yy = (y + 0.5) / 4;
                    double zz = (z + 0.5) / 4;
                    manager.addEffect(((net.minecraft.client.particle.ParticleDigging) new net.minecraft.client.particle.ParticleDigging.Factory().createParticle(0, world, pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz, xx - 0.5, yy - 0.5, zz - 0.5, Block.getStateId(state))).setBlockPos(pos));
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX + (world.rand.nextFloat() - 0.5) * entity.width, entity.getEntityBoundingBox().minY + 0.1, entity.posZ + (world.rand.nextFloat() - 0.5) * entity.width, -entity.motionX * 4, 1.5, -entity.motionZ * 4, getStateId(getTextureState(world, pos)));
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
    {
        world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numberOfParticles, 0, 0, 0, 0.15000000596046448, Block.getStateId(getTextureState(world, pos)));
        return true;
    }

    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state)
    {
        return 1.0F;
    }

    public IBlockState getTextureState(World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityFakeDoor) return ((TileEntityFakeDoor) tileEntity).getTextureState();
        return world.getBlockState(pos);
    }
}