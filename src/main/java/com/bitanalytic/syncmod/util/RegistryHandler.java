package com.bitanalytic.syncmod.util;

import com.bitanalytic.syncmod.SyncMod;
import com.bitanalytic.syncmod.events.SyncEventListener;
import com.bitanalytic.syncmod.items.LeatherBackpack;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, SyncMod.MOD_ID);

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//Items
	public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("leather_backpack", LeatherBackpack::new);
}
