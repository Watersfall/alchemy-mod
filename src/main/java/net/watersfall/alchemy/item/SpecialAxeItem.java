package net.watersfall.alchemy.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpecialAxeItem extends AxeItem
{
	protected SpecialAxeItem()
	{
		super(AlchemyToolMaterials.MAGIC, 5.0F, -3.0F, (new Item.Settings()).group(AlchemyItems.ALCHEMY_MOD_ITEM_GROUP).fireproof());
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player)
	{
		if(stack.getTag() != null && stack.getTag().contains("RepairCost"))
		{
			stack.getTag().putInt("RepairCost", 0);
		}
	}
}
