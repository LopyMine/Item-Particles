package net.lopymine.itp.yacl.screen;

import java.util.concurrent.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.family.cache.FamilyParticlesCacheManager;
import net.lopymine.mossylib.gui.screen.MossyChoiceScreen;
import net.lopymine.mossylib.toast.MossyToasts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class CacheInvalidationScreen {

	public static Screen create(@Nullable Screen parent) {
		MossyChoiceScreen.Choice back = MossyChoiceScreen.choice(ItemParticles.text("cache_invalidation.back"), () -> {});
		if (Minecraft.getInstance().level != null) {
			return MossyChoiceScreen.builder(parent, ItemParticles.text("cache_invalidation.title"), ItemParticles.text("cache_invalidation.in_world"))
					.row(
							MossyChoiceScreen.choice(ItemParticles.text("cache_invalidation.delete_and_regenerate"), () -> invalidate(true)),
							MossyChoiceScreen.choice(ItemParticles.text("cache_invalidation.delete_only"), () -> invalidate(false))
					)
					.row(back)
					.build();
		}
		return MossyChoiceScreen.builder(parent, ItemParticles.text("cache_invalidation.title"), ItemParticles.text("cache_invalidation.not_in_world"))
				.row(MossyChoiceScreen.choice(ItemParticles.text("cache_invalidation.delete"), () -> invalidate(false)))
				.row(back)
				.build();
	}

	private static void invalidate(boolean regenerateNow) {
		MossyToasts.show(
				ItemParticles.text("toast.cache.deleting.title"),
				ItemParticles.text("toast.cache.deleting.message")
		);
		FamilyParticlesCacheManager.deleteAsync().thenAcceptAsync((deleted) -> onDeleted(deleted, regenerateNow), Minecraft.getInstance());
	}

	private static void onDeleted(boolean deleted, boolean regenerateNow) {
		if (!deleted) {
			MossyToasts.show(
					ItemParticles.text("toast.cache.failed.title"),
					ItemParticles.text("toast.cache.failed.message")
			);
			return;
		}

		MossyToasts.show(
				ItemParticles.text("toast.cache.deleted.title"),
				ItemParticles.text("toast.cache.deleted.message")
		);

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null && regenerateNow) {
			long reloadDelay = 2L;
			CompletableFuture.delayedExecutor(reloadDelay, TimeUnit.SECONDS, minecraft).execute(minecraft::reloadResourcePacks);
			return;
		}

		if (minecraft.level == null) {
			FamilyParticlesCacheManager.clearLoaded();
		} else {
			FamilyParticlesCacheManager.clearLoadedOnNextLinking();
		}
		MossyToasts.show(
				ItemParticles.text("toast.cache.regenerate_on_join.title"),
				ItemParticles.text("toast.cache.regenerate_on_join.message")
		);
	}
}
