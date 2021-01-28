package com.watersfall.alchemy;

import com.watersfall.alchemy.block.AlchemyModBlocks;
import com.watersfall.alchemy.block.BrewingCauldronBlock;
import com.watersfall.alchemy.blockentity.AlchemyModBlockEntities;
import com.watersfall.alchemy.blockentity.BrewingCauldronEntity;
import com.watersfall.alchemy.effect.AlchemyModStatusEffects;
import com.watersfall.alchemy.event.ApplyAffectEvent;
import com.watersfall.alchemy.inventory.handler.ApothecaryGuideHandler;
import com.watersfall.alchemy.item.AlchemyModItems;
import com.watersfall.alchemy.recipe.CauldronIngredients;
import com.watersfall.alchemy.recipe.CauldronIngredientRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AlchemyMod implements ModInitializer
{
	public static final String MOD_ID = "waters_alchemy_mod";
	public static final ScreenHandlerType<ApothecaryGuideHandler> APOTHECARY_GUIDE_HANDLER;
	public static final RecipeType<CauldronIngredients> CAULDRON_INGREDIENTS;
	public static final RecipeType<CauldronIngredientRecipe> CAULDRON_INGREDIENT_RECIPE;
	public static final RecipeSerializer<CauldronIngredients> CAULDRON_INGREDIENTS_SERIALIZER;
	public static final RecipeSerializer<CauldronIngredientRecipe> CAULDRON_INGREDIENT_RECIPE_SERIALIZER;
	public static final Tag<Item> INGREDIENT_TAG;

	static
	{
		APOTHECARY_GUIDE_HANDLER = ScreenHandlerRegistry.registerSimple(getId("apothecary_guide_handler"), ApothecaryGuideHandler::new);
		CAULDRON_INGREDIENTS = Registry.register(Registry.RECIPE_TYPE, getId("cauldron_ingredient"), new RecipeType<CauldronIngredients>() {
			@Override
			public String toString()
			{
				return "cauldron_ingredient";
			}
		});
		CAULDRON_INGREDIENT_RECIPE = Registry.register(Registry.RECIPE_TYPE, getId("cauldron_recipe"), new RecipeType<CauldronIngredientRecipe>() {
			@Override
			public String toString()
			{
				return "cauldron_recipe";
			}
		});
		CAULDRON_INGREDIENTS_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, getId("cauldron_ingredient"), new CauldronIngredients.Serializer(CauldronIngredients::new));
		CAULDRON_INGREDIENT_RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, getId("cauldron_recipe"), new CauldronIngredientRecipe.Serializer(CauldronIngredientRecipe::new));
		INGREDIENT_TAG = TagRegistry.item(getId("ingredients"));
	}

	public static Identifier getId(String id)
	{
		return new Identifier(MOD_ID, id);
	}

	@Override
	public void onInitialize()
	{
		Registry.register(Registry.ITEM, getId("witchy_spoon"), AlchemyModItems.WITCHY_SPOON_ITEM);
		Registry.register(Registry.ITEM, getId("throw_bottle"), AlchemyModItems.THROW_BOTTLE);
		Registry.register(Registry.ITEM, getId("ladle"), AlchemyModItems.LADLE_ITEM);
		Registry.register(Registry.ITEM, getId("apothecary_guide_book"), AlchemyModItems.APOTHECARY_GUIDE);
		Registry.register(Registry.BLOCK, getId("brewing_cauldron"), AlchemyModBlocks.BREWING_CAULDRON_BLOCK);
		Registry.register(Registry.STATUS_EFFECT, getId("projectile_shield"), AlchemyModStatusEffects.PROJECTILE_SHIELD);
		Registry.register(Registry.STATUS_EFFECT, getId("projectile_attraction"), AlchemyModStatusEffects.PROJECTILE_ATTRACTION);
		Registry.register(Registry.STATUS_EFFECT, getId("projectile_weakness"), AlchemyModStatusEffects.PROJECTILE_WEAKNESS);
		Registry.register(Registry.STATUS_EFFECT, getId("projectile_resistance"), AlchemyModStatusEffects.PROJECTILE_RESISTANCE);
		AlchemyModBlockEntities.BREWING_CAULDRON_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, getId("brewing_cauldron_entity"), BlockEntityType.Builder.create(BrewingCauldronEntity::new, AlchemyModBlocks.BREWING_CAULDRON_BLOCK).build(null));
		AttackEntityCallback.EVENT.register(new ApplyAffectEvent());
	}
}
