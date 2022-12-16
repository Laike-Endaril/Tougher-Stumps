package com.wynprice.secretroomsmod.base.interfaces;

import com.wynprice.secretroomsmod.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.HashMap;

public interface ISecretTileEntity extends ITickable
{
    HashMap<Integer, HashMap<BlockPos, IBlockState>> RENDER_MAP = new HashMap<>();

    static HashMap<BlockPos, IBlockState> getMap(World world)
    {
        if (!RENDER_MAP.containsKey(world.provider.getDimension()))
            RENDER_MAP.put(world.provider.getDimension(), new HashMap<>());
        return RENDER_MAP.get(world.provider.getDimension());
    }

    void setMirrorState(IBlockState mirrorState, @Nullable BlockPos pos);

    void setMirrorStateForcable(IBlockState mirrorState, @Nullable BlockPos pos);

    IBlockState getMirrorState();

    static IBlockState getMirrorState(World world, BlockPos pos)
    {
        return getMap(world).get(pos) == null && world.getTileEntity(pos) instanceof ISecretTileEntity ? ((ISecretTileEntity) world.getTileEntity(pos)).getMirrorState() : getMap(world).get(pos);
    }

    static IBlockState getMirrorState(IBlockAccess access, BlockPos pos)
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
}
