package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EldrineEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, double amount,double heat, EnumMatterPhase phase){

		Chunk c = world.getChunkFromBlockCoords(pos);
		if(world.getBiome(pos) != Biomes.HELL){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.HELL);
			ModPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, (byte) Biome.getIdForBiome(Biomes.HELL)), world.provider.getDimension());
		}

		IBlockState oldState = world.getBlockState(pos);
		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<IBlockState> pred : AetherEffect.CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.GLOWSTONE.getDefaultState()){
					world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.LAVA.getDefaultState()){
					world.setBlockState(pos, Blocks.LAVA.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.NETHERRACK.getDefaultState()){
					world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.SOIL_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.SOUL_SAND.getDefaultState()){
					world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
				}
				return;
			}
		}
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		IBlockState oldState = world.getBlockState(pos);
		if(contents != null && contents[13] != null && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
			return;
		}
		doEffect(world, pos, amount, temp, phase);
	}
}
