package net.watersfall.thuwumcraft.api.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.watersfall.thuwumcraft.api.abilities.entity.PlayerResearchAbility;

import java.util.*;

public class ResearchCategory
{
	public static final Registry REGISTRY = new Registry();

	private Identifier id;
	private Text name;
	private ItemStack icon;
	private int index;
	private List<Identifier> requiredResearch;

	public ResearchCategory(Identifier id, JsonObject json)
	{
		this.id = new Identifier(id.getNamespace(), id.getPath().replace("research_category/", "").replace(".json", "").replace("/", "."));
		this.name = new TranslatableText("research_category." + this.id.getNamespace() + "." + this.id.getPath() + ".name");
		this.icon = new ItemStack(JsonHelper.getItem(json, "icon"), 1);
		this.index = JsonHelper.getInt(json, "index", Integer.MAX_VALUE);
		JsonArray array = JsonHelper.getArray(json, "required_research");
		requiredResearch = new ArrayList<>();
		for(int i = 0; i < array.size(); i++)
		{
			requiredResearch.add(Identifier.tryParse(array.get(i).getAsString()));
		}
	}

	public ResearchCategory(PacketByteBuf buf)
	{
		this.fromPacket(buf);
	}

	public List<Research> getResearch()
	{
		List<Research> list = new ArrayList<>();
		Research.REGISTRY.getAll().forEach((research -> {
			if(research.getCategory() == this)
			{
				list.add(research);
			}
		}));
		return list;
	}

	public Identifier getId()
	{
		return this.id;
	}

	public Text getName()
	{
		return name;
	}

	public ItemStack getIcon()
	{
		return this.icon;
	}

	public int getIndex()
	{
		return this.index;
	}

	public boolean isVisible(PlayerResearchAbility ability)
	{
		for(Identifier id : this.requiredResearch)
		{
			if(!ability.hasResearch(id))
			{
				return false;
			}
		}
		return true;
	}

	public PacketByteBuf toPacket(PacketByteBuf buf)
	{
		buf.writeIdentifier(this.id);
		buf.writeItemStack(this.icon);
		buf.writeInt(this.index);
		buf.writeInt(requiredResearch.size());
		for(int i = 0; i < requiredResearch.size(); i++)
		{
			buf.writeIdentifier(requiredResearch.get(i));
		}
		return buf;
	}

	public void fromPacket(PacketByteBuf buf)
	{
		this.id = buf.readIdentifier();
		this.name = new TranslatableText("research_category." + this.id.getNamespace() + "." + this.id.getPath() + ".name");
		this.icon = buf.readItemStack();
		this.index = buf.readInt();
		int size = buf.readInt();
		this.requiredResearch = new ArrayList<>();
		for(int i = 0; i < size; i++)
		{
			requiredResearch.add(buf.readIdentifier());
		}
	}

	public static class Registry
	{
		private final Map<Identifier, ResearchCategory> categories;

		private Registry()
		{
			this.categories = new HashMap<>();
		}

		public void register(Identifier id, ResearchCategory category)
		{
			this.categories.put(id, category);
		}

		public ResearchCategory get(Identifier id)
		{
			return this.categories.get(id);
		}

		public ResearchCategory getFirst()
		{
			return this.categories.values().stream().findFirst().get();
		}

		public Collection<ResearchCategory> getAll()
		{
			return this.categories.values();
		}

		public PacketByteBuf toPacket(PacketByteBuf buf)
		{
			buf.writeInt(this.categories.size());
			this.categories.values().forEach((category -> {
				category.toPacket(buf);
			}));
			return buf;
		}

		public void fromPacket(PacketByteBuf buf)
		{
			this.categories.clear();
			int size = buf.readInt();
			for(int i = 0; i < size; i++)
			{
				ResearchCategory category = new ResearchCategory(buf);
				this.register(category.getId(), category);
			}
		}
	}
}
