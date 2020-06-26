package com.bitanalytic.syncmod.crafting.recipe;

import com.bitanalytic.syncmod.SyncMod;
import com.bitanalytic.syncmod.items.LeatherBackpack;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Collection;

public class BackpackUpgradeRecipe extends SpecialRecipe {
	private enum Upgrade {NONE, SIZE, AUTO_PICKUP}

	;

	public static final ResourceLocation NAME = SyncMod.getId("upgrade_backpack");
	public static final Serializer SERIALIZER = new Serializer();

	Upgrade currentUpgrade = Upgrade.NONE;

	public BackpackUpgradeRecipe(ResourceLocation idIn) {
		super(idIn);
	}


	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int chestCount = 0;
		int enderPearlCount = 0;
		boolean isUpgradable = false;
		boolean matchesRecipe = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem().isIn(Tags.Items.GEMS_DIAMOND)) {
				chestCount++;
				continue;
			}
			if (stack.getItem().isIn(Tags.Items.ENDER_PEARLS)) {
				enderPearlCount++;
				continue;
			}
			if (stack.getItem() instanceof LeatherBackpack) {
				if (stack.getOrCreateTag().getInt("inventorySize") < 54) {
					isUpgradable = true;
				} else if (!stack.getOrCreateTag().getBoolean("autoPickup")) {
					isUpgradable = true;
				}
			}
		}

		if (chestCount == 1 && enderPearlCount == 0) {
			currentUpgrade = Upgrade.SIZE;
			matchesRecipe = true;
		} else if (enderPearlCount == 1 && chestCount == 0) {
			currentUpgrade = Upgrade.AUTO_PICKUP;
			matchesRecipe = true;
		}

		return matchesRecipe && isUpgradable;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack backpack = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() instanceof LeatherBackpack) {
				backpack = stack;
				break;
			}
		}

		ItemStack result = backpack.copy();

		switch (currentUpgrade) {
			case NONE:
				break;
			case SIZE:
				int inventorySize = ((LeatherBackpack)result.getItem()).getInventorySize(result);
				result.getOrCreateTag().putInt("inventorySize", inventorySize + 9);
				break;
			case AUTO_PICKUP:
				result.getOrCreateTag().putBoolean("autoPickup", true);
				break;
		}

		return result;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BackpackUpgradeRecipe> {
		@Override
		public BackpackUpgradeRecipe read(ResourceLocation recipeId, JsonObject json) {
			return new BackpackUpgradeRecipe(recipeId);
		}

		@Override
		public BackpackUpgradeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			return new BackpackUpgradeRecipe(recipeId);
		}

		@Override
		public void write(PacketBuffer buffer, BackpackUpgradeRecipe recipe) {
		}
	}
}
