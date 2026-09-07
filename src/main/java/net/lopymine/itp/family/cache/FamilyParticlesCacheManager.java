package net.lopymine.itp.family.cache;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.mossylib.loader.MossyLoader;

@ExtensionMethod(NativeImageExtension.class)
public class FamilyParticlesCacheManager {

	public static final Path FOLDER = MossyLoader.getConfigDir()
			.resolve(ItemParticles.MOD_ID.replace("_", "-"))
			.resolve("cache");

	public static void deleteSilence() {
		try {
			delete();
		} catch (IOException e) {
			ItemParticlesClient.LOGGER.error("Failed to delete cache folder:", e);
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