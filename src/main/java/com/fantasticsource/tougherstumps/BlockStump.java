package com.fantasticsource.tougherstumps;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.fantasticsource.tougherstumps.TougherStumps.MODID;

public class BlockStump extends Block
{
    public Field
            blockStateField = ReflectionTool.getField(Block.class, "field_176227_L", "blockState"),
            blockResistanceField = ReflectionTool.getField(Block.class, "field_149781_w", "blockResistance");

    public Block log;

    public BlockStump(Block log)
    {
        super(log.getMaterial(log.getDefaultState()));
        this.log = log;

        if (getClass() == BlockStump.class)
        {
            setUnlocalizedName(MODID + ":stump_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());
            setRegistryName("stump_" + log.getRegistryName().getResourceDomain() + "_" + log.getRegistryName().getResourcePath());
        }

        //Hacks to have log field accessible during createBlockState()
        try
        {
            blockStateField.set(this, createBlockState());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        setDefaultState(blockState.getBaseState());

        Blocks.FIRE.setFireInfo(this, (int) (Blocks.FIRE.getEncouragement(log) * 0.5), (int) (Blocks.FIRE.getFlammability(log) * 0.5));
        IBlockState logDefault = log.getDefaultState();
        fullBlock = log.isFullBlock(logDefault);
        lightOpacity = log.getLightOpacity(logDefault);
        translucent = log.isTranslucent(logDefault);
        lightValue = log.getLightValue(logDefault);
        useNeighborBrightness = log.getUseNeighborBrightness(logDefault);
        setBlockUnbreakable();
        blockResistance = (float) ReflectionTool.get(blockResistanceField, log) * 2;
        enableStats = log.getEnableStats();
        blockSoundType = log.getSoundType();
        blockParticleGravity = log.blockParticleGravity;
        slipperiness = log.slipperiness;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        pos = pos.down();
        if (worldIn.getBlockState(pos).getBlock() == BlocksAndItems.rootBlocks.get(log)) worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state)
    {
        return false;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.BLOCK;
    }


    @Override
    protected BlockStateContainer createBlockState()
    {
        //Hacks to have log field accessible during createBlockState()
        if (log == null) return super.createBlockState();


        if (getClass() == BlockStump.class)
        {
            Map.Entry<IProperty<?>, Comparable<?>> foundPropertyEntry = findBlockPropertyEntry(log, new String[]{"axis"}, "y", "none", "null");
            if (foundPropertyEntry == null)
            {
                IBlockState logState = log.getDefaultState();
                System.out.println(TextFormatting.LIGHT_PURPLE + log.getLocalizedName());
                for (Map.Entry<IProperty<?>, Comparable<?>> entry : logState.getProperties().entrySet())
                {
                    System.out.println(TextFormatting.AQUA + "" + entry.getValue());
                }
            }
            else
            {
                for (IBlockState logState : log.getBlockState().getValidStates())
                {
                    if (!stateHasPropertyValue(logState, foundPropertyEntry)) continue;

                    System.out.println(TextFormatting.LIGHT_PURPLE + log.getLocalizedName());
                    for (Map.Entry<IProperty<?>, Comparable<?>> entry : logState.getProperties().entrySet())
                    {
                        if (entry.getKey().getName().equals(foundPropertyEntry.getKey().getName())) continue;

                        System.out.println(TextFormatting.AQUA + "" + entry.getValue());
                    }
                }
            }
        }

        //TODO probably need to mess with this
        return super.createBlockState();
    }

    public static Map.Entry<IProperty<?>, Comparable<?>> findBlockPropertyEntry(Block block, String[] possiblePropertyNames, String... possibleValues)
    {
        for (int i = 0; i < possiblePropertyNames.length; i++) possiblePropertyNames[i] = possiblePropertyNames[i].toLowerCase();
        List<IBlockState> blockStates = block.getBlockState().getValidStates();
        //Main loop is values, so that we can prioritize finding the first possible value over the 2nd, etc
        for (String possibleValue : possibleValues)
        {
            for (IBlockState state : blockStates)
            {
                for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                {
                    if (entry.getValue().toString().equalsIgnoreCase(possibleValue) && Tools.contains(possiblePropertyNames, entry.getKey().getName().toLowerCase())) return entry;
                }
            }
        }
        return null;
    }

    public static boolean stateHasPropertyValue(IBlockState state, Map.Entry<IProperty<?>, Comparable<?>> propertyEntry)
    {
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
        {
            if (entry.getKey().getName().equals(propertyEntry.getKey().getName()) && entry.getValue().toString().equals(propertyEntry.getValue().toString())) return true;
        }
        return false;
    }


    @Override
    public int getMetaFromState(IBlockState state)
    {
        //TODO probably need to mess with this
        return super.getMetaFromState(state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        //TODO probably need to mess with this
        return super.getStateFromMeta(meta);
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return log.getRenderType(log.getDefaultState());
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
    {
        return log.addLandingEffects(log.getDefaultState(), worldObj, blockPosition, iblockstate, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        return log.addRunningEffects(log.getDefaultState(), world, pos, entity);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        //Hacks to have log field accessible during createBlockState()
        if (log == null) return super.isOpaqueCube(state);

        return log.isOpaqueCube(log.getDefaultState());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return log.getItemDropped(log.getDefaultState(), rand, fortune);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return log.damageDropped(log.getDefaultState());
    }

    @Nullable
    @Override
    public BlockPos getBedSpawnPosition(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityPlayer player)
    {
        return log.getBedSpawnPosition(log.getDefaultState(), world, pos, player);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return log.getBlockLayer();
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        return log.addDestroyEffects(world, pos, manager);
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
    {
        return log.addHitEffects(log.getDefaultState(), worldObj, target, manager);
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return log.canBeConnectedTo(world, pos, facing);
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return log.canCollideCheck(log.getDefaultState(), hitIfLiquid);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
    {
        return log.canConnectRedstone(log.getDefaultState(), world, pos, side);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type)
    {
        return log.canCreatureSpawn(log.getDefaultState(), world, pos, type);
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return log.canDropFromExplosion(explosionIn);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
    {
        return log.canEntityDestroy(log.getDefaultState(), world, pos, entity);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return log.canHarvestBlock(world, pos, player);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return log.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return log.canPlaceBlockOnSide(worldIn, pos, side);
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return log.canPlaceTorchOnTop(log.getDefaultState(), world, pos);
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return log.canRenderInLayer(log.getDefaultState(), layer);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return log.canSilkHarvest(world, pos, log.getDefaultState(), player);
    }

    @Override
    public boolean canSpawnInBlock()
    {
        return log.canSpawnInBlock();
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return log.canSustainLeaves(log.getDefaultState(), world, pos);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        return log.canSustainPlant(log.getDefaultState(), world, pos, direction, plantable);
    }

    @Override
    public boolean doesSideBlockChestOpening(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return log.doesSideBlockChestOpening(log.getDefaultState(), world, pos, side);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return log.doesSideBlockRendering(log.getDefaultState(), world, pos, face);
    }

    @Override
    public boolean getEnableStats()
    {
        return log.getEnableStats();
    }

    @Nullable
    @Override
    public Boolean isAABBInsideLiquid(World world, BlockPos pos, AxisAlignedBB boundingBox)
    {
        return log.isAABBInsideLiquid(world, pos, boundingBox);
    }

    @Nullable
    @Override
    public Boolean isAABBInsideMaterial(World world, BlockPos pos, AxisAlignedBB boundingBox, Material materialIn)
    {
        return log.isAABBInsideMaterial(world, pos, boundingBox, materialIn);
    }

    @Override
    public boolean isFertile(World world, BlockPos pos)
    {
        return log.isFertile(world, pos);
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        return log.isFireSource(world, pos, side);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return log.isPassable(worldIn, pos);
    }

    @Override
    public boolean isStickyBlock(IBlockState state)
    {
        return log.isStickyBlock(log.getDefaultState());
    }

    @Override
    public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target)
    {
        return log.isReplaceableOreGen(log.getDefaultState(), world, pos, target);
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos)
    {
        return log.getEnchantPowerBonus(world, pos);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
    {
        return log.getExpDrop(log.getDefaultState(), world, pos, fortune);
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return super.getAiPathNodeType(state, world, pos);
    }

    @Override
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
    {
        return log.getFogColor(world, pos, log.getDefaultState(), entity, originalColor, partialTicks);
    }

    @Override
    public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
    {
        return log.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        log.beginLeavesDecay(log.getDefaultState(), world, pos);
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        log.fillWithRain(worldIn, pos);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        log.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        log.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        log.onEntityCollidedWithBlock(worldIn, pos, log.getDefaultState(), entityIn);
    }

    @Override
    public void onLanded(World worldIn, Entity entityIn)
    {
        super.onLanded(worldIn, entityIn);
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entityIn)
    {
        return log.canEntitySpawn(log.getDefaultState(), entityIn);
    }

    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return log.causesSuffocation(log.getDefaultState());
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return log.isBlockNormalCube(log.getDefaultState());
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return log.isFullBlock(log.getDefaultState());
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return log.isFullCube(log.getDefaultState());
    }

    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return log.isNormalCube(log.getDefaultState());
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return log.isSideSolid(log.getDefaultState(), world, pos, side);
    }

    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return log.isTopSolid(log.getDefaultState());
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return log.shouldSideBeRendered(log.getDefaultState(), blockAccess, pos, side);
    }
}
