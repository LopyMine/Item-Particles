package net.lopymine.itp.element.spawner;

import com.mojang.serialization.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import net.lopymine.itp.client.ItemParticlesClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public record AdvancedSpawnAreas(Map<ResourceLocation, AdvancedSpawnAreaId> areas) implements IParticleSpawnArea {

	public static final AdvancedSpawnAreas EMPTY = new AdvancedSpawnAreas(new HashMap<>());
	public static final AdvancedSpawnAreas FULL = new AdvancedSpawnAreas(new HashMap<>());

	public static final Codec<AdvancedSpawnAreas> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.STRING).comapFlatMap((map) -> {
		Map<ResourceLocation, AdvancedSpawnAreaId> areas = new HashMap<>();

		for (Entry<ResourceLocation, String> entry : map.entrySet()) {
			DataResult<AdvancedSpawnAreaId> result = AdvancedSpawnAreaId.read(entry.getKey(), entry.getValue());
			if (result.isError()) {
				return DataResult.error(() -> result.error().orElseThrow().message());
			}
			areas.put(AdvancedSpawnAreaId.getValidatedBaseTexture(entry.getKey()), result.getOrThrow());
		}

		return DataResult.success(new AdvancedSpawnAreas(areas));
	}, (areas) -> new HashMap<>());

	public boolean isEmpty() {
		return this.areas.isEmpty();
	}

	public Function<ResourceLocation, @Nullable IParticleSpawnPos> getRandomPosFunction(RandomSource random) {
		return (id) -> {
			AdvancedSpawnAreaId spawnAreaId = this.areas.get(id);
			if (spawnAreaId == null) {
				if (ItemParticlesClient.VALIDATION_ENABLED) {
					ItemParticlesClient.LOGGER.error("No spawn area for {}, requested: {}, available: {} ", ItemParticlesClient.CURRENT_STACK, id, new ArrayList<>(this.areas.keySet()));
				}
				return null;
			}
			AdvancedSpawnArea area = spawnAreaId.getArea();
			if (area == null) {
				if (ItemParticlesClient.VALIDATION_ENABLED) {
					ItemParticlesClient.LOGGER.error("Failed to get spawn area from [{}|{}] for {}, requested: {}",  spawnAreaId.getBaseTexture(), spawnAreaId.getMaskTexture(), ItemParticlesClient.CURRENT_STACK, id);
				}
				return null;
			}
			IParticleSpawnPos pos = area.getRandomPosFunction(random).apply(id);
			if (pos == null && ItemParticlesClient.VALIDATION_ENABLED) {
				ItemParticlesClient.LOGGER.error("Failed to get pos from spawn area from [{}|{}] for {}, requested: {}",  spawnAreaId.getBaseTexture(), spawnAreaId.getMaskTexture(), ItemParticlesClient.CURRENT_STACK, id);
			}
			return pos;
		};
	}
}
