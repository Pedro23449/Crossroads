package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.CrucibleContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CrucibleScreen extends MachineGUI<CrucibleContainer, HeatingCrucibleTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fat_collector_gui.png");

	public CrucibleScreen(CrucibleContainer container, PlayerInventory playerInv, ITextComponent name){
		super(container, playerInv, name);
	}

	@Override
	protected void init(){
		super.init();
		initFluidManager(0, 70, 70);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
		blit(matrix, guiLeft + 42, guiTop + 35, 176, 0, te.progressRef.get() * 28 / HeatingCrucibleTileEntity.REQUIRED, 18);

		super.drawGuiContainerBackgroundLayer(matrix, partialTicks, mouseX, mouseY);
	}
}
