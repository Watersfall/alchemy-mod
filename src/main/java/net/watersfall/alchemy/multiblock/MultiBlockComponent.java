package net.watersfall.alchemy.multiblock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public interface MultiBlockComponent
{
	default void onBreak()
	{
		getMultiBlock().markInvalid();
	}

	void onUse(PlayerEntity player);

	void tick();

	BlockPos getPos();

	World getWorld();

	MultiBlock<? extends MultiBlockComponent> getMultiBlock();

	VoxelShape getOutline();
}
