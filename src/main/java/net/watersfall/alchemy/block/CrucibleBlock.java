package net.watersfall.alchemy.block;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.watersfall.alchemy.block.entity.CrucibleEntity;
import net.watersfall.alchemy.recipe.AlchemyRecipes;
import net.watersfall.alchemy.recipe.AspectIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class CrucibleBlock extends AbstractCauldronBlock implements BlockEntityProvider
{
	public CrucibleBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		ActionResult result = super.onUse(state, world, pos, player, hand, hit);
		if(result != ActionResult.PASS)
		{
			return result;
		}
		ItemStack stack = player.getStackInHand(hand);
		CrucibleEntity entity = (CrucibleEntity) world.getBlockEntity(pos);
		if(entity != null)
		{
			if(!stack.isEmpty())
			{
				if(!world.isClient)
				{
					entity.setCurrentInput(stack);
					Optional<AspectIngredient> aspectOptional = world.getRecipeManager().getFirstMatch(AlchemyRecipes.ASPECT_INGREDIENTS, entity, world);
					if(aspectOptional.isPresent())
					{
						AspectIngredient ingredient = aspectOptional.get();
						ingredient.craft(entity);
						entity.markDirty();
						entity.sync();
					}
				}
				return ActionResult.success(world.isClient);
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{

	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new CrucibleEntity(pos, state);
	}
}