package net.watersfall.alchemy.recipe;

import com.google.gson.JsonObject;
import net.watersfall.alchemy.inventory.BrewingCauldronInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Locale;

public class CauldronIngredientRecipe implements Recipe<BrewingCauldronInventory>
{
	public static final String ITEMS = "items";
	public static final String ACTION = "action";
	public static final String WATER_USE = "water_use";
	public static final String USES = "uses";
	public static final String OUTPUT = "output";

	private final Identifier id;
	private final Ingredient input;
	private final int waterUse;
	private final int uses;
	private final CraftingAction craftingAction;
	private final ItemStack output;

	public CauldronIngredientRecipe(Identifier id, Ingredient input, int waterUse, CraftingAction action, int uses, ItemStack output)
	{
		this.id = id;
		this.input = input;
		this.waterUse = waterUse;
		this.craftingAction = action;
		this.uses = uses;
		this.output = output;
	}

	@Override
	public boolean matches(BrewingCauldronInventory inv, World world)
	{
		return getInput().test(inv.getInput().get(0));
	}

	@Override
	public ItemStack craft(BrewingCauldronInventory inv)
	{
		return this.getOutput();
	}

	@Override
	public boolean fits(int width, int height)
	{
		return true;
	}

	@Override
	public ItemStack getOutput()
	{
		return this.output;
	}

	@Override
	public Identifier getId()
	{
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return AlchemyModRecipes.CAULDRON_INGREDIENT_RECIPE_SERIALIZER;
	}

	@Override
	public boolean isIgnoredInRecipeBook()
	{
		return true;
	}

	@Override
	public RecipeType<?> getType()
	{
		return AlchemyModRecipes.CAULDRON_INGREDIENT_RECIPE;
	}

	public Ingredient getInput()
	{
		return input;
	}

	public int getWaterUse()
	{
		return waterUse;
	}

	public int getUses()
	{
		return uses;
	}

	public CraftingAction getCraftingAction()
	{
		return craftingAction;
	}

	public static class Serializer implements RecipeSerializer<CauldronIngredientRecipe>
	{
		private final CauldronIngredientRecipe.Serializer.RecipeFactory<CauldronIngredientRecipe> recipeFactory;
		public Serializer(CauldronIngredientRecipe.Serializer.RecipeFactory<CauldronIngredientRecipe> recipeFactory)
		{
			this.recipeFactory = recipeFactory;
		}

		@Override
		public CauldronIngredientRecipe read(Identifier id, JsonObject json)
		{
			Ingredient input = Ingredient.fromJson(json.get(ITEMS).getAsJsonArray());
			CraftingAction action = CraftingAction.valueOf(json.get(ACTION).getAsString().toUpperCase(Locale.ROOT));
			int waterUse = json.get(WATER_USE).getAsInt();
			int uses = 0;
			ItemStack output = ItemStack.EMPTY;
			if(json.get(USES) != null)
			{
				uses = json.get(USES).getAsInt();
			}
			if(json.get(OUTPUT) != null)
			{
				output = new ItemStack(Registry.ITEM.get(Identifier.tryParse(json.get(OUTPUT).getAsString())));
			}
			return new CauldronIngredientRecipe(id, input, waterUse, action, uses, output);
		}

		@Override
		public CauldronIngredientRecipe read(Identifier id, PacketByteBuf buf)
		{
			int waterUse = buf.readInt();
			CraftingAction action = buf.readEnumConstant(CraftingAction.class);
			int uses = buf.readInt();
			Ingredient input = Ingredient.fromPacket(buf);
			ItemStack output = buf.readItemStack();
			return new CauldronIngredientRecipe(id, input, waterUse, action, uses, output);
		}

		@Override
		public void write(PacketByteBuf buf, CauldronIngredientRecipe recipe)
		{
			buf.writeInt(recipe.getWaterUse());
			buf.writeEnumConstant(recipe.getCraftingAction());
			buf.writeInt(recipe.getUses());
			recipe.getInput().write(buf);
			buf.writeItemStack(recipe.getOutput());
		}

		public interface RecipeFactory<T extends Recipe<?>>
		{
			T create(Identifier id, Ingredient input, int waterUse, CraftingAction action, int uses, ItemStack output);
		}
	}

	public enum CraftingAction
	{
		ADD_EFFECTS,
		CREATE_ITEM_EFFECT,
		CREATE_ITEM_NO_EFFECT,
		CREATE_POTION
	}
}