package net.watersfall.thuwumcraft.abilities.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.watersfall.thuwumcraft.api.abilities.common.AspectStorageAbility;
import net.watersfall.thuwumcraft.api.aspect.Aspect;
import net.watersfall.thuwumcraft.api.aspect.AspectStack;
import net.watersfall.thuwumcraft.api.aspect.Aspects;

import java.util.ArrayList;
import java.util.List;

public class PhialStorageAbility implements AspectStorageAbility<ItemStack>
{
	private NbtCompound tag;

	public PhialStorageAbility(AspectStack stack)
	{
		tag = new NbtCompound();
		tag.putInt(stack.getAspect().getId().toString(), stack.getCount());
	}

	public PhialStorageAbility(Aspect aspect)
	{
		this(new AspectStack(aspect, 64));
	}

	public PhialStorageAbility(Aspect aspect, int count)
	{
		this(new AspectStack(aspect, count));
	}

	public PhialStorageAbility(NbtCompound tag, ItemStack stack)
	{
		this.tag = tag;
	}

	@Override
	public NbtCompound getTag()
	{
		return this.tag;
	}

	@Override
	public int getSize()
	{
		return this.tag.getSize();
	}

	@Override
	public AspectStack getAspect(Aspect aspect)
	{
		if(this.tag.contains(aspect.getId().toString()))
		{
			return new AspectStack(aspect, this.tag.getInt(aspect.getId().toString()));
		}
		return AspectStack.EMPTY;
	}

	@Override
	public AspectStack removeAspect(Aspect aspect, int count)
	{
		AspectStack stack = this.getAspect(aspect);
		if(count > stack.getCount())
		{
			count = stack.getCount();
		}
		stack.decrement(count);
		this.setAspect(stack);
		return new AspectStack(aspect, count);
	}


	@Override
	public void setAspect(AspectStack stack)
	{
		this.tag.putInt(stack.getAspect().getId().toString(), stack.getCount());
	}

	@Override
	public void addAspect(AspectStack stack)
	{
		if(this.tag.contains(stack.getAspect().getId().toString()))
		{
			AspectStack stack1 = this.getAspect(stack.getAspect());
			stack.increment(stack1.getCount());
		}
		this.setAspect(stack);
	}

	@Override
	public List<AspectStack> getAspects()
	{
		List<AspectStack> list = new ArrayList<>();
		tag.getKeys().forEach((key) -> {
			list.add(this.getAspect(Aspects.getAspectById(Identifier.tryParse(key))));
		});
		return list;
	}

	@Override
	public NbtCompound toNbt(NbtCompound tag, ItemStack t)
	{
		return this.tag;
	}

	@Override
	public void fromNbt(NbtCompound tag, ItemStack t)
	{
		this.tag = tag;
	}
}
