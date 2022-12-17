package com.wynprice.secretroomsmod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
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

        World world = getWorld();
        BlockPos pos = tileEntity.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof BlockSecretDoor)) return;


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
        currentRender = tileEntity.getWorld().getBlockState(pos).getActualState(tileEntity.getWorld(), pos);
        currentPos = pos;
        currentWorld = tileEntity.getWorld();

        TileEntityFakeDoor te = (TileEntityFakeDoor) tileEntity;
        IBlockState renderState = te.getTextureState().getBlock().getActualState(te.getTextureState(), tileEntity.getWorld(), pos);
        GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

        currentRender = ((BlockSecretDoor) block).overrideThisState(world, currentPos, currentRender);
        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, new DoorFakeModel(new FakeBlockModel(renderState)), state, pos, buffer, false);

        for (BakedQuad quad : new DoorFakeModel(new FakeBlockModel(renderState)).getQuads(renderState, null, 0)) tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
        for (EnumFacing face : EnumFacing.values())
        {
            for (BakedQuad quad : new DoorFakeModel(new FakeBlockModel(renderState)).getQuads(renderState, face, 0))
            {
                tintList.add(quad.hasTintIndex() ? quad.getTintIndex() : -1);
            }
        }
        Collections.reverse(tintList);
        Color color;
        for (int i = 0; i < buffer.getVertexCount() + 1; i++)
        {
            int sec = Math.floorDiv(i - 1, 4);
            if (sec < 0 || tintList.size() <= sec || tintList.get(sec) < 0) continue;

            color = new Color(mc.getBlockColors().colorMultiplier(te.getTextureState(), tileEntity.getWorld(), pos, tintList.get(sec)));
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
