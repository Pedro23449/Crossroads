package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CowLeggings extends ArmorItem{

	protected CowLeggings(){
		super(ChickenBoots.BOBO_MATERIAL, EquipmentSlotType.LEGS, new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "cow_leggings";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		if(player.getActivePotionEffect(Effects.POISON) != null || player.getActivePotionEffect(Effects.WITHER) != null || player.getActivePotionEffect(Effects.NAUSEA) != null || player.getActivePotionEffect(Effects.BLINDNESS) != null || player.getActivePotionEffect(Effects.SLOWNESS) != null || player.getActivePotionEffect(Effects.WEAKNESS) != null || player.getActivePotionEffect(Effects.HUNGER) != null){
			player.removePotionEffect(Effects.POISON);
			player.removePotionEffect(Effects.WITHER);
			player.removePotionEffect(Effects.NAUSEA);
			player.removePotionEffect(Effects.BLINDNESS);
			player.removePotionEffect(Effects.SLOWNESS);
			player.removePotionEffect(Effects.WEAKNESS);
			player.removePotionEffect(Effects.HUNGER);
			player.removePotionEffect(Effects.MINING_FATIGUE);
			player.removePotionEffect(Effects.UNLUCK);
			player.removePotionEffect(Effects.BAD_OMEN);
			world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_COW_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
