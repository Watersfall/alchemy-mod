package net.watersfall.alchemy.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;

public class AlchemyBlocks
{
	public static final BrewingCauldronBlock BREWING_CAULDRON_BLOCK;
	public static final PedestalBlock PEDESTAL_BLOCK;
	public static final AlchemicalFurnaceBlock ALCHEMICAL_FURNACE_BLOCK;
	public static final ChildBlock CHILD_BLOCK;

	static
	{
		BREWING_CAULDRON_BLOCK = new BrewingCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON).ticksRandomly());
		PEDESTAL_BLOCK = new PedestalBlock(FabricBlockSettings.copyOf(Blocks.STONE).luminance(7).nonOpaque());
		ALCHEMICAL_FURNACE_BLOCK = new AlchemicalFurnaceBlock(FabricBlockSettings.copyOf(Blocks.STONE));
		CHILD_BLOCK = new ChildBlock(FabricBlockSettings.copyOf(Blocks.GLASS));
	}
}