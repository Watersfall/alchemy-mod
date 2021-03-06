package net.watersfall.thuwumcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.watersfall.thuwumcraft.Thuwumcraft;
import net.watersfall.thuwumcraft.abilities.entity.PlayerResearchAbilityImpl;
import net.watersfall.thuwumcraft.abilities.entity.PlayerUnknownAbilityImpl;
import net.watersfall.thuwumcraft.api.abilities.Ability;
import net.watersfall.thuwumcraft.api.abilities.AbilityClientSerializable;
import net.watersfall.thuwumcraft.api.abilities.AbilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements AbilityProvider<Entity>
{
	@Shadow private int id;

	@Shadow public abstract int getId();

	private final HashMap<Identifier, Ability<Entity>> thuwumcraft$abilities = new HashMap<>();
	private final HashMap<Identifier, Ability<Entity>> thuwumcraft$temp = new HashMap<>();

	@Override
	public void addAbility(Ability<Entity> ability)
	{
		this.thuwumcraft$abilities.put(ability.getId(), ability);
	}

	@Override
	public void removeAbility(Identifier id)
	{
		this.thuwumcraft$abilities.remove(id);
	}

	@Override
	public <R> Optional<R> getAbility(Identifier id, Class<R> clazz)
	{
		if(this.thuwumcraft$abilities.containsKey(id))
		{
			Ability<Entity> ability = this.thuwumcraft$abilities.get(id);
			if(clazz.isInstance(ability))
			{
				return Optional.of(clazz.cast(ability));
			}

		}
		return Optional.empty();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void thuwumcraft$addToConstructor(EntityType<? extends Entity> type, World world, CallbackInfo info)
	{
		if(type == EntityType.PLAYER)
		{
			if(!world.isClient)
			{
				this.addAbility(new PlayerResearchAbilityImpl());
			}
			this.addAbility(new PlayerUnknownAbilityImpl());
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void thuwumcraft$tick(CallbackInfo info)
	{
		this.thuwumcraft$abilities.values().forEach((ability) -> {
			ability.tick((Entity)(Object)this);
		});
	}

	@Override
	public void copy(Entity to, boolean alive)
	{
		AbilityProvider<Entity> provider = AbilityProvider.getProvider(to);
		this.thuwumcraft$abilities.values().forEach(ability -> {
			if(ability.copyable() || alive)
			{
				provider.addAbility(ability);
			}
		});
	}

	@Override
	public NbtCompound toNbt(NbtCompound tag)
	{
		this.thuwumcraft$abilities.values().forEach(value -> tag.put(value.getId().toString(), value.toNbt(new NbtCompound(), (Entity)(Object)this)));
		return tag;
	}

	@Override
	public void fromNbt(NbtCompound tag)
	{
		AbilityProvider.ENTITY_REGISTRY.getIds().forEach((id) -> {
			if(tag.contains(id.toString()))
			{
				this.addAbility(AbilityProvider.ENTITY_REGISTRY.create(id, tag.getCompound(id.toString()), (Entity)(Object)this));
			}
		});
	}

	@Override
	public PacketByteBuf toPacket(PacketByteBuf buf)
	{
		buf.writeInt(this.id);
		buf.writeInt(this.thuwumcraft$abilities.size());
		thuwumcraft$abilities.values().forEach((value) -> {
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
		thuwumcraft$temp.clear();
		int size = buf.readInt();
		for(int i = 0; i < size; i++)
		{
			Identifier id = Identifier.tryParse(buf.readString());
			this.thuwumcraft$temp.put(id, AbilityProvider.ENTITY_REGISTRY.create(id, buf));
		}
		MinecraftClient.getInstance().execute(() -> {
			this.thuwumcraft$abilities.clear();
			this.thuwumcraft$abilities.putAll(thuwumcraft$temp);
		});
	}

	@Override
	public void sync(Entity entity)
	{
		PacketByteBuf buf = this.toPacket(PacketByteBufs.create());
		if(entity.getType() == EntityType.PLAYER)
		{
			ServerPlayNetworking.send((ServerPlayerEntity)entity, Thuwumcraft.getId("abilities_packet"), buf);
		}
		for(ServerPlayerEntity player : PlayerLookup.tracking(entity))
		{
			ServerPlayNetworking.send(player, Thuwumcraft.getId("abilities_packet"), buf);
		}
	}

	@Override
	public void clear()
	{
		this.thuwumcraft$abilities.clear();
	}



	@Inject(method = "writeNbt", at = @At("RETURN"))
	public void thuwumcraft$writeCustomData(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info)
	{
		this.toNbt(tag);
	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	public void thuwumcraft$readCustomData(NbtCompound tag, CallbackInfo info)
	{
		this.fromNbt(tag);
	}
}
