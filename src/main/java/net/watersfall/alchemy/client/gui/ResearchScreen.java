package net.watersfall.alchemy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.watersfall.alchemy.AlchemyMod;
import net.watersfall.alchemy.api.abilities.AbilityProvider;
import net.watersfall.alchemy.api.abilities.entity.PlayerResearchAbility;
import net.watersfall.alchemy.api.research.Research;

import java.util.List;
import java.util.Optional;

public class ResearchScreen extends Screen
{
	public static final Identifier BACKGROUND = AlchemyMod.getId("textures/gui/research/research_page.png");
	private static final Identifier ICONS = new Identifier(AlchemyMod.MOD_ID, "textures/gui/research/research_icons.png");

	private final ResearchBookScreen parent;
	private final Research research;
	private final int textureWidth = 384;
	private final int textureHeight = 272;
	private final int screenWidth = 384;
	private final int screenHeight = 256;
	private final int buttonWidth = 112;
	private final int buttonHeight = 16;
	private final ResearchTab[] tabs;

	protected ResearchScreen(ResearchBookScreen parent, Research research)
	{
		super(research.getName());
		this.parent = parent;
		this.research = research;
		this.tabs = new ResearchTab[research.getTabs().length];
		for(int i = 0; i < tabs.length; i++)
		{
			this.tabs[i] = new ResearchTab(research.getTabs()[i], this);
		}
		this.addButton(new ButtonWidget(242, 100, buttonWidth, buttonHeight, new LiteralText("research"), (button) -> {}));
	}

	public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(BACKGROUND);
		int x = (width - textureWidth) / 2;
		int y = (height - textureHeight) / 2;
		matrices.push();
		matrices.translate(0, 0, 0F);
		drawTexture(matrices, x, y, 0, 0, screenWidth, screenHeight, textureWidth, textureHeight);
		matrices.pop();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		drawBackground(matrices, delta, mouseX, mouseY);
		int buttonX = width - (width + screenWidth) / 2 + (screenWidth * 3 / 4) - (buttonWidth / 2);
		int buttonY = height - 48;
		AbilityProvider<Entity> provider = AbilityProvider.getProvider(client.player);
		Optional<PlayerResearchAbility> optional = provider.getAbility(PlayerResearchAbility.ID, PlayerResearchAbility.class);
		PlayerResearchAbility ability = null;
		if(optional.isPresent())
		{
			ability = optional.get();
		}
		if(ability != null)
		{
			if(!ability.hasResearch(this.research) && this.research.isAvailable(ability))
			{
				if(mouseX > buttonX && mouseX < buttonX + buttonWidth && mouseY > buttonY && mouseY < buttonY + buttonHeight)
				{
					drawTexture(matrices, buttonX, buttonY, 112, 256, buttonWidth, buttonHeight, textureWidth, textureHeight);
				}
				else
				{
					drawTexture(matrices, buttonX, buttonY, 0, 256, buttonWidth, buttonHeight, textureWidth, textureHeight);
				}
			}
		}
		this.textRenderer.draw(matrices,
				this.title,
				(width - (screenWidth * 1.5F)) / 2F + (screenWidth / 2F) - (textRenderer.getWidth(this.title) / 2F),
				24,
				4210752);
		List<OrderedText> text;
		if(ability != null && ability.hasResearch(this.research))
		{
			text = this.textRenderer.wrapLines(this.research.getCompletedDescription(), 160);
		}
		else
		{
			text = this.textRenderer.wrapLines(this.research.getDescription(), 160);
		}
		int offset = 40;
		for(int i = 0; i < text.size(); i++, offset += 9)
		{
			this.textRenderer.draw(matrices, text.get(i), (width - screenWidth) / 2F + 16F, offset, 4210752);
		}
		if(ability != null)
		{
			if(!ability.hasResearch(this.research) && this.research.isAvailable(ability))
			{
				DrawableHelper.drawCenteredText(matrices, textRenderer, new LiteralText("Complete Research"), buttonX + buttonWidth / 2, buttonY + buttonHeight / 2 - 4, -1);
			}
		}
		if(this.research.getTabs().length > 0)
		{
			client.getTextureManager().bindTexture(ICONS);
			int startX = 426;
			int startY = 20;
			for(int i = 0; i < this.research.getTabs().length; i++)
			{
				if(!tabs[i].requiresComplete() || ability.hasResearch(research))
				{
					if(mouseX > startX && mouseX < startX + 24 && mouseY > startY + i * 24 && mouseY < startY + i * 24 + 16)
					{
						drawTexture(matrices, startX, startY +  i * 24, 24, 16, 24, 16, 256, 256);
					}
					else
					{
						drawTexture(matrices, startX, startY +  i * 24, 28, 16, 20, 16, 256, 256);
					}
				}

			}
			for(int i = 0; i < this.research.getTabs().length; i++)
			{
				if(!tabs[i].requiresComplete() || ability.hasResearch(research))
				{
					long time = MinecraftClient.getInstance().world.getTime();
					int index = (int)(time / (20F) % this.tabs[i].recipes.length);
					if(mouseX > startX && mouseX < startX + 24 && mouseY > startY + i * 24 && mouseY < startY + i * 24 + 16)
					{
						this.itemRenderer.renderInGui(tabs[i].recipes[index].getOutput(), startX + 4, startY + i * 24);
					}
					else
					{
						this.itemRenderer.renderInGui(tabs[i].recipes[index].getOutput(), startX, startY + i * 24);
					}
				}
			}
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		AbilityProvider<Entity> provider = AbilityProvider.getProvider(client.player);
		provider.getAbility(PlayerResearchAbility.ID, PlayerResearchAbility.class).ifPresent((ability -> {
			if(!ability.hasResearch(research) && this.research.isAvailable(ability))
			{
				int buttonX = width - (width + screenWidth) / 2 + (screenWidth * 3 / 4) - (buttonWidth / 2);
				int buttonY = height - 48;
				if(mouseX > buttonX && mouseX < buttonX + buttonWidth && mouseY > buttonY && mouseY < buttonY + buttonHeight)
				{
					PacketByteBuf buf = PacketByteBufs.create();
					buf.writeIdentifier(research.getId());
					ClientPlayNetworking.send(AlchemyMod.getId("research_click"), buf);
				}
			}
		}));
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		int startX = 426;
		int startY = 20;
		for(int i = 0; i < this.research.getTabs().length; i++)
		{
			if(mouseX > startX && mouseX < startX + 24 && mouseY > startY + i * 24 && mouseY < startY + i * 24 + 16)
			{
				this.client.openScreen(this.tabs[i]);
			}
		}
		return true;
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
}
