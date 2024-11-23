package net.puffish.attributesmod.main;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.util.Registrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(AttributesMod.MOD_ID)
public class ForgeMain {

	private final List<RegistryAlias<?>> registryAliases = new ArrayList<>();

	public ForgeMain() {
		AttributesMod.setup(new RegistrarImpl());

		MinecraftForge.EVENT_BUS.addListener(this::onIdMapping);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEntityAttributeCreation);
	}

	private void onIdMapping(RegistryEvent.IdMappingEvent event) {
		registryAliases.forEach(RegistryAlias::apply);
	}

	// This event is used because there is no better event that is fired at this specific time.
	private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		registryAliases.forEach(RegistryAlias::apply);
	}

	private class RegistrarImpl implements Registrar {
		@Override
		public <V, T extends V> void register(Registry<V> registry, Identifier id, T entry) {
			var deferredRegister = DeferredRegister.create(registry.getKey(), id.getNamespace());
			deferredRegister.register(id.getPath(), () -> entry);
			deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		@Override
		public <V> void registerAlias(Registry<V> registry, Identifier aliasId, Identifier id) {
			registryAliases.add(new RegistryAlias<>(registry, aliasId, id));
		}
	}

	private record RegistryAlias<V>(Registry<V> registry, Identifier aliasId, Identifier id) {
		@SuppressWarnings("unchecked")
		public void apply() {
			try {
				var namespacedWrapperClass = Class.forName("net.minecraftforge.registries.NamespacedWrapper");
				var namespacedHolderHelperClass = Class.forName("net.minecraftforge.registries.NamespacedHolderHelper");
				var forgeRegistryClass = ForgeRegistry.class;

				var delegateField = namespacedWrapperClass.getDeclaredField("delegate");
				delegateField.setAccessible(true);

				var holdersField = namespacedWrapperClass.getDeclaredField("holders");
				holdersField.setAccessible(true);

				var holdersByNameField = namespacedHolderHelperClass.getDeclaredField("holdersByName");
				holdersByNameField.setAccessible(true);

				var addAliasMethod = forgeRegistryClass.getDeclaredMethod("addAlias", Identifier.class, Identifier.class);
				addAliasMethod.setAccessible(true);

				var delegate = (ForgeRegistry<?>) delegateField.get(registry);
				var locked = delegate.isLocked();
				if (locked) {
					delegate.unfreeze();
				}
				addAliasMethod.invoke(delegate, aliasId, id);
				if (locked) {
					delegate.freeze();
				}

				var holders = holdersField.get(registry);
				var holdersByName = (Map<Identifier, RegistryEntry.Reference<V>>) holdersByNameField.get(holders);
				holdersByName.put(aliasId, holdersByName.get(id));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
