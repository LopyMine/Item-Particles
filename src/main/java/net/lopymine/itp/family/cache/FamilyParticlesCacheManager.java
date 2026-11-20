package net.lopymine.itp.family.cache;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.element.texture.provider.ITextureProvider;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.itp.family.atlas.manager.FamilyParticlesAtlasManager;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.mossylib.loader.MossyLoader;
import net.minecraft.util.Util;

@ExtensionMethod(NativeImageExtension.class)
public class FamilyParticlesCacheManager {

	public static final Path FOLDER = MossyLoader.getConfigDir()
			.resolve(ItemParticles.MOD_ID.replace("_", "-"))
			.resolve("cache");

	private static boolean clearLoadedOnNextLinking;

	public static boolean hasModdedItemsCache() {
		if (!Files.isDirectory(FOLDER)) {
			return false;
		}
		try (Stream<Path> stream = Files.list(FOLDER)) {
			return stream.filter(Files::isDirectory)
					.filter((path) -> !path.getFileName().toString().equals("minecraft"))
					.anyMatch((path) -> Files.isRegularFile(path.resolve("regions.txt")));
		} catch (IOException e) {
			ItemParticlesClient.LOGGER.error("Failed to check cache folder:", e);
			return false;
		}
	}

	public static void deleteSilence() {
		try {
			delete();
		} catch (IOException e) {
			ItemParticlesClient.LOGGER.error("Failed to delete cache folder:", e);
		}
	}

	public static CompletableFuture<Boolean> deleteAsync() {
		return CompletableFuture.supplyAsync(() -> {
			deleteSilence();
			return !Files.exists(FOLDER);
		}, Util.ioPool());
	}

	public static void clearLoaded() {
		ITextureProvider.clear();
		ModelTexelScanner.clearTextureCache();
		FamilyParticlesAtlasManager.closeAll();
		FamilyParticlesAtlasCacheManager.clear();
		FamilyParticlesSpawnAreasCacheManager.clear();
	}

	public static void clearLoadedOnNextLinking() {
		clearLoadedOnNextLinking = true;
	}

	public static void clearLoadedIfRequested() {
		if (clearLoadedOnNextLinking) {
			clearLoadedOnNextLinking = false;
			clearLoaded();
		}
	}

	public static void delete() throws IOException {
		if (!Files.exists(FOLDER)) {
			return;
		}
		try (Stream<Path> stream = Files.walk(FOLDER).sorted(Comparator.reverseOrder())) {
			stream.forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					ItemParticlesClient.LOGGER.error("Failed to delete specific cache path {}:", path, e);
				}
			});
		} catch (IOException e) {
			ItemParticlesClient.LOGGER.error("Failed to delete cache folder:", e);
			throw e;
		}
	}
}