package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.DefaultChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.heat.DefaultHeatHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.beams.DefaultBeamHandler;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.redstone.DefaultAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.API.technomancy.DefaultFluxHandler;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

public class Capabilities{

	@CapabilityInject(IHeatHandler.class)
	public static Capability<IHeatHandler> HEAT_CAPABILITY = null;

	@CapabilityInject(IAxleHandler.class)
	public static Capability<IAxleHandler> AXLE_CAPABILITY = null;
	
	@CapabilityInject(ICogHandler.class)
	public static Capability<ICogHandler> COG_CAPABILITY = null;
	
	@CapabilityInject(IBeamHandler.class)
	public static Capability<IBeamHandler> MAGIC_CAPABILITY = null;
	
	@CapabilityInject(IAxisHandler.class)
	public static Capability<IAxisHandler> AXIS_CAPABILITY = null;
	
	@CapabilityInject(ISlaveAxisHandler.class)
	public static Capability<ISlaveAxisHandler> SLAVE_AXIS_CAPABILITY = null;
	
	@CapabilityInject(IAdvancedRedstoneHandler.class)
	public static Capability<IAdvancedRedstoneHandler> ADVANCED_REDSTONE_CAPABILITY = null;
	
	@CapabilityInject(IChemicalHandler.class)
	public static Capability<IChemicalHandler> CHEMICAL_CAPABILITY = null;

	@CapabilityInject(IFluxHandler.class)
	public static Capability<IFluxHandler> FLUX_CAPABILITY = null;

	public static void register(){
		CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultStorage<>(), DefaultHeatHandler::new);
		CapabilityManager.INSTANCE.register(IAxleHandler.class, new DefaultStorage<>(), DefaultAxleHandler::new);
		CapabilityManager.INSTANCE.register(ICogHandler.class, new DefaultStorage<>(), DefaultCogHandler::new);
		CapabilityManager.INSTANCE.register(IBeamHandler.class, new DefaultStorage<>(), DefaultBeamHandler::new);
		CapabilityManager.INSTANCE.register(IAxisHandler.class, new DefaultStorage<>(), DefaultAxisHandler::new);
		CapabilityManager.INSTANCE.register(ISlaveAxisHandler.class, new DefaultStorage<>(), DefaultSlaveAxisHandler::new);
		CapabilityManager.INSTANCE.register(IAdvancedRedstoneHandler.class, new DefaultStorage<>(), DefaultAdvancedRedstoneHandler::new);
		CapabilityManager.INSTANCE.register(IChemicalHandler.class, new DefaultStorage<>(), DefaultChemicalHandler::new);
		CapabilityManager.INSTANCE.register(IFluxHandler.class, new DefaultStorage<>(), DefaultFluxHandler::new);
	}

	/**
	 * All credit for this class goes to aidancbrady.
	 * This code originally came from his mod Mekanism.
	 * Copying it is allowed under Mekanism's license at the time of writing this.
	 */
	private static class DefaultStorage<T> implements Capability.IStorage<T>{

		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side){
			if(instance instanceof INBTSerializable)
				return ((INBTSerializable) instance).serializeNBT();
			return new NBTTagCompound();
		}

		@SuppressWarnings({"unchecked"})
		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt){
			if(instance instanceof INBTSerializable){
				Class<? extends NBTBase> nbtClass = ((INBTSerializable) instance).serializeNBT().getClass();

				if(nbtClass.isInstance(nbt)){
					((INBTSerializable) instance).deserializeNBT(nbtClass.cast(nbt));
				}
			}
		}
	}
}
