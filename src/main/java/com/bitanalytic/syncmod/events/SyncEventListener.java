package com.bitanalytic.syncmod.events;

import com.bitanalytic.syncmod.container.BackpackContainer;
import com.bitanalytic.syncmod.items.LeatherBackpack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber
public class SyncEventListener {

	@SubscribeEvent
	public static void playerPickupItemEvent(EntityItemPickupEvent event) {
		PlayerEntity player = event.getPlayer();
		ItemStack itemStack = null;
		LeatherBackpack backpack = null;
		//Loop through hotbar and find backpack
		for (int i = 0; i < 9; i++) {
			ItemStack stack = player.inventory.mainInventory.get(i);
			if (stack.getItem() instanceof LeatherBackpack) {
				itemStack = stack;
				backpack = (LeatherBackpack) stack.getItem();
				if (backpack.getAutoPickup(stack) && backpack.getAutoPickupEnabled(stack)) {
					break;
				} else {
					backpack = null;
				}
			}
		}
		if (backpack == null) {
			return;
		}

		IItemHandler handler = null;
		if (player.openContainer instanceof BackpackContainer) {
			BackpackContainer backpackContainer = (BackpackContainer) player.openContainer;
			if (backpackContainer.item == itemStack) {
				handler = backpackContainer.itemHandler;
				((BackpackContainer) player.openContainer).copyInventory(handler);
			} else {
				handler = backpack.getInventory(itemStack);
			}
		} else {
			handler = backpack.getInventory(itemStack);
		}
		if (mergeItems(handler, event)) {
			event.getItem().getItem().setCount(0);
			event.setCanceled(true);
		}
		backpack.saveInventory(itemStack, handler);
	}

	private static boolean mergeItems(IItemHandler handler, EntityItemPickupEvent event) {
		boolean merged = false;
		for (int i = 0; i < handler.getSlots() && !merged; i++) {
			ItemStack stack = handler.getStackInSlot(i);
			int count = stack.getCount();
			if (count >= 64 || stack.getItem() != event.getItem().getItem().getItem()) {
				continue;
			}

			int eventCount = event.getItem().getItem().getCount();
			int diff = 64 - count;
			if (eventCount > diff) {
				eventCount -= diff;
				event.getItem().getItem().setCount(eventCount);
				stack.setCount(64);
			} else {
				count += eventCount;
				stack.setCount(count);
				event.getItem().getItem().setCount(0);
				merged = true;
			}
			handler.getStackInSlot(i).setCount(stack.getCount());
		}

		if (!merged) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);
				if (stack.getItem() instanceof AirItem) {
					stack = event.getItem().getItem().copy();
					handler.insertItem(i, stack, false);
					handler.getStackInSlot(i).setCount(stack.getCount());
					merged = true;
					break;
				}
			}
		}

		return merged;
	}
}
