package com.Da_Technomancer.crossroads.dimensions;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDimLoadToClient;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class ModDimensions{

	public static DimensionType workspaceDimType;
	public static DimensionType prototypeDimType;
	private static HashMap<GameProfileNonPicky, Integer> playerDim;

	public static void init(){
		workspaceDimType = DimensionType.register(Main.MODID, "_workspace", 567, WorkspaceWorldProvider.class, false);
		prototypeDimType = DimensionType.register(Main.MODID, "_prototype", 568, WorkspaceWorldProvider.class, false);
		
		DimensionManager.registerDimension(27, prototypeDimType);
	}

	public static void loadDims(){
		for(int i : DimensionManager.getDimensions(workspaceDimType)){
			DimensionManager.unregisterDimension(i);
		}
		//This hasn't broken in testing, but there are far too many ways for this to go wrong. Better safe then sorry.
		try{
			
			playerDim = PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), DimensionManager.getWorld(0).getMinecraftServer() == null ? null : DimensionManager.getWorld(0).getMinecraftServer().getPlayerProfileCache()).playerDim;
			for(int id : playerDim.values()){
				DimensionManager.registerDimension(id, workspaceDimType);
			}
			ModPackets.network.sendToAll(new SendDimLoadToClient(playerDim.values().toArray(new Integer[playerDim.size()])));
		}catch(Exception ex){
			if(ModConfig.wipeInvalidMappings.getBoolean()){
				if(playerDim != null){
					playerDim.clear();
				}
				Main.logger.fatal(Main.MODID + ": Something went wrong while loading the player dimension mappings. Attempting to wipe the mappings. Shutting down. It should work if you restart now.", ex);
			}else{
				Main.logger.fatal(Main.MODID + ": Something went wrong while loading the player dimension mappings. Shutting down. If you would like to wipe the mappings completely, there is a config option.", ex);
			}
			throw ex;
		}
	}

	/**
	 * This does not initialize the dimension. If needed, run 
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension.
	 */
	public static int getDimForPlayer(EntityPlayerMP play){
		return getDimForPlayer(new GameProfileNonPicky(play.getGameProfile()));
	}
	
	/**
	 * This does not initialize the dimension. If needed, run 
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension.
	 */
	public static int getDimForPlayer(GameProfileNonPicky play){
		if(playerDim.containsKey(play)){
			int dim = playerDim.get(play);
			return dim;
		}
		
		int dim = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dim, workspaceDimType);
		playerDim.put(play, dim);
		PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), null).markDirty();
		ModPackets.network.sendToAll(new SendDimLoadToClient(new int[] {dim}));
		return dim;
	}
	
	/**
	 * Sets up the next available prototype dimension chunk, reserves the chunk, and returns the coordinates in long form. 
	 */
	public static long nextFreePrototypeChunk(PrototypePortTypes[] ports){
		//This method assumes that all chunks saved in PrototypeWorldSavedData are in the layout this would create.
		//It creates a grid layout of chunks containing only barriers & air, with the remaining chunks being used for prototypes. The grid is centered on chunk 0,0 (non prototype). 
		//The grid created starts at (-100, -100) and goes to (100, infinity). 
		PrototypeWorldSavedData data = PrototypeWorldSavedData.get(DimensionManager.getWorld(27));
		
		int used = data.prototypeInfo.size();
		
		//Do to the grid, one row has capacity for 100 chunks
		int x = ((used % 100) * 2) - 99;
		int z = (used / 50) - 99;
		
		//This part may redundantly block already blocked chunks. This is a possible optimization point if it ends up mattering. 
		blockChunk(new ChunkPos(x - 1, z - 1));
		blockChunk(new ChunkPos(x - 1, z));
		blockChunk(new ChunkPos(x, z - 1));
		blockChunk(new ChunkPos(x + 1, z));
		blockChunk(new ChunkPos(x, z + 1));
		blockChunk(new ChunkPos(x + 1, z + 1));
		
		long chunk = MiscOp.getLongFromChunkPos(new ChunkPos(x, z));
		data.prototypeInfo.put(chunk, new PrototypeInfo(ports));
		return chunk;
	}
	
	private static void blockChunk(ChunkPos pos){
		WorldServer world = DimensionManager.getWorld(27);
		for(int x = pos.getXStart(); x <= pos.getXEnd(); x++){
			for(int z = pos.getZStart(); z <= pos.getZEnd(); z++){
				for(int y = 0; y < 256; y++){
					if(x == pos.getXStart() || x == pos.getXEnd() || z == pos.getZStart() || z == pos.getZEnd()){
						world.setBlockState(new BlockPos(x, y, z), Blocks.BARRIER.getDefaultState(), 0);
					}
				}
			}
		}
	}
}
