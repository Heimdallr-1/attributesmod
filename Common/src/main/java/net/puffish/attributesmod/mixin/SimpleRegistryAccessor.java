package net.puffish.attributesmod.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor<T> {
	@Accessor
	Map<Identifier, RegistryEntry.Reference<T>> getIdToEntry();

	@Accessor
	Map<RegistryKey<T>, RegistryEntry.Reference<T>> getKeyToEntry();
}
