package net.watersfall.thuwumcraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.watersfall.thuwumcraft.spell.SpellAction;

public class CastingStaffItem extends Item
{
	protected final SpellAction action;
	protected final int castingTime;
	protected final int cooldown;

	public CastingStaffItem(Settings settings, SpellAction action, int castingTime, int cooldown)
	{
		super(settings);
		this.action = action;
		this.castingTime = castingTime;
		this.cooldown = cooldown;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		user.setCurrentHand(hand);
		return TypedActionResult.consume(user.getStackInHand(hand));
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		if(user instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)user;
			action.use(stack, world, player);
			player.getItemCooldownManager().set(this, cooldown);
		}
		return stack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		return this.castingTime;
	}

}
