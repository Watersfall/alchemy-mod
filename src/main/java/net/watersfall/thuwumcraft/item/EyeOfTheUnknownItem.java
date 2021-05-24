package net.watersfall.thuwumcraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.watersfall.thuwumcraft.Thuwumcraft;

public class EyeOfTheUnknownItem extends Item
{
	public EyeOfTheUnknownItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
		{
			Text text =  getClientName(stack);
			if(text != null)
			{
				return text;
			}
		}
		return new TranslatableText("item.thuwumcraft.eye_of_the_known");
	}

	public Text getClientName(ItemStack stack)
	{
		if(MinecraftClient.getInstance().player != null)
		{
			if(MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler().getManager().get(Thuwumcraft.getId("into_the_unknown")) == null)
			{
				return new TranslatableText("item.thuwumcraft.eye_of_the_unknown");
			}
		}
		return null;
	}
}
