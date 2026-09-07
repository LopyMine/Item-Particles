package net.lopymine.itp.element.color.advanced;

import java.util.Map;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public interface IAdvancedColorProvider {


	Map<String, AdvancedParticleColorTypeModeFactory> FACTORIES = Map.of(
			"random", (s, speed) -> new AdvancedColorProviderRandom(speed),
			"random_static", (s, speed) -> new AdvancedColorProviderRandomStatic(),
			"gradient", (s, speed) -> new AdvancedColorProviderGradient(speed),
			"gradient_random_static", (s, speed) -> new AdvancedColorProviderGradientRandomStatic(speed),
			"gradient_loop", (s, speed) -> new AdvancedColorProviderGradientLoop(speed),
			"gradient_bounce", (s, speed) -> new AdvancedColorProviderGradientBounce(speed),
			"mixed", (s, speed) -> new AdvancedColorProviderMixed()
	);

	static IAdvancedColorProvider parse(String s, int speed) {
		AdvancedParticleColorTypeModeFactory factory = FACTORIES.get(s);
		if (factory == null) {
			return new AdvancedColorProviderRandomStatic();
		}
		return factory.create(s, speed);
	}

	int tickResolve(Integer[] compiledColors, RandomSource random);

	String asString();

	IAdvancedColorProvider copy();

	interface AdvancedParticleColorTypeModeFactory {

		@NotNull IAdvancedColorProvider create(String s, int speed);

	}

}
