package net.lopymine.itp.element.color;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IColorProvider {

	Map<String, ParticleColorTypeFactory> FACTORIES = Map.of(
			"nbt", (s) -> new NbtColorProvider(),
			"nbt_list", (s) -> new NbtListColorProvider()
	);

	Codec<IColorProvider> CODEC = Codec.STRING.xmap(IColorProvider::parse, IColorProvider::asString);

	private static IColorProvider parse(String s) {
		if (s.startsWith("#")) {
			return new CustomColorProvider(s);
		}
		ParticleColorTypeFactory factory = FACTORIES.get(s);
		if (factory == null) {
			return new StandardColorProvider();
		}
		return factory.create(s);
	}

	default void compile(ItemStack stack, RandomSource random) {
	}

	int tick(RandomSource random);

	String asString();

	default String getString(int color) {
		return this.asString() + "[" + color + "]";
	}

	IColorProvider copy();

	interface ParticleColorTypeFactory {

		@NotNull IColorProvider create(String s);

	}

}
