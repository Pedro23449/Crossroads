package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class HeatCableTileEntity extends ModuleTE{

	protected final boolean[] hasMatch = new boolean[6];
	protected final boolean[] locked = new boolean[6];

	public HeatCableTileEntity(){
		this(HeatInsulators.WOOL);
	}

	public HeatCableTileEntity(HeatInsulators insulator){
		super();
		this.insulator = insulator;
	}

	protected HeatInsulators insulator;

	public Boolean[] getMatches(){
		return new Boolean[] {hasMatch[0], hasMatch[1], hasMatch[2], hasMatch[3], hasMatch[4], hasMatch[5]};
	}

	public void adjust(int side){
		locked[side] = !locked[side];
		if(hasMatch[side]){
			hasMatch[side] = false;
			markSideChanged();
		}
	}

	protected void markSideChanged(){
		int message = 0;
		for(int i = 0; i < 6; i++){
			if(hasMatch[i]){
				message |= 1 << i;
			}
		}
		ModPackets.network.sendToAllAround(new SendLongToClient((byte) 1, message, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 1){
			for(int i = 0; i < 6; i++){
				hasMatch[i] = ((message >>> i) & 1) == 1;
			}
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new CableHeatHandler();
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		double prevTemp = temp;

		boolean sidesChanged = false;

		//Heat transfer
		ArrayList<IHeatHandler> heatHandlers = new ArrayList<>(6);
		for(EnumFacing side : EnumFacing.values()){
			if(locked[side.getIndex()]){
				continue;
			}

			TileEntity te = world.getTileEntity(pos.offset(side));
			if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				IHeatHandler handler = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite());
				temp += handler.getTemp();
				handler.addHeat(-handler.getTemp());
				heatHandlers.add(handler);
				if(!hasMatch[side.getIndex()]){
					hasMatch[side.getIndex()] = true;
					sidesChanged = true;
				}
			}else if(hasMatch[side.getIndex()]){
				hasMatch[side.getIndex()] = false;
				sidesChanged = true;
			}
		}

		if(sidesChanged){
			markSideChanged();
		}

		temp /= heatHandlers.size() + 1;

		for(IHeatHandler handler : heatHandlers){
			handler.addHeat(temp);
		}

		//Energy loss
		double biomeTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		temp += Math.min(insulator.getRate(), Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);

		if(temp != prevTemp){
			markDirty();
		}

		if(temp > insulator.getLimit()){
			if(ModConfig.heatEffects.getBoolean()){
				insulator.getEffect().doEffect(world, pos, 1);
			}else{
				world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		insulator = nbt.hasKey("insul") ? HeatInsulators.valueOf(nbt.getString("insul")) : HeatInsulators.WOOL;
		for(int i = 0; i < 6; i++){
			locked[i] = nbt.getBoolean("lock_" + i);
			hasMatch[i] = nbt.getBoolean("match_" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("insul", insulator.name());
		for(int i = 0; i < 6; i++){
			nbt.setBoolean("lock_" + i, locked[i]);
			nbt.setBoolean("match_" + i, hasMatch[i]);
		}
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		return writeToNBT(super.getUpdateTag());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || !locked[facing.getIndex()])){
			return (T) heatHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class CableHeatHandler extends HeatHandler{

		@Override
		public void init(){
			if(!initHeat){
				if(insulator == HeatInsulators.ICE){
					temp = -10;
				}else{
					temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				}
				initHeat = true;
				markDirty();
			}
		}
	}
}
