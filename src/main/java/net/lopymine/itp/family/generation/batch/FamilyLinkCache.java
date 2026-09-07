package net.lopymine.itp.family.generation.batch;

import java.util.*;
import lombok.*;
import net.lopymine.itp.family.*;
import net.minecraft.world.item.Item;

@Getter
@Setter
public class FamilyLinkCache {

	private final Map<Item, List<FamilyParticleConfig>> resolvedFamilyConfigs = new IdentityHashMap<>();
	private RenderedItemImages images = new RenderedItemImages();

	public void putResolvedFamilyConfigs(Item item, List<FamilyParticleConfig> family) {
		this.resolvedFamilyConfigs.put(item, family);
	}

	public List<FamilyParticleConfig> getResolvedFamilyConfigs(Item item) {
		List<FamilyParticleConfig> family = this.resolvedFamilyConfigs.get(item);
		return family != null ? family : FamilyParticlesManager.getFamilyConfigsForItem(item);
	}

	public void closeAndClear() {
		this.images.closeAndClear();
		this.resolvedFamilyConfigs.clear();
	}
}
