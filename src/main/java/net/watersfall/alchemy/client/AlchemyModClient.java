package net.watersfall.alchemy.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.watersfall.alchemy.AlchemyMod;
import net.watersfall.alchemy.block.AlchemyModBlocks;
import net.watersfall.alchemy.blockentity.AlchemyModBlockEntities;
import net.watersfall.alchemy.client.gui.ApothecaryGuideScreen;
import net.watersfall.alchemy.client.renderer.BrewingCauldronEntityRenderer;
import net.watersfall.alchemy.client.renderer.PedestalEntityRenderer;
import net.watersfall.alchemy.util.StatusEffectHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

@Environment(EnvType.CLIENT)
public class AlchemyModClient implements ClientModInitializer
{
	@Override public void onInitializeClient()
	{
		ColorProviderRegistry.BLOCK.register(
				(state, view, pos, tintIndex) -> BiomeColors.getWaterColor(view, pos),
				AlchemyModBlocks.BREWING_CAULDRON_BLOCK
		);
		BlockEntityRendererRegistry.INSTANCE.register(AlchemyModBlockEntities.BREWING_CAULDRON_ENTITY, BrewingCauldronEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AlchemyModBlockEntities.PEDESTAL_ENTITY, PedestalEntityRenderer::new);
		ScreenRegistry.register(AlchemyMod.APOTHECARY_GUIDE_HANDLER, ApothecaryGuideScreen::new);
		BlockRenderLayerMap.INSTANCE.putBlock(AlchemyModBlocks.CHILD_BLOCK, RenderLayer.getCutout());
		ItemTooltipCallback.EVENT.register(((stack, context, tooltip) -> {
			if(stack.getTag() != null && !stack.getTag().isEmpty())
			{
				CompoundTag tag = stack.getTag();
				if(tag.contains(StatusEffectHelper.EFFECTS_LIST))
				{
					ListTag list = tag.getList(StatusEffectHelper.EFFECTS_LIST, NbtType.COMPOUND);
					if(list.size() > 0)
					{
						tooltip.add(StatusEffectHelper.APPLIED_EFFECTS);
						list.forEach((effect) -> {
							tooltip.add(StatusEffectHelper.getEffectText(StatusEffectHelper.getEffectFromTag((CompoundTag) effect), true));
						});
					}
					else
					{
						tooltip.add(StatusEffectHelper.NO_EFFECT);
					}
				}
			}
		}));
	}
}
