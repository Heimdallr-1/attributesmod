package net.puffish.attributesmod.main;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.mixin.SimpleRegistryAccessor;
import net.puffish.attributesmod.util.Registrar;

public class FabricMain implements ModInitializer {

	@Override
	public void onInitialize() {
		AttributesMod.setup(new RegistrarImpl());
	}

	private record RegistrarImpl() implements Registrar {
		@Override
		public <V, T extends V> void register(Registry<V> registry, Identifier id, T entry) {
			Registry.register(registry, id, entry);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <V> void registerAlias(Registry<V> registry, Identifier aliasId, Identifier id) {
			var accessor = (SimpleRegistryAccessor<V>) registry;
			var entry = accessor.getIdToEntry().get(id);
			accessor.getIdToEntry().put(aliasId, entry);
			accessor.getKeyToEntry().put(RegistryKey.of(registry.getKey(), aliasId), entry);
		}
	}

}
