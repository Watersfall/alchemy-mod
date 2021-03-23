package net.watersfall.alchemy.client.gui.element;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.watersfall.alchemy.api.research.ResearchCategory;
import net.watersfall.alchemy.client.gui.ResearchBookScreen;

import java.util.List;

public class CategoryTabElement extends TabElement
{
	private final ResearchCategory category;
	private final ResearchBookScreen screen;

	public CategoryTabElement(ResearchBookScreen screen, ResearchCategory category, int x, int y, boolean inverted)
	{
		super(null, x, y, inverted);
		this.items = new ItemElement(new ItemStack[]{category.getIcon()}, x + 8, y, inverted ? x + 12 : x + 4, y, this);
		this.category = category;
		this.screen = screen;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(isMouseOver(mouseX, mouseY))
		{
			screen.setCurrentCategory(this.category);
		}
		return false;
	}

	@Override
	public List<Text> getTooltip(int mouseX, int mouseY)
	{
		return Lists.newArrayList(this.category.getName());
	}
}