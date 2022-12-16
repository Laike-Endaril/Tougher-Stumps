package com.wynprice.secretroomsmod.base;

import com.wynprice.secretroomsmod.base.interfaces.ISecretBlock;
import com.wynprice.secretroomsmod.base.interfaces.ISecretTileEntity;
import com.wynprice.secretroomsmod.render.fakemodels.FakeBlockModel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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
        if (!(tileEntity instanceof ISecretTileEntity)) return;


        ISecretTileEntity te = (ISecretTileEntity) tileEntity;
        GlStateManager.pushMatrix();
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            EntityPlayer entityplayer = Minecraft.getMinecraft().player;
            double d0 = (entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) partialTicks);
            double d1 = (entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) partialTicks);
            double d2 = (entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) partialTicks);
            Tessellator.getInstance().getBuffer().setTranslation(-d0, -d1, -d2);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            World world = getWorld();
            Tessellator.getInstance().getBuffer().noColor();
            Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            Block block = tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock();
            ArrayList<Integer> tintList = new ArrayList<>();
            currentRender = tileEntity.getWorld().getBlockState(tileEntity.getPos()).getActualState(tileEntity.getWorld(), tileEntity.getPos());
            currentPos = tileEntity.getPos();
            currentWorld = tileEntity.getWorld();
            if (block instanceof ISecretBlock && te.getMirrorState() != null)
            {
                IBlockState renderState = te.getMirrorState().getBlock().getActualState(te.getMirrorState(), tileEntity.getWorld(), tileEntity.getPos());
                GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

                currentRender = ((ISecretBlock) block).overrideThisState(world, currentPos, currentRender);
                try
                {
                    Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ((ISecretBlock) block).phaseModel(new FakeBlockModel(renderState)),
                            world.getBlockState(tileEntity.getPos()), tileEntity.getPos(), Tessellator.getInstance().getBuffer(), false);
                }
                catch (Throwable e)
                {
                    try
                    {
                        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ((ISecretBlock) block).phaseModel(new FakeBlockModel(renderState)),
                                renderState, tileEntity.getPos(), Tessellator.getInstance().getBuffer(), false);
                    }
                    catch (Throwable t)
                    {
                        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ((ISecretBlock) block).phaseModel(new FakeBlockModel(Blocks.STONE.getDefaultState())),
                                Blocks.STONE.getDefaultState(), tileEntity.getPos(), Tessellator.getInstance().getBuffer(), false);
                    }
                }

                for (BakedQuad quad : ((ISecretBlock) block).phaseModel(new FakeBlockModel(renderState)).getQuads(renderState, null, 0L))
                    tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
                for (EnumFacing face : EnumFacing.values())
                    for (BakedQuad quad : ((ISecretBlock) block).phaseModel(new FakeBlockModel(renderState)).getQuads(renderState, face, 0L))
                        tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
            }
            Collections.reverse(tintList);
            for (int i = 0; i < tessellator.getBuffer().getVertexCount() + 1; i++)
            {
                int sec = Math.floorDiv(i - 1, 4);
                if (sec < 0 || tintList.size() <= sec || tintList.get(sec) < 0)
                    continue;
                Color color = new Color(Minecraft.getMinecraft().getBlockColors()
                        .colorMultiplier(te.getMirrorState(), tileEntity.getWorld(), tileEntity.getPos(), tintList.get(sec)));
                tessellator.getBuffer().putColorMultiplier(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, i);
            }

            tessellator.draw();
            GlStateManager.shadeModel(7424);
            Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
            GlStateManager.disableBlend();
            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();
    }
}
