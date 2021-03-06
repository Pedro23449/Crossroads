package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class Phial extends AbstractGlassware{

	public Phial(boolean crystal){
		super(GlasswareTypes.PHIAL, crystal);
		String name = "phial_" + (crystal ? "cryst" : "glass");
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		ReagentMap contents = getReagants(context.getItem());
		if(contents.getTotalQty() != 0){
			if(!context.getWorld().isRemote){
				AlchemyUtil.releaseChemical(context.getWorld(), context.getPos(), contents);
				if(context.getPlayer() == null || !context.getPlayer().isCreative()){
					setReagents(context.getItem(), new ReagentMap());
				}
			}
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}
