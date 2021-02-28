package net.watersfall.alchemy.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.watersfall.alchemy.AlchemyMod;
import net.watersfall.alchemy.api.abilities.Ability;
import net.watersfall.alchemy.api.abilities.AbilityClientSerializable;
import net.watersfall.alchemy.api.abilities.AbilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements AbilityProvider<Entity>
{
	@Shadow private int entityId;

	@Shadow public abstract int getId();

	private final HashMap<Identifier, Ability<Entity>> waters_abilities = new HashMap<>();
	private final HashMap<Identifier, Ability<Entity>> temp = new HashMap<>();

	@Override
	public void addAbility(Ability<Entity> ability)
	{
		this.waters_abilities.put(ability.getId(), ability);
	}

	@Override
	public void removeAbility(Identifier id)
	{
		this.waters_abilities.remove(id);
	}

	@Override
	public Optional<Ability<Entity>> getAbility(Identifier id)
	{
		if(this.waters_abilities.containsKey(id))
		{
			return Optional.of(this.waters_abilities.get(id));
		}
		return Optional.empty();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info)
	{
		this.waters_abilities.values().forEach((ability) -> {
			ability.tick((Entity)(Object)this);
		});
	}

	@Override
	public CompoundTag toNbt(CompoundTag tag)
	{
		this.waters_abilities.values().forEach(value -> tag.put(value.getId().toString(), value.toNbt(new CompoundTag())));
		return tag;
	}

	@Override
	public void fromNbt(CompoundTag tag)
	{
		AbilityProvider.ENTITY_REGISTRY.getIds().forEach((id) -> {
			if(tag.contains(id.toString()))
			{
				this.addAbility(AbilityProvider.ENTITY_REGISTRY.create(id, tag.getCompound(id.toString())));
			}
		});
	}

	@Override
	public PacketByteBuf toPacket(PacketByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(this.waters_abilities.size());
		waters_abilities.values().forEach((value) -> {
			if(value instanceof AbilityClientSerializable)
			{
				String id = value.getId().toString();
				buf.writeString(id);
				((AbilityClientSerializable) value).toPacket(buf);
			}
		});
		return buf;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fromPacket(PacketByteBuf buf)
	{
		temp.clear();
		int size = buf.readInt();
		for(int i = 0; i < size; i++)
		{
			Identifier id = Identifier.tryParse(buf.readString());
			this.temp.put(id, AbilityProvider.ENTITY_REGISTRY.create(id, buf));
		}
		MinecraftClient.getInstance().execute(() -> {
			this.waters_abilities.clear();
			this.waters_abilities.putAll(temp);
		});
	}

	@Override
	public void sync(Entity entity)
	{
		PacketByteBuf buf = this.toPacket(PacketByteBufs.create());
		if(entity.getType() == EntityType.PLAYER)
		{
			ServerPlayNetworking.send((ServerPlayerEntity)entity, AlchemyMod.getId("abilities_packet"), buf);
		}
		for(ServerPlayerEntity player : PlayerLookup.tracking(entity))
		{
			ServerPlayNetworking.send(player, AlchemyMod.getId("abilities_packet"), buf);
		}
	}

	@Override
	public void clear()
	{
		this.waters_abilities.clear();
	}
}