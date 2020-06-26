package com.bitanalytic.syncmod.util;

import com.bitanalytic.syncmod.crafting.recipe.BackpackUpgradeRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;

public class ModRecipes {
	public static void init() {
		IRecipeSerializer.register(BackpackUpgradeRecipe.NAME.toString(), BackpackUpgradeRecipe.SERIALIZER);
	}
}
