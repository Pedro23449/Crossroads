package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelPump;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class SteamTurbineRenderer extends TileEntitySpecialRenderer<SteamTurbineTileEntity>{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/model/pump.png");
	private static final ModelPump MODEL = new ModelPump();

	@Override
	public void render(SteamTurbineTileEntity turbine, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(turbine == null || turbine.getWorld().getBlockState(turbine.getPos()).getBlock() != ModBlocks.steamTurbine){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5F, 0F, .5F);
		GlStateManager.rotate(turbine.getCompletion(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		MODEL.renderScrew();
		GlStateManager.popMatrix();
	}
}
