package net.lopymine.itp.element.spawner;

import java.util.List;
import java.util.function.Function;
import net.lopymine.itp.t2o.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public record AdvancedSpawnArea(IParticleSpawnPos[] positions) implements IParticleSpawnArea {

	public static AdvancedSpawnArea createFull() {
		return new AdvancedSpawnArea(new IParticleSpawnPos[]{AdvancedSpawnPos.FULL_POS});
	}

	public static AdvancedSpawnArea readFromTexture(@Nullable Identifier texture, @NotNull Identifier mask) {
		List<AdvancedSpawnPos> positions = Texture2ObjectsManager.readFromTexture(
				mask,
				"advanced spawn area",
				Texture2ObjectPixelFilter.NOT_TRANSPARENT,
				(x, y, width, height, color) -> new AdvancedSpawnPos(texture, x, y)
		);

		return new AdvancedSpawnArea(positions.toArray(AdvancedSpawnPos[]::new));
	}

	public boolean isEmpty() {
		return this.positions.length == 0;
	}

	public Function<Identifier, @Nullable IParticleSpawnPos> getRandomPosFunction(RandomSource random) {
		return (ignored) -> {
			if (this.isEmpty()) {
				return null;
			}
			return this.positions[random.nextInt(this.positions.length)];
		};
	}

}
