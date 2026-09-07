package net.lopymine.itp.family.cache;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.element.spawner.AdvancedSpawnPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class FamilyParticlesSpawnAreasCacheManager {

	private static final int MAGIC = 0x49505058; // "IPPX"
	private static final int VERSION = 3;

	private static final Map<String, Map<String, List<AdvancedSpawnPos>>> NAMESPACE_PIXELS = new ConcurrentHashMap<>();
	private static final Map<String, Object> NAMESPACE_WRITE_LOCKS = new ConcurrentHashMap<>();

	private static Object lock(String namespace) {
		return NAMESPACE_WRITE_LOCKS.computeIfAbsent(namespace, ignoredNamespace -> new Object());
	}

	public static void clear() {
		NAMESPACE_PIXELS.clear();
	}

	@Nullable
	public static List<AdvancedSpawnPos> load(Identifier itemId) {
		Map<String, List<AdvancedSpawnPos>> map = getOrLoadNamespacePixels(itemId.getNamespace());
		if (map == null) {
			return null;
		}
		return map.get(itemId.getPath());
	}

	@Nullable
	private static Map<String, List<AdvancedSpawnPos>> getOrLoadNamespacePixels(String namespace) {
		Map<String, List<AdvancedSpawnPos>> pixels = NAMESPACE_PIXELS.get(namespace);
		if (pixels == null) {
			synchronized (lock(namespace)) {
				pixels = NAMESPACE_PIXELS.get(namespace);

				if (pixels == null) {
					Path file = FamilyParticlesCacheManager.FOLDER.resolve(namespace).resolve("spawn_areas.cached");

					try {
						pixels = readNamespacePixels(file);
					} catch (IOException exception) {
						ItemParticlesClient.LOGGER.error(
								"Failed to load cached pixels for namespace {}, reason:",
								namespace,
								exception
						);

						return null;
					}

					NAMESPACE_PIXELS.put(namespace, pixels);
				}
			}
		}
		return pixels;
	}

	private static Map<String, List<AdvancedSpawnPos>> readNamespacePixels(Path file) throws IOException {
		Map<String, List<AdvancedSpawnPos>> namespacePixels = new HashMap<>();

		if (!Files.isRegularFile(file)) {
			return namespacePixels;
		}

		try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
			int magic = inputStream.readInt();
			if (magic != MAGIC) {
				return namespacePixels;
			}

			int version = inputStream.readInt();
			if (version != VERSION) {
				return namespacePixels;
			}

			int entriesCount = inputStream.readInt();
			if (entriesCount < 0) {
				return namespacePixels;
			}

			for (int entryIndex = 0; entryIndex < entriesCount; entryIndex++) {
				String itemPath = inputStream.readUTF();
				int pixelsCount = inputStream.readInt();
				if (pixelsCount < 0) {
					return namespacePixels;
				}

				List<AdvancedSpawnPos> pixels = new ArrayList<>(pixelsCount);

				for (int pixelIndex = 0; pixelIndex < pixelsCount; pixelIndex++) {
					String rawTexture = inputStream.readUTF();
					int x = inputStream.readInt();
					int y = inputStream.readInt();

					Identifier texture = Identifier.tryParse(rawTexture);
					if (texture == null) {
						ItemParticlesClient.LOGGER.error(
								"Corrupted spawn areas cache, file {}, unable to parse texture id \"{}\", dropping cache",
								file,
								rawTexture
						);
						return new HashMap<>();
					}

					pixels.add(new AdvancedSpawnPos(texture, x, y));
				}

				namespacePixels.put(itemPath, pixels);
			}
		}

		return namespacePixels;
	}

	public static void add(Identifier itemId, List<AdvancedSpawnPos> pixels) {
		String namespace = itemId.getNamespace();

		synchronized (lock(namespace)) {
			Map<String, List<AdvancedSpawnPos>> map = NAMESPACE_PIXELS.computeIfAbsent(
					namespace,
					ignoredNamespace -> new HashMap<>()
			);

			map.put(itemId.getPath(), pixels);
		}
	}

	public static void save(String namespace) {
		Map<String, List<AdvancedSpawnPos>> map = NAMESPACE_PIXELS.get(namespace);
		if (map == null || map.isEmpty()) {
			return;
		}

		Map<String, List<AdvancedSpawnPos>> snapshot;

		synchronized (lock(namespace)) {
			map = NAMESPACE_PIXELS.get(namespace);
			if (map == null || map.isEmpty()) {
				return;
			}

			snapshot = new HashMap<>(map);
		}

		Path file = FamilyParticlesCacheManager.FOLDER.resolve(namespace).resolve("spawn_areas.cached");
		Path temporaryFile = file.resolveSibling(file.getFileName() + ".tmp");

		Util.ioPool().execute(() -> {
			synchronized (lock(namespace)) {
				try {
					Files.createDirectories(file.getParent());

					try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temporaryFile)))) {
						out.writeInt(MAGIC);
						out.writeInt(VERSION);
						out.writeInt(snapshot.size());

						for (Map.Entry<String, List<AdvancedSpawnPos>> entry : snapshot.entrySet()) {
							out.writeUTF(entry.getKey());
							writePixels(out, entry.getValue());
						}
					}

					Files.move(
							temporaryFile,
							file,
							StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.ATOMIC_MOVE
					);
				} catch (IOException exception) {
					ItemParticlesClient.LOGGER.error(
							"Failed to cache pixels for namespace {}, reason:",
							namespace,
							exception
					);

					try {
						Files.deleteIfExists(temporaryFile);
					} catch (IOException ignoredException) {
					}
				}
			}
		});
	}

	private static void writePixels(DataOutputStream out, List<AdvancedSpawnPos> pixels) throws IOException {
		out.writeInt(pixels.size());

		for (AdvancedSpawnPos pixel : pixels) {
			Identifier string = pixel.texture();
			out.writeUTF(string == null ? "none" : string.toString());
			out.writeInt(pixel.x());
			out.writeInt(pixel.y());
		}
	}
}