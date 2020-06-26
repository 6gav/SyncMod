package com.bitanalytic.syncmod;

import com.bitanalytic.syncmod.info.ModContainerTypes;
import com.bitanalytic.syncmod.util.ModRecipes;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class SideProxy {
	SideProxy() {
		// Life-cycle events
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, ModContainerTypes::registerContainerTypes);

		// Other events
		MinecraftForge.EVENT_BUS.register(this);

		ModRecipes.init();
	}

	/**
	 * Called after registry events, so we know blocks, items, etc. are registered
	 *
	 * @param event The event
	 */
	private static void commonSetup(FMLCommonSetupEvent event) {
		SyncMod.LOGGER.debug("commonSetup for Tutorial Mod");
	}

	/**
	 * Send IMC messages to other mods
	 *
	 * @param event The event
	 */
	private static void enqueueIMC(final InterModEnqueueEvent event) {
	}

	/**
	 * Receive and process IMC messages from other mods
	 *
	 * @param event The event
	 */
	private static void processIMC(final InterModProcessEvent event) {
	}

	/**
	 * One of several events fired when a server (integrated or dedicated) is starting up. Here, we
	 * can register commands and classes which process resources. For example, if you have a machine
	 * with custom recipes, you would register your resource manager and reload resources as the
	 * server is starting. We will cover that in a later episode.
	 *
	 * @param event The event
	 */
	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
	}

	/**
	 * In addition to everything handled by SideProxy, this will handle client-side resources. This
	 * is where you would register things like models and color handlers.
	 */
	static class Client extends SideProxy {
		Client() {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ModContainerTypes::registerScreens);
		}

		private static void clientSetup(FMLClientSetupEvent event) {
		}
	}

	/**
	 * Only created on dedicated servers.
	 */
	static class Server extends SideProxy {
		Server() {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Server::serverSetup);
		}

		private static void serverSetup(FMLDedicatedServerSetupEvent event) {
		}
	}
}
