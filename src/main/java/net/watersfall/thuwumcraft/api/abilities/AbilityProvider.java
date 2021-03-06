package net.watersfall.thuwumcraft.api.abilities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface AbilityProvider<T> extends Clearable
{
	public static <T> AbilityProvider<T> getProvider(T t)
	{
		return (AbilityProvider<T>)t;
	}

	public static final Registry<Entity> ENTITY_REGISTRY = new Registry<>();
	public static final Registry<ItemStack> ITEM_REGISTRY = new Registry<>();
	public static final Registry<Chunk> CHUNK_REGISTRY = new Registry<>();

	void addAbility(Ability<T> ability);

	default void removeAbility(Ability<T> ability)
	{
		removeAbility(ability.getId());
	}

	void removeAbility(Identifier id);

	void copy(T to, boolean alive);

	<R> Optional<R> getAbility(Identifier id, Class<R> clazz);

	NbtCompound toNbt(NbtCompound tag);

	void fromNbt(NbtCompound tag);

	PacketByteBuf toPacket(PacketByteBuf buf);

	@Environment(EnvType.CLIENT)
	void fromPacket(PacketByteBuf buf);

	void tick(T t);

	void sync(T t);

	class Registry<T>
	{
		private final HashMap<Identifier, Ability.FactoryTag<T>> registry;
		private final HashMap<Identifier, Ability.FactoryPacket<T>> packetRegistry;

		private Registry()
		{
			registry = new HashMap<>();
			packetRegistry = new HashMap<>();
		}

		public void register(Identifier id, Ability.FactoryTag<T> factory)
		{
			registry.put(id, factory);
		}

		public void registerPacket(Identifier id, Ability.FactoryPacket<T> packet)
		{
			packetRegistry.put(id, packet);
		}

		public Ability<T> create(Identifier id, NbtCompound tag, T t)
		{
			return registry.get(id).create(tag, t);
		}

		public Ability<T> create(Identifier id, PacketByteBuf buf)
		{
			return packetRegistry.get(id).create(buf);
		}

		public Set<Identifier> getIds()
		{
			return registry.keySet();
		}
	}
}
