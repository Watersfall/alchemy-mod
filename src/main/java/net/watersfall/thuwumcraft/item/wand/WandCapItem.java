package net.watersfall.thuwumcraft.item.wand;

import net.minecraft.item.Item;

public class WandCapItem extends Item
{
	private final WandCapMaterial material;

	public WandCapItem(WandCapMaterial material, Settings settings)
	{
		super(settings);
		this.material = material;
	}

	public WandCapMaterial getMaterial()
	{
		return this.material;
	}
}
