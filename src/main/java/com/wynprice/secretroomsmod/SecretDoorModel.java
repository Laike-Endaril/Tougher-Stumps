package com.wynprice.secretroomsmod;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecretDoorModel implements IBakedModel
{
    protected static final HashMap<IBakedModel, SecretDoorModel> SECRET_DOOR_MODELS = new HashMap<>();

    protected IBakedModel textureBlockModel;

    protected SecretDoorModel(IBakedModel textureBlockModel)
    {
        this.textureBlockModel = textureBlockModel;
    }

    public static SecretDoorModel provideModel(IBakedModel textureBlockModel)
    {
        return SECRET_DOOR_MODELS.computeIfAbsent(textureBlockModel, o -> new SecretDoorModel(textureBlockModel));
    }

    public static IBakedModel getModel(IBlockState state)
    {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
    }

    public static IBakedModel getModel(ResourceLocation resourceLocation)
    {
        IBakedModel bakedModel;
        IModel model;
        try
        {
            model = ModelLoaderRegistry.getModel(resourceLocation);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        return bakedModel;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return textureBlockModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return textureBlockModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return textureBlockModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return textureBlockModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return textureBlockModel.getOverrides();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(new ItemStack(Items.ARROW)).getItemCameraTransforms();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        if (!BlockSecretDoor.class.isAssignableFrom(BaseTERender.currentRender.getBlock().getClass())) return textureBlockModel.getQuads(state, side, rand);


        IBlockState textureState = ((TileEntityFakeDoor) BaseTERender.currentWorld.getTileEntity(BaseTERender.currentPos)).getTextureState();
        IBakedModel textureModel = getModel(textureState);
        IBlockState s = BaseTERender.currentRender;
        IBlockState baseState = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, s.getValue(BlockDoor.FACING)).withProperty(BlockDoor.HALF, s.getValue(BlockDoor.HALF)).withProperty(BlockDoor.HINGE, s.getValue(BlockDoor.HINGE)).withProperty(BlockDoor.OPEN, s.getValue(BlockDoor.OPEN)).withProperty(BlockDoor.POWERED, s.getValue(BlockDoor.POWERED));

        ArrayList<BakedQuad> finalList = new ArrayList<>();
        for (BakedQuad quad : getModel(baseState).getQuads(baseState, side, rand))
        {
            List<BakedQuad> textureQuads = textureModel.getQuads(textureState, side, rand);
            if (textureQuads.isEmpty()) textureQuads = textureModel.getQuads(textureState, null, rand);
            if (textureQuads.isEmpty())
            {
                for (int i = EnumFacing.VALUES.length - 1; i >= 0; i--)
                {
                    textureQuads = textureModel.getQuads(textureState, EnumFacing.VALUES[i], rand);
                    if (!textureQuads.isEmpty()) break;
                }
            }

            for (BakedQuad textureQuad : textureQuads)
            {
                int[] quadVertexData = new int[quad.getVertexData().length];
                System.arraycopy(quad.getVertexData(), 0, quadVertexData, 0, quadVertexData.length);
                int[] textureQuadVertexData = textureQuad.getVertexData();
                if (textureQuadVertexData != null)
                {
                    //TODO This loop replaces the texture of the door with the texture of the block below it
                    //TODO For mine, want to use similar code with the stump and its log type, but more specific code for the roots with their additional soil type
                    for (int i : new int[]{4, 5, 11, 12, 18, 19, 25, 26}) quadVertexData[i] = textureQuadVertexData[i];
                }
                finalList.add(new BakedQuad(quadVertexData, textureQuad.getTintIndex(), quad.getFace(), Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state), textureQuad.shouldApplyDiffuseLighting(), textureQuad.getFormat()));
            }
        }
        return finalList;
    }
}
