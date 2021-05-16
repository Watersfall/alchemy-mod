package net.watersfall.alchemy.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public record SpellActionInstance(SpellAction action, int level, int duration)
{
	public TypedActionResult<ItemStack> cast(ItemStack stack, World world, PlayerEntity player)
	{
		return action.use(stack, world, player);
	}
}