package com.wynprice.secretroomsmod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class TileEntityInfomationHolder extends TileEntity implements ITickable
{
    protected IBlockState mirrorState;

    private boolean locked;

    public static HashMap<Integer, HashMap<BlockPos, IBlockState>> RENDER_MAP = new HashMap<>();

    public static HashMap<BlockPos, IBlockState> getMap(World world)
    {
        if (!RENDER_MAP.containsKey(world.provider.getDimension()))
            RENDER_MAP.put(world.provider.getDimension(), new HashMap<>());
        return RENDER_MAP.get(world.provider.getDimension());
    }

    public static IBlockState getMirrorState(World world, BlockPos pos)
    {
        return getMap(world).get(pos) == null && world.getTileEntity(pos) instanceof TileEntityInfomationHolder ? ((TileEntityInfomationHolder) world.getTileEntity(pos)).getMirrorState() : getMap(world).get(pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        locked = getTileData().getBoolean("locked");
        Block testBlock = Block.REGISTRY.getObject(new ResourceLocation(getTileData().getString("MirrorBlock")));
        if (testBlock != Blocks.AIR)
            mirrorState = testBlock.getStateFromMeta(getTileData().getInteger("MirrorMeta"));
        if (mirrorState != null && mirrorState.getBlock() instanceof BaseBlockDoor)
            mirrorState = null;
        if (!BaseBlockDoor.ALL_SECRET_TILE_ENTITIES.contains(this))
            BaseBlockDoor.ALL_SECRET_TILE_ENTITIES.add(this);
    }

    @Override
    public void update()
    {
        if (mirrorState != null)
            ParticleHandler.BLOCKBRAKERENDERMAP.put(pos, mirrorState.getBlock().getStateFromMeta(mirrorState.getBlock().getMetaFromState(mirrorState)));
    }

    public IBlockState getMirrorState()
    {
        if (mirrorState == null && ParticleHandler.BLOCKBRAKERENDERMAP.containsKey(pos))
            mirrorState = ParticleHandler.BLOCKBRAKERENDERMAP.get(pos);
        if (mirrorState == null && RENDER_MAP.containsKey(pos))
            mirrorState = TileEntityInfomationHolder.getMap(world).get(pos);
        return mirrorState;
    }

    public void setMirrorState(IBlockState mirrorState)
    {
        if (!locked)
        {
            if (mirrorState.getBlock() instanceof BaseBlockDoor) mirrorState = Blocks.STONE.getDefaultState();
            TileEntityInfomationHolder.getMap(world).put(this.pos, mirrorState);
            this.mirrorState = mirrorState.getBlock().getStateFromMeta(mirrorState.getBlock().getMetaFromState(mirrorState));
        }
        locked = true;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        int metadata = getBlockMetadata();
        return new SPacketUpdateTileEntity(this.pos, metadata, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if (mirrorState != null)
        {
            getTileData().setString("MirrorBlock", mirrorState.getBlock().getRegistryName().toString());
            getTileData().setInteger("MirrorMeta", mirrorState.getBlock().getMetaFromState(mirrorState));
        }
        getTileData().setBoolean("locked", locked);
        return super.writeToNBT(compound);
    }
}
