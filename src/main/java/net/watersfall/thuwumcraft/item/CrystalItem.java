package net.watersfall.thuwumcraft.item;

import com.google.common.collect.Lists;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.watersfall.thuwumcraft.api.aspect.Aspect;
import net.watersfall.thuwumcraft.api.aspect.AspectStack;
import net.watersfall.thuwumcraft.client.item.AspectTooltipData;

import java.util.Optional;

public class CrystalItem extends Item
{
	private final Aspect aspect;

	public CrystalItem(Settings settings, Aspect aspect)
	{
		super(settings);
		this.aspect = aspect;
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack)
	{
		return Optional.of(new AspectTooltipData(Lists.newArrayList(new AspectStack(this.aspect, 1))));
	}

	@Override
	public String getTranslationKey()
	{
		return "item.thuwumcraft.crystal";
	}

	public Aspect getAspect()
	{
		return this.aspect;
	}
}
