package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.items.crafting.BlockCraftingStack;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class FusionBeamRecipe implements IRecipeWrapper{

	private final BlockCraftingStack input;
	private final IBlockState endState;
	private final int minPower;
	private final boolean voi;

	public FusionBeamRecipe(BlockCraftingStack input, IBlockState endState, int minPower, boolean voi){
		this.input = input;
		this.endState = endState;
		this.minPower = minPower;
		this.voi = voi;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("Minimum power:  " + minPower, 40, 20, 4210752);
		minecraft.fontRenderer.drawString(voi ? "Void-Fusion beam" : "Fusion beam", 40, 30, 4210752);
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

	@Override
	public void getIngredients(IIngredients ingredients){
		ArrayList<ItemStack> ing = new ArrayList<ItemStack>();
		for(IBlockState state : input.getMatchingList()){
			ing.add(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
		}
		ingredients.setInput(ItemStack.class, ing);
		ingredients.setOutput(ItemStack.class, ImmutableList.of(new ItemStack(endState.getBlock(), 1, endState.getBlock().getMetaFromState(endState))));
	}
}