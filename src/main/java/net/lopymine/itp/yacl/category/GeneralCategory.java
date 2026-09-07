package net.lopymine.itp.yacl.category;

import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.config.sub.*;
import net.lopymine.itp.config.sub.ItemParticlesCacheConfig.CacheInvalidateMode;
import net.lopymine.itp.config.sub.ItemParticlesFamilyGenerationConfig.Mode;
import net.lopymine.itp.family.cache.FamilyParticlesCacheManager;
import net.lopymine.mossylib.yacl.api.*;
import net.lopymine.mossylib.yacl.extension.SimpleOptionExtension;

@ExtensionMethod(SimpleOptionExtension.class)
public class GeneralCategory {

	public static SimpleCategory get(ItemParticlesConfig defConfig, ItemParticlesConfig config) {
		return SimpleCategory.startBuilder("general")
				.groups(getMainGroup(defConfig.getMainConfig(), config.getMainConfig()))
				.groups(getVisualGroup(defConfig.getParticleConfig(), config.getParticleConfig()))
				.groups(getFamilyGenerationGroup(defConfig.getFamilyGenerationConfig(), config.getFamilyGenerationConfig()))
				.groups(getCacheGroup(defConfig.getCacheConfig(), config.getCacheConfig()));
	}

	private static SimpleGroup getFamilyGenerationGroup(ItemParticlesFamilyGenerationConfig defConfig, ItemParticlesFamilyGenerationConfig config) {
		return SimpleGroup.startBuilder("family_generation").options(
				SimpleOption.<Mode>startBuilder("family_generation_mods_mode")
						.withBinding(defConfig.getModsMode(), config::getModsMode, config::setModsMode, true)
						.withController(Mode.class)
						.withDescription(SimpleContent.NONE),
				SimpleOption.<String>startBuilder("family_generation_mods")
						.withBinding(defConfig.getMods(), config::getMods, config::setMods, true)
						.withController()
						.withDescription(SimpleContent.NONE),
				SimpleOption.<Mode>startBuilder("family_generation_items_mode")
						.withBinding(defConfig.getItemsMode(), config::getItemsMode, config::setItemsMode, true)
						.withController(Mode.class)
						.withDescription(SimpleContent.NONE),
				SimpleOption.<String>startBuilder("family_generation_items")
						.withBinding(defConfig.getItems(), config::getItems, config::setItems, true)
						.withController()
						.withDescription(SimpleContent.NONE)
//				,SimpleOption.<Float>startBuilder("123")
//						.withBinding(0.005F, () -> (float) AbstractItemParticle.HAND_DEPTH_BIAS, (d) -> AbstractItemParticle.HAND_DEPTH_BIAS = d, true)
//						.withController(-1.0f, 1f, 0.01f, true)
//						.withDescription(SimpleContent.NONE),
//				SimpleOption.<Float>startBuilder("456")
//						.withBinding(0.0F, () -> (float) AbstractItemParticle.FIRST_PERSON_FACING_OFFSET, (d) -> AbstractItemParticle.FIRST_PERSON_FACING_OFFSET = d, true)
//						.withController(-1.0f, 10.0f, 0.05f, true)
//						.withDescription(SimpleContent.NONE)
		);
	}

	private static SimpleGroup getMainGroup(ItemParticlesMainConfig defConfig, ItemParticlesMainConfig config) {
		return SimpleGroup.startBuilder("main").options(
				SimpleOption.<Boolean>startBuilder("mod_enabled")
						.withBinding(defConfig.isModEnabled(), config::isModEnabled, config::setModEnabled, false)
						.withController()
						.withDescription(SimpleContent.NONE),
				SimpleOption.<Boolean>startBuilder("debug_mode_enabled")
						.withBinding(defConfig.isDebugModeEnabled(), config::isDebugModeEnabled, config::setDebugModeEnabled, false)
						.withController()
						.withDescription(SimpleContent.NONE),
				SimpleOption.<Boolean>startBuilder("nbt_debug_mode_enabled")
						.withBinding(defConfig.isNbtDebugModeEnabled(), config::isNbtDebugModeEnabled, config::setNbtDebugModeEnabled, false)
						.withController()
						.withDescription(SimpleContent.NONE)
		);
	}

	private static SimpleGroup getVisualGroup(ItemParticleConfig defConfig, ItemParticleConfig config) {
		return SimpleGroup.startBuilder("visual").options(
				SimpleOption.<Double>startBuilder("particles_transparency")
						.withBinding(defConfig.getParticleTransparency(), config::getParticleTransparency, config::setParticleTransparency, true)
						.withController(0.0D, 1.0D, 0.05D)
						.withDescription(SimpleContent.NONE)
		);
	}

	private static SimpleGroup getCacheGroup(ItemParticlesCacheConfig defConfig, ItemParticlesCacheConfig config) {
		return SimpleGroup.startBuilder("cache").options(
				SimpleOption.<CacheInvalidateMode>startBuilder("invalidate_mode")
						.withBinding(defConfig.getInvalidateMode(), config::getInvalidateMode, config::setInvalidateMode, true)
						.withController(CacheInvalidateMode.class)
						.withDescription(SimpleContent.NONE),
				SimpleOption.startButtonBuilder("invalidate_now", (screen, option) -> {
					FamilyParticlesCacheManager.deleteSilence();
				}).withDescription(SimpleContent.NONE)
		);
	}

}
