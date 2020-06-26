package com.bitanalytic.syncmod.items;

import com.bitanalytic.syncmod.container.BackpackContainer;
import com.bitanalytic.syncmod.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.List;

public class LeatherBackpack extends Item {
	private String currentInventory = "inventory";
	private boolean autoPickupEnabled = false;

	public LeatherBackpack() {
		super(new Properties().group(ItemGroup.TOOLS).maxStackSize(1));
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (getAutoPickup(stack)) {
			StringTextComponent component = new StringTextComponent("Auto Pickup Enabled");
			component.setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true));
			tooltip.add(component);
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return getAutoPickupEnabled(stack);
	}

	public boolean getAutoPickupEnabled(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean("autoPickupEnabled");
	}

	public boolean getAutoPickup(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean("autoPickup");
	}

	private void toggleAutoPickupEnabled(PlayerEntity player, Hand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if(!getAutoPickup(stack)) {
			return;
		}
		boolean enabled = getAutoPickupEnabled(stack);
		stack.getOrCreateTag().putBoolean("autoPickupEnabled", !enabled);
		player.setHeldItem(hand, stack);
		this.autoPickupEnabled = !enabled;
	}

	public int getInventorySize(ItemStack stack) {
		int inventorySize = stack.getOrCreateTag().getInt("inventorySize");
		if (inventorySize == 0) {
			inventorySize = 9;
			stack.getOrCreateTag().putInt("inventorySize", inventorySize);
		}
		return inventorySize;
	}

	public IItemHandler getInventory(ItemStack stack) {
		int invSize = getInventorySize(stack);
		ItemStackHandler stackHandler = new ItemStackHandler(invSize);
		ItemStackHandler inventory = new ItemStackHandler(invSize);
		inventory.deserializeNBT(stack.getOrCreateTag().getCompound(currentInventory));
		for (int i = 0; i < inventory.getSlots(); i++) {
			stackHandler.setStackInSlot(i, inventory.getStackInSlot(i));
		}
		return stackHandler;
	}

	public void saveInventory(ItemStack stack, IItemHandler handler) {
		if (handler instanceof ItemStackHandler) {
			stack.getOrCreateTag().put(currentInventory, ((ItemStackHandler) handler).serializeNBT());
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
		if (!world.isRemote) {
			if (KeyboardHelper.isHoldingShift()) {
				toggleAutoPickupEnabled(playerEntity, hand);
			} else {
				playerEntity.openContainer(new SimpleNamedContainerProvider(
						(id, playerInventory, player) -> new BackpackContainer(id, playerInventory),
						new TranslationTextComponent("container.backpack.title")
				));
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, playerEntity.getHeldItem(hand));
	}


}
