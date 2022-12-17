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

    public IBlockState getNormalStateWith(IBlockState s)
    {
        return Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, s.getValue(BlockDoor.FACING)).withProperty(BlockDoor.HALF, s.getValue(BlockDoor.HALF))
                .withProperty(BlockDoor.HINGE, s.getValue(BlockDoor.HINGE)).withProperty(BlockDoor.OPEN, s.getValue(BlockDoor.OPEN)).withProperty(BlockDoor.POWERED, s.getValue(BlockDoor.POWERED));
    }

    protected Class<? extends BaseBlockDoor> getBaseBlockClass()
    {
        return BaseBlockDoor.class;
    }

    protected EnumFacing[] fallbackOrder()
    {
        EnumFacing[] list = new EnumFacing[EnumFacing.VALUES.length + 1];
        for (int i = 0; i < EnumFacing.VALUES.length; i++)
            list[i] = EnumFacing.VALUES[i];
        list[EnumFacing.VALUES.length] = null;
        return list;
    }

    protected RenderInfo getRenderInfo(EnumFacing face, IBlockState teMirrorState)
    {
        return new RenderInfo(teMirrorState, getModel(teMirrorState));
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        if (getBaseBlockClass() != null && !(getBaseBlockClass().isAssignableFrom(BaseTERender.currentRender.getBlock().getClass())))
            return super.getQuads(state, side, rand);
        ArrayList<BakedQuad> finalList = new ArrayList<>();
        RenderInfo renderInfo = getRenderInfo(side, ((TileEntityInfomationHolder) BaseTERender.currentWorld.getTileEntity(BaseTERender.currentPos)).getMirrorState());
        IBlockState normalState = getNormalStateWith(BaseTERender.currentRender);
        if (renderInfo != null)
            for (BakedQuad quad : getModel(normalState).getQuads(normalState, side, rand))
            {
                List<BakedQuad> secList = new ArrayList<>(renderInfo.renderModel.getQuads(renderInfo.blockstate, side, rand));
                if (secList.isEmpty())
                    for (EnumFacing facing : fallbackOrder())
                        if (!renderInfo.renderModel.getQuads(renderInfo.blockstate, facing, rand).isEmpty())
                            secList = renderInfo.renderModel.getQuads(renderInfo.blockstate, facing, rand);
                for (BakedQuad mirrorQuad : secList)
                {
                    int[] vList = new int[quad.getVertexData().length];
                    System.arraycopy(quad.getVertexData(), 0, vList, 0, vList.length);
                    int[] sList = mirrorQuad.getVertexData();
                    int[] cList = {4, 5, 11, 12, 18, 19, 25, 26};
                    if (sList != null)
                        for (int i : cList)
                            vList[i] = sList[i];
                    finalList.add(new BakedQuad(vList, mirrorQuad.getTintIndex(), quad.getFace(),
                            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state), mirrorQuad.shouldApplyDiffuseLighting(), mirrorQuad.getFormat()));
                }
            }
        return finalList;
    }

    public static class RenderInfo
    {
        public final IBlockState blockstate;
        public final IBakedModel renderModel;

        public RenderInfo(IBlockState blockstate, IBakedModel renderModel)
        {
            this.blockstate = blockstate;
            this.renderModel = renderModel;
        }
    }
}
