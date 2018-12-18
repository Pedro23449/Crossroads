package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		if(ray == null){
			return;
		}

		OmniMeter.measure(chat, player, player.world, ray.getBlockPos(), ray.sideHit, (float) ray.hitVec.x, (float) ray.hitVec.y, (float) ray.hitVec.z);
	}
}