package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.itp.element.spawner.SpawnCategory;
import static com.mojang.serialization.Codec.DOUBLE;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mossylib.utils.CodecUtils.option;
import static net.lopymine.mossylib.utils.CodecUtils.parseNewInstanceHacky;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticlesCoefficientsConfig {

	public static final Codec<ItemParticlesCoefficientsConfig> CODEC = create((instance) -> instance.group(
			option("global_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getGlobalConfig),
			option("first_person_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getFirstPersonConfig),
			option("third_person_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getThirdPersonConfig),
			option("dropped_item_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getDroppedItemConfig),
			option("item_frame_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getItemFrameConfig),
			option("shelf_config", ParticleCoefficientConfig.getNewInstance(), ParticleCoefficientConfig.CODEC, ItemParticlesCoefficientsConfig::getShelfConfig)
	).apply(instance, ItemParticlesCoefficientsConfig::new));

	private ParticleCoefficientConfig globalConfig;
	private ParticleCoefficientConfig firstPersonConfig;
	private ParticleCoefficientConfig thirdPersonConfig;
	private ParticleCoefficientConfig droppedItemConfig;
	private ParticleCoefficientConfig itemFrameConfig;
	private ParticleCoefficientConfig shelfConfig;

	public static Supplier<ItemParticlesCoefficientsConfig> getNewInstance() {
		return () -> parseNewInstanceHacky(CODEC);
	}

	public ParticleCoefficientConfig getConfig(SpawnCategory category) {
		return switch (category) {
			case FIRST_PERSON -> this.firstPersonConfig;
			case THIRD_PERSON -> this.thirdPersonConfig;
			case DROPPED_ITEM -> this.droppedItemConfig;
			case ITEM_FRAME -> this.itemFrameConfig;
			case SHELF -> this.shelfConfig;
		};
	}

	public double getCountCoefficient(SpawnCategory category) {
		return this.globalConfig.getCountCoefficient() * this.getConfig(category).getCountCoefficient();
	}

	public double getCooldownCoefficient(SpawnCategory category) {
		return this.globalConfig.getCooldownCoefficient() * this.getConfig(category).getCooldownCoefficient();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ParticleCoefficientConfig {

		public static final Codec<ParticleCoefficientConfig> CODEC = create((instance) -> instance.group(
				option("count_coefficient", 1.0D, DOUBLE, ParticleCoefficientConfig::getCountCoefficient),
				option("cooldown_coefficient", 1.0D, DOUBLE, ParticleCoefficientConfig::getCooldownCoefficient)
		).apply(instance, ParticleCoefficientConfig::new));

		private double countCoefficient;
		private double cooldownCoefficient;

		public static Supplier<ParticleCoefficientConfig> getNewInstance() {
			return () -> parseNewInstanceHacky(CODEC);
		}

	}
}
