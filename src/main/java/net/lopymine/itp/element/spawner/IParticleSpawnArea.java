package net.lopymine.itp.element.spawner;

import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public interface IParticleSpawnArea {

	boolean isEmpty();

	Function<Identifier, @Nullable IParticleSpawnPos> getRandomPosFunction(RandomSource random);

}
