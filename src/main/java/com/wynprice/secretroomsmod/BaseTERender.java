package com.wynprice.secretroomsmod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class BaseTERender<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
    public static IBlockState currentRender;
    public static World currentWorld;
    public static BlockPos currentPos;

    @Override
    public void render(T tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (!(tileEntity instanceof TileEntityFakeDoor)) return;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = mc.player;
        double xx = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double yy = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double zz = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;
        buffer.setTranslation(-xx, -yy, -zz);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderHelper.disableStandardItemLighting();


        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        buffer.noColor();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        ArrayList<Integer> tintList = new ArrayList<>();
        currentRender = tileEntity.getWorld().getBlockState(tileEntity.getPos()).getActualState(tileEntity.getWorld(), tileEntity.getPos());
        currentPos = tileEntity.getPos();
        currentWorld = tileEntity.getWorld();

        World world = getWorld();
        Block block = world.getBlockState(tileEntity.getPos()).getBlock();
        TileEntityFakeDoor te = (TileEntityFakeDoor) tileEntity;
        if (block instanceof BlockFakeDoor && te.getTextureState() != null)
        {
            BlockFakeDoor blockDoor = (BlockFakeDoor) block;
            IBlockState renderState = te.getTextureState().getBlock().getActualState(te.getTextureState(), tileEntity.getWorld(), tileEntity.getPos());
            GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

            currentRender = ((BlockFakeDoor) block).overrideThisState(world, currentPos, currentRender);
            BlockModelRenderer renderer = mc.getBlockRendererDispatcher().getBlockModelRenderer();
            try
            {
                renderer.renderModel(world, blockDoor.phaseModel(new FakeBlockModel(renderState)), world.getBlockState(tileEntity.getPos()), tileEntity.getPos(), buffer, false);
            }
            catch (Throwable e)
            {
                try
                {
                    renderer.renderModel(world, blockDoor.phaseModel(new FakeBlockModel(renderState)), renderState, tileEntity.getPos(), buffer, false);
                }
                catch (Throwable t)
                {
                    renderer.renderModel(world, blockDoor.phaseModel(new FakeBlockModel(Blocks.STONE.getDefaultState())), Blocks.STONE.getDefaultState(), tileEntity.getPos(), buffer, false);
                }
            }

            for (BakedQuad quad : blockDoor.phaseModel(new FakeBlockModel(renderState)).getQuads(renderState, null, 0L)) tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
            for (EnumFacing face : EnumFacing.values())
            {
                for (BakedQuad quad : blockDoor.phaseModel(new FakeBlockModel(renderState)).getQuads(renderState, face, 0L))
                {
                    tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
                }
            }
        }
        Collections.reverse(tintList);
        for (int i = 0; i < buffer.getVertexCount() + 1; i++)
        {
            int sec = Math.floorDiv(i - 1, 4);
            if (sec < 0 || tintList.size() <= sec || tintList.get(sec) < 0) continue;
            Color color = new Color(mc.getBlockColors().colorMultiplier(te.getTextureState(), tileEntity.getWorld(), tileEntity.getPos(), tintList.get(sec)));
            buffer.putColorMultiplier(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, i);
        }

        tessellator.draw();
        GlStateManager.shadeModel(7424);
        buffer.setTranslation(0, 0, 0);


        RenderHelper.enableStandardItemLighting();

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
