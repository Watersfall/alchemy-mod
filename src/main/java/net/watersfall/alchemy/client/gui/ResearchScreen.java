package net.watersfall.alchemy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.watersfall.alchemy.AlchemyMod;
import net.watersfall.alchemy.api.abilities.AbilityProvider;
import net.watersfall.alchemy.api.abilities.entity.PlayerResearchAbility;
import net.watersfall.alchemy.api.research.Research;
import net.watersfall.alchemy.client.gui.element.RecipeTabElement;
import net.watersfall.alchemy.client.gui.element.ResearchButton;
import net.watersfall.alchemy.client.gui.element.TabElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResearchScreen extends Screen
{
	public static final Identifier BACKGROUND = AlchemyMod.getId("textures/gui/research/research_page.png");

	private final PlayerResearchAbility ability = AbilityProvider.getProvider(MinecraftClient.getInstance().player)
			.getAbility(PlayerResearchAbility.ID, PlayerResearchAbility.class).get();
	private final ResearchBookScreen parent;
	private final Research research;
	private final int textureWidth = 384;
	private final int textureHeight = 272;
	private final int screenWidth = 384;
	private final int screenHeight = 256;
	private int x;
	private int y;
	private final ResearchTab[] tabs;
	private ResearchButton researchButton;

	public ResearchScreen(ResearchBookScreen parent, Research research)
	{
		super(research.getName());
		this.parent = parent;
		this.research = research;
		this.tabs = new ResearchTab[research.getTabs().length];
		for(int i = 0; i < tabs.length; i++)
		{
			this.tabs[i] = new ResearchTab(research.getTabs()[i], this);
		}
	}

	@Override
	protected void init()
	{
		super.init();
		int startX = 378;
		int startY = 20;
		x = (width - screenWidth) / 2;
		y = (height - screenHeight) / 2;
		this.researchButton = new ResearchButton(this.research, this.x + 227, this.y + 217);
		for(int i = 0; i < this.tabs.length; i++)
		{
			this.addChild(new RecipeTabElement(tabs[i], this.x + startX, this.y + startY +  i * 24, true));
		}
		this.addChild(researchButton);
	}

	@Override
	public void renderBackground(MatrixStack matrices)
	{
		super.renderBackground(matrices);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(BACKGROUND);
		drawTexture(matrices, x, y, 0, 0, screenWidth, screenHeight, textureWidth, textureHeight);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		List<OrderedText> text;
		if(!ability.hasResearch(this.research) && this.research.isAvailable(ability))
		{
			this.researchButton.enable();
		}
		else
		{
			this.researchButton.disable();
		}
		if(ability.hasResearch(this.research))
		{
			text = this.textRenderer.wrapLines(this.research.getCompletedDescription(), 160);
		}
		else
		{
			text = this.textRenderer.wrapLines(this.research.getDescription(), 160);
		}
		textRenderer.draw(matrices, this.title, this.x + this.screenWidth / 4F - (textRenderer.getWidth(this.title.asOrderedText()) / 2F), this.y + 24, 4210752);
		int offset = 40;
		for(int i = 0; i < text.size(); i++, offset += 9)
		{
			this.textRenderer.draw(matrices, text.get(i), this.x + 16F, this.y + offset, 4210752);
		}
		for(int i = 0; i < this.children.size(); i++)
		{
			if(this.children.get(i) instanceof Drawable)
			{
				((Drawable)this.children.get(i)).render(matrices, mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public void onClose()
	{
		this.client.openScreen(parent);
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	public Research getResearch()
	{
		return this.research;
	}
}
