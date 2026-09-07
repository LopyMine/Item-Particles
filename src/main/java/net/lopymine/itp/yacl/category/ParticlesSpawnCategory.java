package net.lopymine.itp.yacl.category;

import dev.isxander.yacl3.api.Option;
import java.util.Arrays;
import java.util.function.*;
import java.util.stream.Stream;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.config.sub.*;
import net.lopymine.itp.config.sub.ItemParticlesCoefficientsConfig.ParticleCoefficientConfig;
import net.lopymine.itp.config.sub.ItemParticlesItemWhitelistsConfig.*;
import net.lopymine.mossylib.yacl.api.*;
import net.lopymine.mossylib.yacl.extension.SimpleOptionExtension;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(SimpleOptionExtension.class)
public class ParticlesSpawnCategory {

	public static SimpleCategory get(ItemParticlesConfig defConfig, ItemParticlesConfig config) {
		ItemParticlesCoefficientsConfig dc = defConfig.getCoefficientsConfig();
		ItemParticleConfig dp = defConfig.getParticleConfig();
		ItemParticlesItemWhitelistsConfig dw = defConfig.getWhitelistsConfig();

		ItemParticlesCoefficientsConfig c = config.getCoefficientsConfig();
		ItemParticleConfig p = config.getParticleConfig();
		ItemParticlesItemWhitelistsConfig w = config.getWhitelistsConfig();

		SimpleCategory category = SimpleCategory.startBuilder("particles_spawn");

		createSection(
				category,
				"global",
				dc.getGlobalConfig(), c.getGlobalConfig(),
				null, null,
				null, null, null
		);

		createSection(
				category,
				"first_person",
				dc.getFirstPersonConfig(), c.getFirstPersonConfig(),
				dw.getFirstPersonConfig(), w.getFirstPersonConfig(),
				dp.isFirstPersonSpawnEnabled(), p::isFirstPersonSpawnEnabled, p::setFirstPersonSpawnEnabled
		);

		createSection(
				category,
				"third_person",
				dc.getThirdPersonConfig(), c.getThirdPersonConfig(),
				dw.getThirdPersonConfig(), w.getThirdPersonConfig(),
				dp.isThirdPersonSpawnEnabled(), p::isThirdPersonSpawnEnabled, p::setThirdPersonSpawnEnabled
		);

		createSection(
				category,
				"dropped_item",
				dc.getDroppedItemConfig(), c.getDroppedItemConfig(),
				dw.getDroppedItemConfig(), w.getDroppedItemConfig(),
				dp.isDroppedItemSpawnEnabled(), p::isDroppedItemSpawnEnabled, p::setDroppedItemSpawnEnabled
		);

		createSection(
				category,
				"item_frame",
				dc.getItemFrameConfig(), c.getItemFrameConfig(),
				dw.getItemFrameConfig(), w.getItemFrameConfig(),
				dp.isItemFrameSpawnEnabled(), p::isItemFrameSpawnEnabled, p::setItemFrameSpawnEnabled
		);

		createSection(
				category,
				"shelf",
				dc.getShelfConfig(), c.getShelfConfig(),
				dw.getShelfConfig(), w.getShelfConfig(),
				dp.isShelfSpawnEnabled(), p::isShelfSpawnEnabled, p::setShelfSpawnEnabled
		);

		return category;
	}

	public static void createSection(
			SimpleCategory category,
			String id,
			ParticleCoefficientConfig defConfig, ParticleCoefficientConfig config,
			@Nullable ParticlesItemWhitelistConfig defWhitelistConfig, @Nullable ParticlesItemWhitelistConfig whitelistConfig,
			@Nullable Boolean defValue, @Nullable Supplier<Boolean> getter, @Nullable Consumer<Boolean> setter
	) {

		Option<?>[] coefficientsOptions = createCoefficientsOptions(defConfig, config);
		Option<?>[] whitelistOptions = defWhitelistConfig != null && whitelistConfig != null
				? createWhitelistOptions(defWhitelistConfig, whitelistConfig) : new Option[0];

		createSection(category, id, defValue, getter, setter, coefficientsOptions, whitelistOptions);
	}

	public static void createSection(
			SimpleCategory category,
			String id,
			@Nullable Boolean defValue, @Nullable Supplier<Boolean> getter, @Nullable Consumer<Boolean> setter,
			Option<?>[]... options
	) {
		createSection(category, id, defValue, getter, setter, Stream.of(options).flatMap(Arrays::stream).toArray(Option[]::new));
	}

	public static void createSection(
			SimpleCategory category,
			String id,
			@Nullable Boolean defValue, @Nullable Supplier<Boolean> getter, @Nullable Consumer<Boolean> setter,
			Option<?>... options
	) {

		Option<Boolean> option = defValue != null && getter != null && setter != null
				? createToggle(defValue, getter, setter, options) : null;

		createSection(category, id, option, options);
	}

	public static void createSection(
			SimpleCategory category,
			String id,
			@Nullable Option<?> toggleOption,
			Option<?>... options
	) {
		SimpleGroup group = SimpleGroup.startBuilder(id + "_section");

		if (toggleOption != null) {
			group.options(toggleOption);
		}
		if (options.length > 0) {
			group.options(options);
		}

		category.groups(group);
	}

	private static Option<Boolean> createToggle(boolean defValue, Supplier<Boolean> getter, Consumer<Boolean> setter, Option<?>... dependents) {
		var builder = SimpleOption.<Boolean>startBuilder("spawn_enabled")
				.withBinding(defValue, getter, setter, true)
				.withController()
				.withDescription(SimpleContent.NONE);

		if (dependents != null && dependents.length > 0) {
			builder.custom((b) -> {
				b.addListener((o, e) -> {
					boolean value = Boolean.TRUE.equals(getter.get());
					for (Option<?> option : dependents) {
						option.setAvailable(value);
					}
				});
			});
		}

		return builder.build(ItemParticles.MOD_ID);
	}

	private static Option<?>[] createCoefficientsOptions(ParticleCoefficientConfig defConfig, ParticleCoefficientConfig config) {
		return new Option[]{
				SimpleOption.<Double>startBuilder("count_coefficient")
						.withBinding(defConfig.getCountCoefficient(), config::getCountCoefficient, config::setCountCoefficient, true)
						.withController(0.0D, 50D, 0.1D, false)
						.withDescription(SimpleContent.NONE)
						.build(ItemParticles.MOD_ID),
				SimpleOption.<Double>startBuilder("cooldown_coefficient")
						.withBinding(defConfig.getCooldownCoefficient(), config::getCooldownCoefficient, config::setCooldownCoefficient, true)
						.withController(0.0D, 50D, 0.1D, false)
						.withDescription(SimpleContent.NONE)
						.build(ItemParticles.MOD_ID)
		};
	}

	private static Option<?>[] createWhitelistOptions(ParticlesItemWhitelistConfig defConfig, ParticlesItemWhitelistConfig config) {
		return new Option[]{
				SimpleOption.<Mode>startBuilder("whitelist_mode")
						.withBinding(defConfig.getMode(), config::getMode, config::setMode, true)
						.withController(Mode.class)
						.withDescription(SimpleContent.NONE)
						.build(ItemParticles.MOD_ID),
				SimpleOption.<String>startBuilder("whitelist_items")
						.withBinding(defConfig.getItems(), config::getItems, config::setItems, true)
						.withController()
						.withDescription(SimpleContent.NONE)
						.build(ItemParticles.MOD_ID)
		};
	}

}
