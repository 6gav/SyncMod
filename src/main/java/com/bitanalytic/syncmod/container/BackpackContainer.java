package com.bitanalytic.syncmod.container;

import com.bitanalytic.syncmod.info.ModContainerTypes;
import com.bitanalytic.syncmod.items.LeatherBackpack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackContainer extends Container {
	public final ItemStack item;
	public IItemHandler itemHandler;
	private int blocked = -1;


	public BackpackContainer(int id, PlayerInventory playerInventory) {
		super(ModContainerTypes.backpack, id);
		this.item = getHeldItem(playerInventory.player);
		this.itemHandler = ((LeatherBackpack) this.item.getItem()).getInventory(this.item);

		//Add Backpack slots (3 X 9)
		for (int i = 0; i < this.itemHandler.getSlots(); i++) {
			int x = 8 + 18 * (i % 9);
			int y = 18 + 18 * (i / 9);
			addSlot(new SlotItemHandler(this.itemHandler, i, x, y));
		}

		final int rowCount = this.itemHandler.getSlots() / 9;
		final int yOffset = (rowCount - 4) * 18;

		//Player inventory (3 rows of 9)
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + yOffset));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; x++) {
			Slot slot = addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + yOffset) {
				@Override
				public boolean canTakeStack(PlayerEntity playerEntity) {
					return slotNumber != blocked;
				}
			});

			if (x == playerInventory.currentItem && ItemStack.areItemStacksEqual(playerInventory.getCurrentItem(), this.item)) {
				blocked = slot.slotNumber;
			}
		}
	}

	private static ItemStack getHeldItem(PlayerEntity playerEntity) {
		if (isBackpack(playerEntity.getHeldItemMainhand())) {
			return playerEntity.getHeldItemMainhand();
		} else if (isBackpack(playerEntity.getHeldItemOffhand())) {
			return playerEntity.getHeldItemOffhand();
		}
		return ItemStack.EMPTY;
	}

	public int getInventoryRows() {
		return this.itemHandler.getSlots() / 9;
	}

	@Override
	public void onContainerClosed(PlayerEntity playerEntity) {
		super.onContainerClosed(playerEntity);

		((LeatherBackpack) this.item.getItem()).saveInventory(this.item, this.itemHandler);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if(itemstack1.getItem() instanceof LeatherBackpack) {
				return ItemStack.EMPTY;
			}
			if (!slot.isItemValid(itemstack1)) {
				return itemstack;
			}

			itemstack = itemstack1.copy();
			int containerSlots = itemHandler.getSlots();
			if (index < containerSlots) {
				if (!this.mergeItemStack(itemstack1, containerSlots, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
				return ItemStack.EMPTY;
			}


			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity playerEntity) {
		if (slotId < 0 || slotId > inventorySlots.size()) {
			return super.slotClick(slotId, dragType, clickType, playerEntity);
		}

		Slot slot = inventorySlots.get(slotId);
		if (!canTake(slotId, slot, dragType, playerEntity, clickType)) {
			return slot.getStack();
		}

		return super.slotClick(slotId, dragType, clickType, playerEntity);
	}

	private static boolean isBackpack(ItemStack stack) {
		return stack.getItem() instanceof LeatherBackpack;
	}

	private boolean canTake(int slotId, Slot slot, int button, PlayerEntity playerEntity, ClickType clickType) {
		if (slotId == blocked || slotId <= itemHandler.getSlots() - 1 && isBackpack(playerEntity.inventory.getItemStack())) {
			return false;
		}

		if (clickType == ClickType.SWAP) {
			int hotbarId = itemHandler.getSlots() + 27 + button;
			if (blocked == hotbarId) {
				return false;
			}

			Slot hotbarSlot = getSlot(hotbarId);
			if (slotId <= itemHandler.getSlots() - 1) {
				return !isBackpack(slot.getStack()) && !isBackpack(hotbarSlot.getStack());
			}
		}

		return true;
	}

	@Override

	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	public void copyInventory(IItemHandler handler) {
		this.itemHandler = handler;
	}
}
