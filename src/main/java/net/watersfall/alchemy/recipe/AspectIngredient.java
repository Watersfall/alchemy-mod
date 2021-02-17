package net.watersfall.alchemy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.watersfall.alchemy.api.aspect.Aspect;
import net.watersfall.alchemy.api.aspect.AspectInventory;
import net.watersfall.alchemy.api.aspect.AspectStack;

import java.util.ArrayList;
import java.util.List;

public class AspectIngredient implements Recipe<AspectInventory>
{
	private final Identifier id;
	private final Item input;
	private final List<AspectStack> aspects;

	public AspectIngredient(Identifier id, Item input, List<AspectStack> aspects)
	{
		this.id = id;
		this.input = input;
		this.aspects = aspects;
	}

	@Override
	public boolean matches(AspectInventory inv, World world)
	{
		return inv.getCurrentInput().getItem() == input;
	}

	@Override
	public ItemStack craft(AspectInventory inv)
	{
		inv.setCurrentInput(ItemStack.EMPTY);
		for(int i = 0; i < aspects.size(); i++)
		{
			inv.addAspect(aspects.get(i).copy());
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height)
	{
		return true;
	}

	@Override
	public ItemStack getOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isIgnoredInRecipeBook()
	{
		return true;
	}

	@Override
	public Identifier getId()
	{
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return AlchemyRecipes.ASPECT_INGREDIENT_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType()
	{
		return AlchemyRecipes.ASPECT_INGREDIENTS;
	}

	public List<AspectStack> getAspects()
	{
		return this.aspects;
	}

	public static class Serializer implements RecipeSerializer<AspectIngredient>
	{
		private final AspectIngredient.Serializer.RecipeFactory<AspectIngredient> recipeFactory;

		public Serializer(AspectIngredient.Serializer.RecipeFactory<AspectIngredient> recipeFactory)
		{
			this.recipeFactory = recipeFactory;
		}

		@Override
		public AspectIngredient read(Identifier id, JsonObject json)
		{
			Item item = Registry.ITEM.get(Identifier.tryParse(json.get("item").getAsString()));
			JsonArray array = json.getAsJsonArray("aspects");
			List<AspectStack> aspects = new ArrayList<>(array.size());
			for(int i = 0; i < array.size(); i++)
			{
				JsonObject object = array.get(i).getAsJsonObject();
				Aspect aspect = Aspect.ASPECTS.get(Identifier.tryParse(object.get("aspect").getAsString()));
				int amount = object.get("count").getAsInt();
				aspects.add(new AspectStack(aspect, amount));
			}
			return new AspectIngredient(id, item, aspects);
		}

		@Override
		public AspectIngredient read(Identifier id, PacketByteBuf buf)
		{
			return null;
		}

		@Override
		public void write(PacketByteBuf buf, AspectIngredient recipe)
		{

		}

		public interface RecipeFactory<T extends Recipe<?>>
		{
			T create(Identifier id, Item input, List<AspectStack> aspects);
		}
	}
}
