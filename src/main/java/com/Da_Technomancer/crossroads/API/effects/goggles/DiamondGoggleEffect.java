package com.Da_Technomancer.crossroads.API.effects.goggles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class DiamondGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<ITextComponent> chat, BlockRayTraceResult ray){
		//Effect in SendGoggleConfigureToServer
	}
}