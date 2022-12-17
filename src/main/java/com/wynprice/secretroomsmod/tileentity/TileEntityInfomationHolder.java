package com.wynprice.secretroomsmod.tileentity;

import com.wynprice.secretroomsmod.base.BaseBlockDoor;
import com.wynprice.secretroomsmod.handler.ParticleHandler;
import com.wynprice.secretroomsmod.proxy.ClientProxy;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
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

    public static IBlockState getMirrorState(IBlockAccess access, BlockPos pos)
    {
        IBlockState returnState = Blocks.AIR.getDefaultState();
        final HashMap<Integer, WorldServer> worlds = new HashMap<>();
        if (FMLCommonHandler.instance().getMinecraftServerInstance() != null)
            for (WorldServer server : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
                worlds.put(server.provider.getDimension(), server);
        for (int dim : RENDER_MAP.keySet())
            if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
                returnState = getMirrorState(ClientProxy.getPlayer().world, pos);
            else if (worlds.get(dim) == access)
                returnState = getMirrorState(worlds.get(dim), pos);
        return returnState == null ? Blocks.STONE.getDefaultState() : returnState;
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

    public IBlockState getMirrorState()
    {
        if (mirrorState == null && ParticleHandler.BLOCKBRAKERENDERMAP.containsKey(pos))
            mirrorState = ParticleHandler.BLOCKBRAKERENDERMAP.get(pos);
        if (mirrorState == null && RENDER_MAP.containsKey(pos))
            mirrorState = TileEntityInfomationHolder.getMap(world).get(pos);
        return mirrorState;
    }

    public void setMirrorState(IBlockState mirrorState, @Nullable BlockPos pos)
    {
        if (!locked) setMirrorStateForcable(mirrorState, pos);
        locked = true;
    }

    public void setMirrorStateForcable(IBlockState mirrorState, @Nullable BlockPos pos)
    {
        if (mirrorState.getBlock() instanceof BaseBlockDoor) mirrorState = Blocks.STONE.getDefaultState();
        TileEntityInfomationHolder.getMap(world).put(this.pos, mirrorState);
        this.mirrorState = mirrorState.getBlock().getStateFromMeta(mirrorState.getBlock().getMetaFromState(mirrorState));
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
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

}
