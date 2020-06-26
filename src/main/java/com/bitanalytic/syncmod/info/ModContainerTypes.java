package com.bitanalytic.syncmod.info;

import com.bitanalytic.syncmod.SyncMod;
import com.bitanalytic.syncmod.container.BackpackContainer;
import com.bitanalytic.syncmod.gui.BackpackContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {
	public static ContainerType<BackpackContainer> backpack;

	private ModContainerTypes() {}

	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		backpack = register("backpack", new ContainerType<>(BackpackContainer::new));
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerScreens(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(backpack, BackpackContainerScreen::new);
	}

	private static <T extends Container> ContainerType<T> register(String name, ContainerType<T> type) {
		ResourceLocation id = SyncMod.getId(name);
		type.setRegistryName(id);
		ForgeRegistries.CONTAINERS.register(type);
		return type;
	}
}


