package net.lopymine.itp.element.spawner;

import com.mojang.serialization.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public record AdvancedSpawnAreas(Map<Identifier, AdvancedSpawnAreaId> areas) implements IParticleSpawnArea {

	public static final AdvancedSpawnAreas EMPTY = new AdvancedSpawnAreas(new HashMap<>());
	public static final AdvancedSpawnAreas FULL = new AdvancedSpawnAreas(new HashMap<>());

	public static final Codec<AdvancedSpawnAreas> CODEC = Codec.unboundedMap(Identifier.CODEC, Codec.STRING).comapFlatMap((map) -> {
		Map<Identifier, AdvancedSpawnAreaId> areas = new HashMap<>();

		for (Entry<Identifier, String> entry : map.entrySet()) {
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

	public Function<Identifier, @Nullable IParticleSpawnPos> getRandomPosFunction(RandomSource random) {
		return (id) -> {
			AdvancedSpawnAreaId spawnAreaId = this.areas.get(id);
			if (spawnAreaId == null) {
				return null;
			}
			AdvancedSpawnArea area = spawnAreaId.getArea();
			return area == null ? null : area.getRandomPosFunction(random).apply(id);
		};
	}
}
