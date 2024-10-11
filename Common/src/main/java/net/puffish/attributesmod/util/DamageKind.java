package net.puffish.attributesmod.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;

public class DamageKind {
	private final DamageSource source;

	public DamageKind(DamageSource source) {
		this.source = source;
	}

	public static DamageKind of(DamageSource source) {
		return new DamageKind(source);
	}

	public boolean isMagic() {
		return source.isMagic();
	}

	public boolean isProjectile() {
		return source instanceof ProjectileDamageSource;
	}

	public boolean isMelee() {
		return source instanceof EntityDamageSource && !this.isProjectile();
	}
}
