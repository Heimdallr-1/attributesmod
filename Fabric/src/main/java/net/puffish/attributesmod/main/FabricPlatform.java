package net.puffish.attributesmod.main;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.puffish.attributesmod.mixin.SimpleRegistryAccessor;
import net.puffish.attributesmod.util.Platform;

public class FabricPlatform implements Platform {

	@Override
	public <T> RegistryEntry.Reference<T> registerReference(Registry<T> registry, Identifier id, T entry) {
		return Registry.registerReference(registry, id, entry);
	}

	@Override
	public <T> void registerAlias(Registry<T> registry, Identifier aliasId, Identifier id) {
		var accessor = (SimpleRegistryAccessor<T>) registry;
		var entry = accessor.getIdToEntry().get(id);
		accessor.getIdToEntry().put(aliasId, entry);
		accessor.getKeyToEntry().put(RegistryKey.of(registry.getKey(), aliasId), entry);
	}

}
