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
import net.puffish.attributesmod.util.Registrar;

import java.util.ArrayList;
import java.util.List;

@Mod(AttributesMod.MOD_ID)
public class NeoForgeMain {

	private final List<RegistryAlias<?>> registryAliases = new ArrayList<>();

	public NeoForgeMain(IEventBus modEventBus) {
		AttributesMod.setup(new RegistrarImpl(modEventBus));

		modEventBus.addListener(EventPriority.LOW, this::onRegister);
	}

	private void onRegister(RegisterEvent event) {
		registryAliases.forEach(registryAlias -> registryAlias.apply(event.getRegistry()));
	}

	private class RegistrarImpl implements Registrar {
		private final IEventBus modEventBus;

		public RegistrarImpl(IEventBus modEventBus) {
			this.modEventBus = modEventBus;
		}

		@Override
		public <V, T extends V> void register(Registry<V> registry, Identifier id, T entry) {
			var deferredRegister = DeferredRegister.create(registry.getKey(), id.getNamespace());
			deferredRegister.register(id.getPath(), () -> entry);
			deferredRegister.register(modEventBus);
		}

		@Override
		public <V> void registerAlias(Registry<V> registry, Identifier aliasId, Identifier id) {
			registryAliases.add(new RegistryAlias<>(registry, aliasId, id));
		}
	}

	private record RegistryAlias<V>(Registry<V> registry, Identifier aliasId, Identifier id) {
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
