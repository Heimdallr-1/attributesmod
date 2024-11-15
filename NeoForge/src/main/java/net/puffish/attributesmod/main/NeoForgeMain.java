package net.puffish.attributesmod.main;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.mixin.SimpleRegistryAccessor;

import java.util.ArrayList;
import java.util.List;

@Mod(AttributesMod.MOD_ID)
public class NeoForgeMain {

	public static final List<DeferredRegister<?>> DEFERRED_REGISTERS = new ArrayList<>();
	public static final List<RegistryAlias<?>> REGISTRY_ALIASES = new ArrayList<>();

	public NeoForgeMain(IEventBus modEventBus) {
		AttributesMod.setup();
		modEventBus.addListener(EventPriority.LOW, this::onRegister);
		for (var deferredRegister : DEFERRED_REGISTERS) {
			deferredRegister.register(modEventBus);
		}
	}

	private void onRegister(RegisterEvent event) {
		REGISTRY_ALIASES.forEach(registryAlias -> registryAlias.apply(event.getRegistry()));
	}

	public record RegistryAlias<V>(Registry<V> registry, Identifier aliasId, Identifier id) {
		@SuppressWarnings("unchecked")
		public void apply(Registry<?> registry) {
			if (registry == this.registry) {
				var accessor = (SimpleRegistryAccessor<V>) this.registry;
				var entry = accessor.getIdToEntry().get(id);
				accessor.getIdToEntry().put(aliasId, entry);
				accessor.getKeyToEntry().put(RegistryKey.of(this.registry.getKey(), aliasId), entry);
			}
		}
	}
}
