package com.wynprice.secretroomsmod.render.fakemodels;

import com.wynprice.secretroomsmod.base.BaseBlockDoor;
import com.wynprice.secretroomsmod.base.BaseTERender;
import com.wynprice.secretroomsmod.tileentity.TileEntityInfomationHolder;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class DoorFakeModel extends FakeBlockModel
{
    public DoorFakeModel(FakeBlockModel model)
    {
        super(model);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        if (!BaseBlockDoor.class.isAssignableFrom(BaseTERender.currentRender.getBlock().getClass())) return super.getQuads(state, side, rand);


        ArrayList<BakedQuad> finalList = new ArrayList<>();
        IBlockState teMirrorState = ((TileEntityInfomationHolder) BaseTERender.currentWorld.getTileEntity(BaseTERender.currentPos)).getMirrorState();
        IBakedModel teMirrorModel = getModel(teMirrorState);
        IBlockState s = BaseTERender.currentRender;
        IBlockState normalState = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, s.getValue(BlockDoor.FACING)).withProperty(BlockDoor.HALF, s.getValue(BlockDoor.HALF)).withProperty(BlockDoor.HINGE, s.getValue(BlockDoor.HINGE)).withProperty(BlockDoor.OPEN, s.getValue(BlockDoor.OPEN)).withProperty(BlockDoor.POWERED, s.getValue(BlockDoor.POWERED));
        for (BakedQuad quad : getModel(normalState).getQuads(normalState, side, rand))
        {
            List<BakedQuad> secList = new ArrayList<>(teMirrorModel.getQuads(teMirrorState, side, rand));
            if (secList.isEmpty())
            {
                EnumFacing[] fallbackOrder = new EnumFacing[EnumFacing.VALUES.length + 1];
                System.arraycopy(EnumFacing.VALUES, 0, fallbackOrder, 0, EnumFacing.VALUES.length);
                fallbackOrder[EnumFacing.VALUES.length] = null;
                for (EnumFacing facing : fallbackOrder)
                {
                    if (!teMirrorModel.getQuads(teMirrorState, facing, rand).isEmpty()) secList = teMirrorModel.getQuads(teMirrorState, facing, rand);
                }
            }
            for (BakedQuad mirrorQuad : secList)
            {
                int[] vList = new int[quad.getVertexData().length];
                System.arraycopy(quad.getVertexData(), 0, vList, 0, vList.length);
                int[] sList = mirrorQuad.getVertexData();
                int[] cList = {4, 5, 11, 12, 18, 19, 25, 26};
                if (sList != null)
                {
                    for (int i : cList) vList[i] = sList[i];
                }
                finalList.add(new BakedQuad(vList, mirrorQuad.getTintIndex(), quad.getFace(), Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state), mirrorQuad.shouldApplyDiffuseLighting(), mirrorQuad.getFormat()));
            }
        }
        return finalList;
    }
}
