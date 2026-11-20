package net.lopymine.itp.client;

import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.family.cache.FamilyParticlesCacheManager;
import net.lopymine.mossylib.announcement.*;

public class ItemParticlesAnnouncements {

	public static void register() {
		AnnouncementGroup cacheRegeneration = new AnnouncementGroup(
				"cache_regeneration",
				ItemParticles.text("announcement.cache_regeneration"),
				ItemParticles.text("announcement.cache_regeneration.option")
		);

		MossyAnnouncements.register(Announcement.builder(ItemParticles.MOD_ID, "1.4.0/cache_regeneration")
				.group(cacheRegeneration)
				.condition(FamilyParticlesCacheManager::hasModdedItemsCache)
				.option(false, (regenerate) -> {
					if (regenerate) {
						FamilyParticlesCacheManager.deleteSilence();
					}
				})
				.unlockDelay(10)
				.build());
	}
}
