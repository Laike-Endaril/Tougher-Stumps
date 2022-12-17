package com.wynprice.secretroomsmod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class TileEntityFakeDoor extends TileEntity
{
    public static HashMap<Integer, HashMap<BlockPos, IBlockState>> RENDER_MAP = new HashMap<>();

    public static HashMap<BlockPos, IBlockState> getMap(World world)
    {
        return RENDER_MAP.computeIfAbsent(world.provider.getDimension(), o -> new HashMap<>());
    }

    public static IBlockState getTextureState(World world, BlockPos pos)
    {
        IBlockState result = getMap(world).get(pos);
        if (result != null) return result;


        return world.getTileEntity(pos) instanceof TileEntityFakeDoor ? ((TileEntityFakeDoor) world.getTileEntity(pos)).getTextureState() : world.getBlockState(pos.down()).getActualState(world, pos);
    }

    public IBlockState getTextureState()
    {
        BlockPos pos2 = pos.down();
        TileEntity te = world.getTileEntity(pos2);
        while (te instanceof TileEntityFakeDoor)
        {
            pos2 = pos2.down();
            te = world.getTileEntity(pos2);
        }
        return world.getBlockState(pos2).getActualState(world, pos);
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
}
