package net.watersfall.thuwumcraft.block;

import net.minecraft.block.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.watersfall.thuwumcraft.block.entity.ChildBlockEntity;
import net.watersfall.thuwumcraft.api.multiblock.component.ItemComponent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ChildBlock extends Block implements BlockEntityProvider, InventoryProvider
{
	public ChildBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		BlockEntity entityTest = world.getBlockEntity(pos);
		if(entityTest instanceof ChildBlockEntity entity)
		{
			if(entity.getComponent() != null)
			{
				if(!world.isClient)
				{
					entity.getComponent().getMultiBlock().onUse(world, pos, player);
				}
			}
		}
		return ActionResult.success(world.isClient);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		BlockEntity entityTest = world.getBlockEntity(pos);
		if(entityTest instanceof ChildBlockEntity entity)
		{
			if(entity.getComponent() != null)
			{
				if(!world.isClient())
				{
					entity.getComponent().onBreak();
				}
			}
		}
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos)
	{
		BlockEntity entityTest = world.getBlockEntity(pos);
		if(entityTest instanceof ChildBlockEntity entity)
		{
			if(entity.getComponent() instanceof ItemComponent component)
			{
				return component.getInventory();
			}
		}
		return null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		BlockEntity entityTest = world.getBlockEntity(pos);
		if(entityTest instanceof ChildBlockEntity entity)
		{
			if(entity.getComponent() != null)
			{
				return entity.getComponent().getOutline();
			}
		}
		return super.getOutlineShape(state, world, pos, context);
	}

	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return 1.0F;
	}

	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
	{
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new ChildBlockEntity(pos, state);
	}
}
